package com.cinema.users.service;

import com.cinema.users.dto.*;

import java.util.List;

/**
 * Service interface for user management operations.
 * Defines contract for user CRUD operations and activity aggregation.
 *
 * @author Alexandru Tesula
 */
public interface IUserService {
    /**
     * Creates a new user account.
     *
     * @param userDTO the user creation details
     * @return the created user as UserDTO
     * @author Alexandru Tesula
     */
    UserDTO createUser(UserCreateDTO userDTO);

    /**
     * Updates an existing user's information.
     *
     * @param email   the email identifier of the user to update
     * @param userDTO the updated user details
     * @return the updated user as UserDTO
     * @author Alexandru Tesula
     */
    UserDTO updateUser(String email, UserCreateDTO userDTO);

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users as UserDTO objects
     * @author Alexandru Tesula
     */
    List<UserDTO> getAllUsers();

    /**
     * Deletes a user account by email.
     *
     * @param email the email identifier of the user to delete
     * @return true if deletion was successful, false otherwise
     * @author Alexandru Tesula
     */
    boolean deleteUser(String email);

    /**
     * Authenticates a user with email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return true if credentials are valid, false otherwise
     * @author Alexandru Tesula
     */
    boolean login(String email, String password);

    /**
     * Searches users by first or last name.
     *
     * @param keyword the search keyword
     * @return a list of matching users as UserDTO objects
     * @author Alexandru Tesula
     */
    List<UserDTO> searchByName(String keyword);

    /**
     * Filters users by role-based access control.
     *
     * @param role the role to filter by
     * @return a list of users with the specified role as UserDTO objects
     * @author Alexandru Tesula
     */
    List<UserDTO> filterByRole(String role);

    /**
     * Sorts users by date of birth.
     *
     * @param direction the sort direction ("asc" or "desc")
     * @return a list of users sorted by date of birth as UserDTO objects
     * @author Alexandru Tesula
     */
    List<UserDTO> sortByDateOfBirth(String direction);

    /**
     * Aggregates user activity by retrieving user details along with bookings and movie information.
     * This method performs cross-service calls to aggregate data from bookings and movies services.
     *
     * @param userId the ID of the user whose activity should be aggregated
     * @return a UserActivityDTO containing user details, bookings, and watched movies
     * @author Alexandru Tesula
     */
    UserActivityDTO getUserActivity(Long userId);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user as UserDTO
     * @author Alexandru Tesula
     */
    UserDTO getUserById(Long userId);

    /**
     * Retrieves user with their booking history from the bookings microservice.
     * This is a cross-service operation that combines data from users and bookings services.
     * <p>
     * The method performs the following steps:
     * 1. Retrieves the user from the local database
     * 2. Calls the bookings microservice via BookingServiceClient
     * 3. Combines the data into a single response DTO
     * <p>
     * If the bookings service is unavailable, an empty bookings list is returned
     * with totalBookings set to 0, allowing graceful degradation.
     *
     * @param userId the unique identifier of the user
     * @return DTO containing user information and booking history
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    UserWithBookingsResponseDTO getUserWithBookings(Long userId);

    /**
     * Retrieves user with their watched movies list.
     * This is a cross-service operation that combines data from users, bookings, and movies services.
     *
     * The method performs the following steps:
     * 1. Retrieves the user from the local database
     * 2. Calls the bookings microservice to get all bookings for this user
     * 3. For each booking, calls the movies microservice to get movie details
     * 4. Combines all data into a single response DTO
     *
     * If external services are unavailable, partial data is returned gracefully.
     *
     * @param userId the unique identifier of the user
     * @return DTO containing user information and list of movies they've watched
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    UserWatchedMoviesResponseDTO getUserWatchedMovies(Long userId);

    /**
     * Upgrades a user account to premium status and applies discounts to existing bookings.
     * This is a cross-service operation that updates user status and calls the bookings service
     * to apply premium discounts to future and existing bookings.
     *
     * The method performs the following steps:
     * 1. Retrieves the user from the local database
     * 2. Updates the user role to PREMIUM
     * 3. Calls the bookings microservice to apply discount to user's bookings
     * 4. Returns upgrade confirmation with discount details
     *
     * If the bookings service is unavailable, the user is still upgraded but discounts
     * may not be applied, allowing graceful degradation.
     *
     * @param userId the unique identifier of the user to upgrade
     * @return DTO containing user information, premium status, and applied discount details
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    UserPremiumUpgradeResponseDTO upgradeToPremium(Long userId);
}


