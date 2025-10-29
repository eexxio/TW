package com.cinema.movies.controller;

import com.cinema.movies.dto.MovieMapper;
import com.cinema.movies.dto.MovieRequestDTO;
import com.cinema.movies.dto.MovieResponseDTO;
import com.cinema.movies.entity.Movie;
import com.cinema.movies.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
