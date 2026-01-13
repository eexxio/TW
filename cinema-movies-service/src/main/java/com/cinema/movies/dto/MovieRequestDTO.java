package com.cinema.movies.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String genre;

    private Integer duration;

    private String director;

    private LocalDate releaseDate;

    private Double rating;

    private String posterUrl;
}
