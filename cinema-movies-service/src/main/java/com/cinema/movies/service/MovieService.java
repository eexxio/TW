package com.cinema.movies.service;

import com.cinema.movies.dto.BookingDTO;
import com.cinema.movies.dto.MoviePopularityResponseDTO;
import com.cinema.movies.dto.MovieWithBookingsResponseDTO;
import com.cinema.movies.entity.Movie;

import java.util.List;

/**
 * Service interface for managing movie-related business logic.
 * Provides operations for CRUD, search, filtering, sorting,
 * and cross-microservice communication with the bookings service.
 *
 * @author Tudor
 */
public interface MovieService {

    /**
     * Creates a new movie in the database.
     *
     * @param movie the movie entity to be created
     * @return the saved movie with generated ID and timestamps
     */
    Movie createMovie(Movie movie);

    /**
     * Retrieves a movie by its unique identifier.
     *
     * @param id the unique identifier of the movie
     * @return the movie entity
     * @throws com.cinema.movies.exception.ResourceNotFoundException if movie not found
     */
    Movie getMovieById(Long id);

    /**
     * Updates an existing movie's information.
     *
     * @param id    the unique identifier of the movie to update
     * @param movie the movie entity containing updated information
     * @return the updated movie entity
     * @throws com.cinema.movies.exception.ResourceNotFoundException if movie not found
     */
    Movie updateMovie(Long id, Movie movie);

    /**
     * Deletes a movie from the database.
     *
     * @param id the unique identifier of the movie to delete
     * @throws com.cinema.movies.exception.ResourceNotFoundException if movie not found
     */
    void deleteMovie(Long id);

    /**
     * Retrieves all movies from the database.
     *
     * @return list of all movies, empty list if no movies exist
     */
    List<Movie> getAllMovies();

    /**
     * Searches for movies by title using case-insensitive partial matching.
     *
     * @param title the title or partial title to search for
     * @return list of movies matching the search criteria, empty if no matches
     */
    List<Movie> searchMoviesByTitle(String title);

    /**
     * Filters movies by genre using case-insensitive exact matching.
     *
     * @param genre the genre to filter by
     * @return list of movies in the specified genre, empty if no matches
     */
    List<Movie> filterMoviesByGenre(String genre);

    /**
     * Sorts movies by rating in the specified order.
     * Movies with null ratings are treated as having a rating of 0.0.
     *
     * @param order the sort order ("asc" for ascending, "desc" for descending)
     * @return list of movies sorted by rating
     */
    List<Movie> sortMoviesByRating(String order);

    /**
     * Retrieves a movie with its associated bookings from the bookings microservice.
     * This is a cross-service operation that combines data from movies and bookings services.
     *
     * @param movieId the unique identifier of the movie
     * @return DTO containing movie information and list of bookings
     * @throws com.cinema.movies.exception.ResourceNotFoundException if movie not found
     */
    MovieWithBookingsResponseDTO getMovieWithBookings(Long movieId);

    /**
     * Evaluates and marks a movie as popular based on its booking count.
     * A movie is considered popular if it has at least 5 bookings.
     * This operation queries the bookings microservice for booking count.
     *
     * @param movieId the unique identifier of the movie
     * @return DTO containing movie information, booking count, and popularity status
     * @throws com.cinema.movies.exception.ResourceNotFoundException if movie not found
     */
    MoviePopularityResponseDTO markMovieAsPopular(Long movieId);
}
