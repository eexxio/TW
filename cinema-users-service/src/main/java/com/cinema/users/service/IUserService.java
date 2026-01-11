package com.cinema.users.service;

import com.cinema.users.dto.UserCreateDTO;
import com.cinema.users.dto.UserDTO;
import com.cinema.users.dto.UserActivityDTO;

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
     * @param email the email identifier of the user to update
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
     * @param email the user's email
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
}


