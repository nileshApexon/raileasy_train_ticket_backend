# RailEasy Backend API

A Spring Boot REST API for train ticket booking with user authentication, schedule management, and admin controls.

## Tech Stack

- **Java 17+**
- **Spring Boot 3.3.5**
- **Spring Data JPA with Hibernate**
- **Spring Security with JWT Authentication**
- **Flyway Database Migration**
- **PostgreSQL / H2 (dev)**
- **SpringDoc OpenAPI (Swagger UI)**
- **JJWT (JSON Web Token library)**

## Setup & Running

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 12+ (for production) or H2 (for development)

### 1. Clone & Build
```bash
git clone <repo-url>
cd backend
mvn clean install
```

### 2. Run Development (H2 In-Memory DB)
```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

**Access H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:raileasy`
- Username: `sa`
- Password: (leave blank)

### 3. Run Production (PostgreSQL)
```bash
export DB_URL=jdbc:postgresql://localhost:5432/raileasy
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

### 4. Access API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

---

## API Endpoints

### Authentication

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123",
  "name": "John Doe"
}

Response: 201 Created
{
  "userId": "uuid",
  "email": "user@example.com",
  "name": "John Doe",
  "isAdmin": false,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Note:** The `token` field contains a JWT token that must be included in the `Authorization` header for authenticated requests.

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123"
}

Response: 200 OK
{
  "userId": "uuid",
  "email": "user@example.com",
  "name": "John Doe",
  "isAdmin": false,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Logout
```http
POST /api/auth/logout

Response: 204 No Content
```

### Train Search

#### Search Schedules
```http
GET /api/schedules?from=Chennai%20Central&to=Mumbai%20CSMT&date=2025-10-21

Response: 200 OK
[
  {
    "scheduleId": "uuid",
    "trainNumber": "12163",
    "trainName": "Chennai Express",
    "fromStation": "Chennai Central",
    "toStation": "Mumbai CSMT",
    "journeyDate": "2025-10-21",
    "departureTime": "2025-10-21T06:00:00",
    "arrivalTime": "2025-10-22T05:30:00",
    "classAvailability": [
      {
        "travelClass": "SLEEPER",
        "availableSeats": 60,
        "fare": 450.00
      },
      {
        "travelClass": "AC_3",
        "availableSeats": 62,
        "fare": 1200.00
      },
      {
        "travelClass": "AC_2",
        "availableSeats": 64,
        "fare": 1800.00
      }
    ]
  }
]
```

#### Get Booked Seats
```http
GET /api/schedules/{scheduleId}/seats?class=AC_3

Response: 200 OK
{
  "scheduleId": "uuid",
  "travelClass": "AC_3",
  "totalSeats": 64,
  "bookedSeats": ["1A", "1B", "2A"]
}
```

### Bookings

#### Create Booking (Authenticated)
```http
POST /api/bookings
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
  "scheduleId": "uuid",
  "travelClass": "AC_3",
  "seatNumbers": ["3A", "3B"]
}

Response: 201 Created
{
  "bookingId": "uuid",
  "pnrNumber": "A1B2C3D4",
  "status": "CONFIRMED",
  "travelClass": "AC_3",
  "seatNumbers": ["3A", "3B"],
  "scheduleId": "uuid",
  "trainNumber": "12163",
  "trainName": "Chennai Express",
  "fromStation": "Chennai Central",
  "toStation": "Mumbai CSMT",
  "journeyDate": "2025-10-21",
  "departureTime": "2025-10-21T06:00:00",
  "arrivalTime": "2025-10-22T05:30:00"
}
```

#### Get My Bookings (Authenticated)
```http
GET /api/bookings/mine
Authorization: Bearer <jwt-token>

Response: 200 OK
[
  {
    "bookingId": "uuid",
    "pnrNumber": "A1B2C3D4",
    "status": "CONFIRMED",
    ...
  }
]
```

#### Cancel Booking (Authenticated)
```http
PUT /api/bookings/{bookingId}/cancel
Authorization: Bearer <jwt-token>

Response: 200 OK
{
  "bookingId": "uuid",
  "pnrNumber": "A1B2C3D4",
  "status": "CANCELLED",
  ...
}
```

### Admin - Trains

#### Get All Trains (Admin)
```http
GET /api/trains
Authorization: Bearer <admin-jwt-token>

Response: 200 OK
[
  {
    "trainId": "uuid",
    "trainNumber": "12163",
    "trainName": "Chennai Express",
    "seatsPerClass": 64
  }
]
```

#### Create Train (Admin)
```http
POST /api/trains
Content-Type: application/json
Authorization: Bearer <admin-jwt-token>

{
  "trainNumber": "12345",
  "trainName": "New Express",
  "seatsPerClass": 64
}

Response: 201 Created
```

#### Update Train (Admin)
```http
PUT /api/trains/{trainId}
Content-Type: application/json
Authorization: Bearer <admin-jwt-token>

{
  "trainNumber": "12345",
  "trainName": "Updated Express",
  "seatsPerClass": 80
}

Response: 200 OK
```

#### Delete Train (Admin)
```http
DELETE /api/trains/{trainId}
Authorization: Bearer <admin-jwt-token>

Response: 204 No Content
```

### Admin - Schedules

#### Get All Schedules (Admin)
```http
GET /api/schedules/admin
Authorization: Bearer <admin-jwt-token>

Response: 200 OK
[
  {
    "scheduleId": "uuid",
    "trainId": "uuid",
    "trainNumber": "12163",
    "trainName": "Chennai Express",
    "fromStation": "Chennai Central",
    "toStation": "Mumbai CSMT",
    "departureTime": "2025-10-21T06:00:00",
    "arrivalTime": "2025-10-22T05:30:00",
    "journeyDate": "2025-10-21",
    "fareSleeper": 450.00,
    "fareAc3": 1200.00,
    "fareAc2": 1800.00
  }
]
```

#### Create Schedule (Admin)
```http
POST /api/schedules
Content-Type: application/json
Authorization: Bearer <admin-jwt-token>

{
  "trainId": "uuid",
  "fromStation": "Bangalore",
  "toStation": "Delhi",
  "departureTime": "2025-11-01T08:00:00",
  "arrivalTime": "2025-11-01T20:00:00",
  "journeyDate": "2025-11-01",
  "fareSleeper": 500.00,
  "fareAc3": 1350.00,
  "fareAc2": 2000.00
}

Response: 201 Created
```

#### Update Schedule (Admin)
```http
PUT /api/schedules/{scheduleId}
Content-Type: application/json
Authorization: Bearer <admin-jwt-token>

{ ... same fields ... }

Response: 200 OK
```

#### Delete Schedule (Admin)
```http
DELETE /api/schedules/{scheduleId}
Authorization: Bearer <admin-jwt-token>

Response: 204 No Content
```

---

## Seed Data (Default)

### Admin User
- Email: `admin@raileasy.com`
- Password: `password` (use this to login)
- isAdmin: `true`

### Sample Trains
1. **Chennai Express** (12163) - 64 seats per class
2. **Rajdhani Express** (22691) - 64 seats per class

### Sample Schedules
- Chennai Central → Mumbai CSMT on 2025-10-21
- Multiple time slots with different fares per class

### Sample Bookings
- Admin user has confirmed and cancelled bookings for testing

---

## Error Response Format

All errors follow this format:

```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "path": "/api/bookings",
  "error": "VALIDATION_ERROR",
  "message": "Seat already booked: 1A"
}
```

### Common HTTP Status Codes
- `201 Created` - Resource created successfully
- `200 OK` - Request successful
- `204 No Content` - Request successful, no response body
- `400 Bad Request` - Invalid input or validation error
- `401 Unauthorized` - Missing or invalid credentials
- `403 Forbidden` - Authenticated but not authorized (admin required)
- `404 Not Found` - Resource not found
- `409 Conflict` - Seat already booked, duplicate email, etc.
- `500 Internal Server Error` - Server error

---

## Testing

### Unit Tests
```bash
mvn test
```

Run specific test:
```bash
mvn test -Dtest=ScheduleSearchServiceTest
```

### Manual Testing with cURL

#### Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@raileasy.com",
    "password": "password"
  }'
```

#### Search Trains
```bash
curl -X GET "http://localhost:8080/api/schedules?from=Chennai%20Central&to=Mumbai%20CSMT&date=2025-10-21"
```

#### Create Booking (Replace with real JWT token)
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "scheduleId": "uuid",
    "travelClass": "AC_3",
    "seatNumbers": ["4A", "4B"]
  }'
```

---

## Architecture

### Layered Structure
- **Controller Layer** (`controller/*`) - HTTP endpoints
- **Service Layer** (`service/*`) - Business logic
- **Repository Layer** (`repository/*`) - Data access
- **Domain Layer** (`domain/*`) - JPA entities
- **DTO Layer** (`dto/*`) - Request/Response objects
- **Config Layer** (`config/*`) - Spring beans

### Key Services
- **AuthService** - User registration and login
- **BookingService** - Create, retrieve, cancel bookings
- **ScheduleSearchService** - Search with real-time availability
- **ScheduleSeatMapService** - Get booked seats for a class
- **TrainAdminService** - CRUD for trains
- **ScheduleAdminService** - CRUD for schedules
- **UserAccessService** - Authentication and authorization

---

## Deployment

### Docker (Optional)
```bash
mvn clean package
docker build -t raileasy-backend .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://db:5432/raileasy \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=password \
  -e SPRING_PROFILES_ACTIVE=prod \
  raileasy-backend
```

### Environment Variables
```
SPRING_PROFILES_ACTIVE=dev|prod
DB_URL=jdbc:postgresql://host:5432/raileasy
DB_USERNAME=postgres
DB_PASSWORD=password
JWT_SECRET=your-256-bit-secret-key-for-jwt-token-generation
JWT_EXPIRATION_MS=86400000
```

---

## Performance Notes

- All schedule searches indexed on (from_station, to_station, journey_date)
- Booking lookups optimized with composite index on (schedule_id, travel_class, status)
- EntityGraph used to prevent N+1 queries
- Transactional boundaries properly set

---

## Security

- Passwords hashed with BCrypt (strength 10)
- JWT token-based stateless authentication
- Bearer token in Authorization header for authenticated requests
- Admin authorization on sensitive endpoints using role-based access control
- Guest users can search schedules and view seat availability without authentication
- Input validation on all requests
- SQL injection prevention via parameterized queries (JPA)
- CORS configuration for cross-origin requests

---

## Future Enhancements

- Refresh token mechanism for JWT
- Waitlist functionality for fully booked classes
- Station autocomplete
- Downloadable ticket PDFs
- Real-time seat availability via WebSockets
- Fare calculation rules
- Payment gateway integration
- Email notifications
- Rate limiting on API
- API versioning (v1, v2, etc.)

---

## Contributing

1. Create feature branch: `git checkout -b feature/feature-name`
2. Commit changes: `git commit -m 'Add feature'`
3. Push to branch: `git push origin feature/feature-name`
4. Open Pull Request for review

---

## License

MIT
