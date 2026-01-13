package com.cinema.movies.dto;

import com.cinema.movies.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDTO requestDTO) {
        Movie movie = new Movie();
        movie.setTitle(requestDTO.getTitle());
        movie.setDescription(requestDTO.getDescription());
        movie.setGenre(requestDTO.getGenre());
        movie.setDuration(requestDTO.getDuration());
        movie.setDirector(requestDTO.getDirector());
        movie.setReleaseDate(requestDTO.getReleaseDate());
        movie.setRating(requestDTO.getRating());
        movie.setPosterUrl(requestDTO.getPosterUrl());
        return movie;
    }

    public MovieResponseDTO toResponseDTO(Movie movie) {
        MovieResponseDTO responseDTO = new MovieResponseDTO();
        responseDTO.setId(movie.getId());
        responseDTO.setTitle(movie.getTitle());
        responseDTO.setDescription(movie.getDescription());
        responseDTO.setGenre(movie.getGenre());
        responseDTO.setDuration(movie.getDuration());
        responseDTO.setDirector(movie.getDirector());
        responseDTO.setReleaseDate(movie.getReleaseDate());
        responseDTO.setRating(movie.getRating());
        responseDTO.setPosterUrl(movie.getPosterUrl());
        responseDTO.setCreatedAt(movie.getCreatedAt());
        responseDTO.setUpdatedAt(movie.getUpdatedAt());
        return responseDTO;
    }

    public List<MovieResponseDTO> toResponseDTOList(List<Movie> movies) {
        return movies.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
