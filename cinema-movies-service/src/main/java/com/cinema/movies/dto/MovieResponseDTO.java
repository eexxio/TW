package com.cinema.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDTO {

    private Long id;

    private String title;

    private String description;

    private String genre;

    private Integer duration;

    private String director;

    private LocalDate releaseDate;

    private Double rating;

    private String posterUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
