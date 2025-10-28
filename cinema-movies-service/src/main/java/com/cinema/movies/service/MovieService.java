package com.cinema.movies.service;

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
}
