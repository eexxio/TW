# Cinema App - Microservices Project

## Overview
A cinema management application built with microservices architecture.

**Tech Stack:** Java Spring Boot, PostgreSQL

## Microservices

### 1. Cinema Movies Service
Manages the movie catalog - CRUD operations, search, filter, and sort movies.

### 2. Cinema Users Service
Manages user accounts and authentication - registration, login, profile management.

### 3. Cinema Bookings Service
Manages ticket bookings and reservations - create bookings, view booking history, manage reservations.

## Project Structure
```
TW/
├── cinema-movies-service/
│   ├── src/main/java/com/cinema/movies/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── exception/
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── README.md
│
├── cinema-users-service/
│   ├── src/main/java/com/cinema/users/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── exception/
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── README.md
│
└── cinema-bookings-service/
    ├── src/main/java/com/cinema/bookings/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   ├── dto/
    │   └── exception/
    ├── src/main/resources/
    │   └── application.properties
    ├── pom.xml
    └── README.md
```

## Database Setup

All three microservices connect to the same PostgreSQL database: `cinema_db`

### Create Database
```sql
CREATE DATABASE cinema_db;
```
```sql
-- Table 1: movies (Movies Service)
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(100),
    duration INTEGER,
    director VARCHAR(255),
    release_date DATE,
    rating DECIMAL(3,1),
    poster_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: users (Users Service)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 3: bookings (Bookings Service)
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    movie_title VARCHAR(255),
    user_email VARCHAR(255),
    screening_time TIMESTAMP NOT NULL,
    seat_number INTEGER,
    seat_row VARCHAR(5),
    price DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Running the Services

Each microservice runs on a different port and can be started independently:

```bash
# Terminal 1 - Movies Service
cd cinema-movies-service
mvn spring-boot:run

# Terminal 2 - Users Service
cd cinema-users-service
mvn spring-boot:run

# Terminal 3 - Bookings Service
cd cinema-bookings-service
mvn spring-boot:run
```

## Configuration

Each service has its own `application.properties`:

- **Movies Service:** Port 8081
- **Users Service:** Port 8082
- **Bookings Service:** Port 8083

All services share the same database connection:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cinema_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```
