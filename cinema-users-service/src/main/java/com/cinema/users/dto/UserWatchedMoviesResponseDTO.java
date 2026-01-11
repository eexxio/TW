package com.cinema.users.dto;

import com.cinema.users.dto.MovieDTO;
import com.cinema.users.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for returning user watched movies information.
 * This combines data from users, bookings, and movies services.
 * Used for the GET /api/users/{userId}/watched-movies endpoint.
 *
 * @author Alexandru Tesula
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWatchedMoviesResponseDTO {

    private UserDTO user;
    private List<MovieDTO> watchedMovies;
    private long totalMoviesWatched;
}
