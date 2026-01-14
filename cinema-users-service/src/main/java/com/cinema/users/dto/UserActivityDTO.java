package com.cinema.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for returning user activity aggregation.
 * Aggregates user details, bookings, and watched movies information
 * by calling multiple microservices (bookings and movies).
 *
 * @author Alexandru Tesula
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDTO {

    private UserDTO user;
    private List<BookingDTO> bookings;
    private List<MovieDTO> watchedMovies;
    private long totalBookings;
    private long totalMoviesWatched;
}

