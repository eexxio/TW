package com.cinema.movies.service;

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

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
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
}
