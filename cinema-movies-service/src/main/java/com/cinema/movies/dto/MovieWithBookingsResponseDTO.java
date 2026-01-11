package com.cinema.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for returning movie information along with its bookings.
 * This combines data from the movies service and bookings service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieWithBookingsResponseDTO {

    private MovieResponseDTO movie;
    private List<BookingDTO> bookings;
    private long totalBookings;
}
