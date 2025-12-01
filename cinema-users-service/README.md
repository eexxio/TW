# Cinema Users Service

## Description
Microservice responsible for managing user accounts and authentication.

## Port
8082

## Endpoints

### CRUD Operations
- `POST /api/users/register` - Register a new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user profile
- `DELETE /api/users/{id}` - Delete user

### Additional Endpoints
- `POST /api/users/login` - User login
- `GET /api/users/search?email={email}` - Search user by email
- `GET /api/users?role={role}` - Filter users by role

## Package Structure
```
com.cinema.users/
├── com.cinema.users.controller/     # REST controllers
├── com.cinema.users.service/        # Business logic (interface + implementation)
├── com.cinema.users.repository/     # Data access layer
├── com.cinema.users.entity/         # JPA entities
├── com.cinema.users.dto/            # Data Transfer Objects
└── exception/      # Custom exceptions and handlers
```

## Entity: User
- id (Long)
- firstName (String)
- lastName (String)
- email (String)
- passwordHash (String)
- phoneNumber (String)
- dateOfBirth (LocalDate)
- role (String) - USER, ADMIN
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

## Running the Service
```bash
cd cinema-users-com.cinema.users.service
mvn spring-boot:run
```
