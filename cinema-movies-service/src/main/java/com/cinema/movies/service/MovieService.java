package com.cinema.movies.service;

import com.cinema.movies.dto.BookingDTO;
import com.cinema.movies.dto.MoviePopularityResponseDTO;
import com.cinema.movies.dto.MovieWithBookingsResponseDTO;
import com.cinema.movies.entity.Movie;

import java.util.List;

public interface MovieService {

    Movie createMovie(Movie movie);

    Movie getMovieById(Long id);

    Movie updateMovie(Long id, Movie movie);

    void deleteMovie(Long id);

    List<Movie> getAllMovies();

    List<Movie> searchMoviesByTitle(String title);

    List<Movie> filterMoviesByGenre(String genre);

    List<Movie> sortMoviesByRating(String order);

    /**
     * Retrieves a movie with its bookings from the bookings service.
     */
    MovieWithBookingsResponseDTO getMovieWithBookings(Long movieId);

    /**
     * Marks a movie as popular based on its booking count.
     */
    MoviePopularityResponseDTO markMovieAsPopular(Long movieId);
}
