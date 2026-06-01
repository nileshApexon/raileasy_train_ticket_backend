param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

function Invoke-ApiJson {
    param(
        [Parameter(Mandatory = $true)][string]$Method,
        [Parameter(Mandatory = $true)][string]$Path,
        [object]$Body = $null,
        [string]$Token = $null
    )

    $headers = @{}
    if ($Token) {
        $headers["X-User-Id"] = $Token
    }

    $invokeArgs = @{
        Method = $Method
        Uri = "$BaseUrl$Path"
        Headers = $headers
        ContentType = "application/json"
    }

    if ($null -ne $Body) {
        $invokeArgs["Body"] = ($Body | ConvertTo-Json -Depth 6)
    }

    return Invoke-RestMethod @invokeArgs
}

function Assert-True {
    param(
        [bool]$Condition,
        [string]$Message
    )

    if (-not $Condition) {
        throw $Message
    }
}

Write-Host "[1/8] Register passenger"
$ts = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$passengerEmail = "smoke+$ts@raileasy.com"
$passengerPassword = "pass1234"

try {
    [void](Invoke-ApiJson -Method "POST" -Path "/api/auth/register" -Body @{
        email = $passengerEmail
        password = $passengerPassword
        name = "Smoke Passenger"
    })
} catch {
    # Ignore conflict in rare rerun collision.
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -ne 409) {
        throw
    }
}

Write-Host "[2/8] Login passenger"
$passengerLogin = Invoke-ApiJson -Method "POST" -Path "/api/auth/login" -Body @{
    email = $passengerEmail
    password = $passengerPassword
}
Assert-True ($passengerLogin.isAdmin -eq $false) "Passenger account should not be admin"
$passengerToken = $passengerLogin.token

Write-Host "[3/8] Login admin"
$adminLogin = Invoke-ApiJson -Method "POST" -Path "/api/auth/login" -Body @{
    email = "admin@raileasy.com"
    password = "password"
}
Assert-True ($adminLogin.isAdmin -eq $true) "Admin account should be admin"
$adminToken = $adminLogin.token

Write-Host "[4/8] Search schedules"
$schedules = Invoke-ApiJson -Method "GET" -Path "/api/schedules?from=Chennai%20Central&to=Mumbai%20CSMT&date=2025-10-21"
Assert-True ($schedules.Count -gt 0) "Expected at least one schedule for seed search"
$scheduleId = $schedules[0].scheduleId

Write-Host "[5/8] Create and cancel passenger booking"
$booking = Invoke-ApiJson -Method "POST" -Path "/api/bookings" -Token $passengerToken -Body @{
    scheduleId = $scheduleId
    travelClass = "SLEEPER"
    seatNumbers = @("P$ts")
}
Assert-True ($booking.status -eq "CONFIRMED") "Booking should be CONFIRMED"

$cancelled = Invoke-ApiJson -Method "PUT" -Path "/api/bookings/$($booking.bookingId)/cancel" -Token $passengerToken
Assert-True ($cancelled.status -eq "CANCELLED") "Booking should be CANCELLED"

Write-Host "[6/8] Admin create train + schedule"
$createdTrain = Invoke-ApiJson -Method "POST" -Path "/api/trains" -Token $adminToken -Body @{
    trainNumber = "9$($ts % 100000000)"
    trainName = "Smoke Express $ts"
    seatsPerClass = 64
}

$createdSchedule = Invoke-ApiJson -Method "POST" -Path "/api/schedules" -Token $adminToken -Body @{
    trainId = $createdTrain.id
    fromStation = "Pune"
    toStation = "Mumbai"
    departureTime = "2025-10-25T08:30:00"
    arrivalTime = "2025-10-25T11:45:00"
    journeyDate = "2025-10-25"
    fareSleeper = 450
    fareAc3 = 950
    fareAc2 = 1450
}

Write-Host "[7/8] Admin cleanup delete schedule + train"
Invoke-WebRequest -Method "DELETE" -Uri "$BaseUrl/api/schedules/$($createdSchedule.id)" -Headers @{"X-User-Id" = $adminToken } | Out-Null
Invoke-WebRequest -Method "DELETE" -Uri "$BaseUrl/api/trains/$($createdTrain.id)" -Headers @{"X-User-Id" = $adminToken } | Out-Null

Write-Host "[8/8] Role check passenger blocked from admin endpoint"
try {
    Invoke-WebRequest -Method "GET" -Uri "$BaseUrl/api/trains" -Headers @{"X-User-Id" = $passengerToken } | Out-Null
    throw "Expected 403 when passenger calls /api/trains"
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Assert-True ($statusCode -eq 403) "Expected 403 for passenger admin access, got $statusCode"
}

Write-Host "Smoke flow passed: auth + passenger booking/cancel + admin CRUD + role guard." -ForegroundColor Green

