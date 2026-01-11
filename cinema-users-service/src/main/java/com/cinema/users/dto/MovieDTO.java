package com.cinema.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing movie information from the movies service.
 * Used for cross-service communication between Users and Movies services.
 *
 * @author Alexandru Tesula
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

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

