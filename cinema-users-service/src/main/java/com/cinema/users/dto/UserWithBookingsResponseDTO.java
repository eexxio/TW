package com.cinema.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for returning user information along with their bookings from the bookings service.
 * This combines data from the users service and bookings service.
 * Used for the GET /api/users/{userId}/bookings endpoint.
 *
 * @author Alexandru Tesula
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithBookingsResponseDTO {

    private UserDTO user;
    private List<BookingDTO> bookings;
    private long totalBookings;
}
