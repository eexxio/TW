# Cinema Bookings Service

## Description
Microservice responsible for managing ticket bookings and reservations.

## Port
8083

## Endpoints

### CRUD Operations
- `POST /api/bookings` - Create a new booking
- `GET /api/bookings/{id}` - Get booking by ID
- `PUT /api/bookings/{id}` - Update booking
- `DELETE /api/bookings/{id}` - Cancel booking

### Additional Endpoints
- `GET /api/bookings/user/{userId}` - Get all bookings for a user
- `GET /api/bookings/filter?status={status}` - Filter bookings by status
- `GET /api/bookings/sort?by=screeningTime&order=asc` - Sort bookings

## Package Structure
```
com.cinema.bookings/
├── controller/     # REST controllers
├── service/        # Business logic (interface + implementation)
├── repository/     # Data access layer
├── entity/         # JPA entities
├── dto/            # Data Transfer Objects
└── exception/      # Custom exceptions and handlers
```

## Entity: Booking
- id (Long)
- userId (Long) - Hardcoded reference to Users service
- movieId (Long) - Hardcoded reference to Movies service
- movieTitle (String) - Denormalized data
- userEmail (String) - Denormalized data
- screeningTime (LocalDateTime)
- seatNumber (Integer)
- seatRow (String)
- price (Double)
- status (String) - PENDING, CONFIRMED, CANCELLED
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

## Running the Service
```bash
cd cinema-bookings-service
mvn spring-boot:run
```
