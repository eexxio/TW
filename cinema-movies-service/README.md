# Cinema Movies Service

## Description
Microservice responsible for managing the movie catalog and information.

## Port
8081

## Endpoints

### CRUD Operations
- `POST /api/movies` - Create a new movie
- `GET /api/movies/{id}` - Get movie by ID
- `PUT /api/movies/{id}` - Update movie
- `DELETE /api/movies/{id}` - Delete movie

### Additional Endpoints
- `GET /api/movies/search?title={title}` - Search movies by title
- `GET /api/movies/filter?genre={genre}` - Filter movies by genre
- `GET /api/movies/sort?by=rating&order=desc` - Sort movies

## Package Structure
```
com.cinema.movies/
├── controller/     # REST controllers
├── service/        # Business logic (interface + implementation)
├── repository/     # Data access layer
├── entity/         # JPA entities
├── dto/            # Data Transfer Objects
└── exception/      # Custom exceptions and handlers
```

## Entity: Movie
- id (Long)
- title (String)
- description (String)
- genre (String)
- duration (Integer) - in minutes
- director (String)
- releaseDate (LocalDate)
- rating (Double)
- posterUrl (String)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

## Running the Service
```bash
cd cinema-movies-service
mvn spring-boot:run
```
