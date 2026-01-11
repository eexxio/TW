package com.cinema.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for receiving movie information from movies-service.
 * Used when making HTTP calls to the movies microservice.
 *
 * @author Ioana-Loredana Stan - Viza 3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDTO {

    /**
     * Unique identifier of the movie
     */
    private Long id;

    /**
     * Title of the movie
     */
    private String title;

    /**
     * Description or synopsis of the movie
     */
    private String description;

    /**
     * Genre of the movie (e.g., Action, Comedy, Drama)
     */
    private String genre;

    /**
     * Duration of the movie in minutes
     */
    private Integer duration;

    /**
     * Release date of the movie
     */
    private LocalDate releaseDate;

    /**
     * Director of the movie
     */
    private String director;
}
