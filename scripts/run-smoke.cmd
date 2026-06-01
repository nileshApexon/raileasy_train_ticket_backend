@echo off
setlocal
set BASE_URL=%1
if "%BASE_URL%"=="" set BASE_URL=http://localhost:8080
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0smoke-auth-booking-admin.ps1" -BaseUrl "%BASE_URL%"

