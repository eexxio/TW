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
├── com.cinema.users.controller/     # REST controllers
├── com.cinema.users.service/        # Business logic (interface + implementation)
├── com.cinema.users.repository/     # Data access layer
├── com.cinema.users.entity/         # JPA entities
├── com.cinema.users.dto/            # Data Transfer Objects
└── exception/      # Custom exceptions and handlers
```

## Entity: Booking
- id (Long)
- userId (Long) - Hardcoded reference to Users com.cinema.users.service
- movieId (Long) - Hardcoded reference to Movies com.cinema.users.service
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
cd cinema-bookings-com.cinema.users.service
mvn spring-boot:run
```
