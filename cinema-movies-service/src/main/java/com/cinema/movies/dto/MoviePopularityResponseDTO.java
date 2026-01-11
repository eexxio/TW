package com.cinema.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning movie popularity information based on booking count.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoviePopularityResponseDTO {

    private MovieResponseDTO movie;
    private long bookingCount;
    private boolean isPopular;
    private String message;
}
