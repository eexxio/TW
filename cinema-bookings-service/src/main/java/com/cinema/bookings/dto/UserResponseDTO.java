package com.cinema.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for receiving user information from users-service.
 * Used when making HTTP calls to the users microservice.
 *
 * @author Ioana-Loredana Stan - Viza 3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    /**
     * Unique identifier of the user
     */
    private Long id;

    /**
     * Username of the user
     */
    private String username;

    /**
     * Email address of the user
     */
    private String email;

    /**
     * First name of the user
     */
    private String firstName;

    /**
     * Last name of the user
     */
    private String lastName;

    /**
     * Phone number of the user
     */
    private String phoneNumber;

    /**
     * Role of the user (e.g., USER, ADMIN)
     */
    private String role;
}
