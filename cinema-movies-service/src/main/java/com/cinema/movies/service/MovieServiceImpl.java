package com.cinema.movies.service;

import com.cinema.movies.client.BookingServiceClient;
import com.cinema.movies.dto.BookingDTO;
import com.cinema.movies.dto.MovieMapper;
import com.cinema.movies.dto.MoviePopularityResponseDTO;
import com.cinema.movies.dto.MovieWithBookingsResponseDTO;
import com.cinema.movies.entity.Movie;
import com.cinema.movies.exception.ResourceNotFoundException;
import com.cinema.movies.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final BookingServiceClient bookingServiceClient;
    private final MovieMapper movieMapper;

    // Threshold for marking a movie as popular
    private static final long POPULARITY_THRESHOLD = 5;

    public MovieServiceImpl(MovieRepository movieRepository,
                           BookingServiceClient bookingServiceClient,
                           MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.bookingServiceClient = bookingServiceClient;
        this.movieMapper = movieMapper;
    }

    @Override
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        Movie existingMovie = getMovieById(id);

        existingMovie.setTitle(movie.getTitle());
        existingMovie.setDescription(movie.getDescription());
        existingMovie.setGenre(movie.getGenre());
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setDirector(movie.getDirector());
        existingMovie.setReleaseDate(movie.getReleaseDate());
        existingMovie.setRating(movie.getRating());
        existingMovie.setPosterUrl(movie.getPosterUrl());

        return movieRepository.save(existingMovie);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Movie> filterMoviesByGenre(String genre) {
        return movieRepository.findByGenreIgnoreCase(genre);
    }

    @Override
    public List<Movie> sortMoviesByRating(String order) {
        List<Movie> movies = movieRepository.findAll();

        if ("desc".equalsIgnoreCase(order)) {
            return movies.stream()
                    .sorted((m1, m2) -> Double.compare(
                            m2.getRating() != null ? m2.getRating() : 0.0,
                            m1.getRating() != null ? m1.getRating() : 0.0))
                    .collect(Collectors.toList());
        } else {
            return movies.stream()
                    .sorted((m1, m2) -> Double.compare(
                            m1.getRating() != null ? m1.getRating() : 0.0,
                            m2.getRating() != null ? m2.getRating() : 0.0))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public MovieWithBookingsResponseDTO getMovieWithBookings(Long movieId) {
        // Get the movie from the database
        Movie movie = getMovieById(movieId);

        // Fetch bookings from the bookings service
        List<BookingDTO> bookings = bookingServiceClient.getBookingsByMovieId(movieId);

        // Create the response DTO
        MovieWithBookingsResponseDTO response = new MovieWithBookingsResponseDTO();
        response.setMovie(movieMapper.toResponseDTO(movie));
        response.setBookings(bookings);
        response.setTotalBookings(bookings.size());

        return response;
    }

    @Override
    public MoviePopularityResponseDTO markMovieAsPopular(Long movieId) {
        // Get the movie from the database
        Movie movie = getMovieById(movieId);

        // Get booking count from bookings service
        long bookingCount = bookingServiceClient.getBookingCountByMovieId(movieId);

        // Determine if the movie is popular based on threshold
        boolean isPopular = bookingCount >= POPULARITY_THRESHOLD;

        String message;
        if (isPopular) {
            message = String.format("Movie '%s' is popular with %d bookings (threshold: %d)",
                    movie.getTitle(), bookingCount, POPULARITY_THRESHOLD);
        } else {
            message = String.format("Movie '%s' has %d bookings, needs %d more to be popular",
                    movie.getTitle(), bookingCount, POPULARITY_THRESHOLD - bookingCount);
        }

        // Create the response
        MoviePopularityResponseDTO response = new MoviePopularityResponseDTO();
        response.setMovie(movieMapper.toResponseDTO(movie));
        response.setBookingCount(bookingCount);
        response.setPopular(isPopular);
        response.setMessage(message);

        return response;
    }
}
