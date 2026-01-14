package com.cinema.movies.controller;

import com.cinema.movies.dto.*;
import com.cinema.movies.entity.Movie;
import com.cinema.movies.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    public MovieController(MovieService movieService, MovieMapper movieMapper) {
        this.movieService = movieService;
        this.movieMapper = movieMapper;
    }

    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(@Valid @RequestBody MovieRequestDTO requestDTO) {
        Movie movie = movieMapper.toEntity(requestDTO);
        Movie createdMovie = movieService.createMovie(movie);
        MovieResponseDTO responseDTO = movieMapper.toResponseDTO(createdMovie);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        MovieResponseDTO responseDTO = movieMapper.toResponseDTO(movie);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequestDTO requestDTO) {
        Movie movie = movieMapper.toEntity(requestDTO);
        Movie updatedMovie = movieService.updateMovie(id, movie);
        MovieResponseDTO responseDTO = movieMapper.toResponseDTO(updatedMovie);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponseDTO>> searchMoviesByTitle(@RequestParam String title) {
        List<Movie> movies = movieService.searchMoviesByTitle(title);
        List<MovieResponseDTO> responseDTOs = movieMapper.toResponseDTOList(movies);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MovieResponseDTO>> filterMoviesByGenre(@RequestParam String genre) {
        List<Movie> movies = movieService.filterMoviesByGenre(genre);
        List<MovieResponseDTO> responseDTOs = movieMapper.toResponseDTOList(movies);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<MovieResponseDTO>> sortMoviesByRating(
            @RequestParam(defaultValue = "desc") String order) {
        List<Movie> movies = movieService.sortMoviesByRating(order);
        List<MovieResponseDTO> responseDTOs = movieMapper.toResponseDTOList(movies);
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Cross-service endpoint: Retrieves a movie with all its bookings.
     * This endpoint calls the bookings-service to fetch booking information.
     *
     * @param movieId the ID of the movie
     * @return movie information combined with booking data
     */
    @GetMapping("/{movieId}/bookings")
    public ResponseEntity<MovieWithBookingsResponseDTO> getMovieWithBookings(@PathVariable Long movieId) {
        MovieWithBookingsResponseDTO response = movieService.getMovieWithBookings(movieId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cross-service endpoint: Marks a movie as popular based on booking count.
     * This endpoint calls the bookings-service to get the booking count.
     *
     * @param movieId the ID of the movie
     * @return movie popularity information
     */
    @PostMapping("/{movieId}/popular")
    public ResponseEntity<MoviePopularityResponseDTO> markMovieAsPopular(@PathVariable Long movieId) {
        MoviePopularityResponseDTO response = movieService.markMovieAsPopular(movieId);
        return ResponseEntity.ok(response);
    }
}
