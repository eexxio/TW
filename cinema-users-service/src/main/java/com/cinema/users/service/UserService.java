package com.cinema.users.service;

import com.cinema.users.client.BookingServiceClient;
import com.cinema.users.client.MovieServiceClient;
import com.cinema.users.dto.*;
import com.cinema.users.entity.User;
import jakarta.transaction.Transactional;
import com.cinema.users.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cinema.users.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of the IUserService interface.
 * Provides business logic for user management including CRUD operations,
 * search, filtering, sorting, and cross-microservice integration with
 * the bookings and movies services.
 *
 * This service uses Spring Data JPA for database operations and WebClient
 * for communication with other microservices.
 *
 * @author Alexandru Tesula
 */
@Service
@Slf4j
public class UserService implements  IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingServiceClient bookingServiceClient;

    @Autowired
    private MovieServiceClient movieServiceClient;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Creates a new user in the database.
     *
     * Validates that the email is not already registered, encrypts the password
     * using BCrypt, and persists the user entity.
     *
     * @param userCreateDTO the user creation data transfer object
     * @return the created user as a DTO
     * @throws RuntimeException if email already exists
     * @author Alexandru Tesula
     */
    @Override
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        if(userRepository.existsByEmail(userCreateDTO.getEmail()))
        {
            throw new RuntimeException("User with email "+ userCreateDTO.getEmail()+" already exists");
        }

        userCreateDTO.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        User user = UserMapper.toEntity(userCreateDTO);
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    /**
     * Updates an existing user's information.
     *
     * Finds user by email and updates personal details. Does not update
     * email or password through this method.
     *
     * @param email the email of the user to update
     * @param userDTO the updated user information
     * @return the updated user as a DTO
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    @Override
    public UserDTO updateUser(String email, UserCreateDTO userDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found in method updateUser"));

        user.setFirstname(userDTO.getFirstName());
        user.setLastname(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return list of all users as DTOs, empty list if no users exist
     * @author Alexandru Tesula
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    /**
     * Deletes a user from the database by email.
     *
     * @param email the email of the user to delete
     * @return true if user was deleted, false if user not found
     * @author Alexandru Tesula
     */
    @Override
    @Transactional
    public boolean deleteUser(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Authenticates a user by verifying email and password.
     *
     * Uses BCrypt password matching for secure authentication.
     *
     * @param email the user's email
     * @param password the plain text password to verify
     * @return true if credentials are valid, false otherwise
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    @Override
    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
        return passwordEncoder.matches(password, user.getPassword());
    }

    /**
     * Searches for users by first or last name using case-insensitive partial matching.
     *
     * @param keyword the search keyword to match against names
     * @return list of matching users as DTOs
     * @author Alexandru Tesula
     */
    @Override
    public List<UserDTO> searchByName(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> (u.getFirstname() != null && u.getFirstname().toLowerCase().contains(keyword.toLowerCase())) ||
                        (u.getLastname() != null && u.getLastname().toLowerCase().contains(keyword.toLowerCase())))
                .map(UserMapper::toDTO)
                .toList();
    }

    /**
     * Filters users by their assigned role.
     *
     * @param role the role to filter by (e.g., USER, ADMIN, PREMIUM)
     * @return list of users with the specified role
     * @author Alexandru Tesula
     */
    @Override
    public List<UserDTO> filterByRole(String role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase(role))
                .map(UserMapper::toDTO)
                .toList();
    }

    /**
     * Sorts users by date of birth in the specified order.
     *
     * @param direction sort direction ("asc" for ascending, "desc" for descending)
     * @return sorted list of users
     * @author Alexandru Tesula
     */
    @Override
    public List<UserDTO> sortByDateOfBirth(String direction) {
        Comparator<User> comparator = Comparator.comparing(User::getDateOfBirth);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        return userRepository.findAll().stream()
                .filter(u -> u.getDateOfBirth() != null)
                .sorted(comparator)
                .map(UserMapper::toDTO)
                .toList();
    }

    /**
     * Retrieves a user with their complete booking history from the bookings microservice.
     * This is a cross-service operation that combines data from users and bookings services.
     *
     * The method performs the following steps:
     * 1. Retrieves the user from the local database
     * 2. Calls the bookings microservice via WebClient
     * 3. Combines the data into a single response DTO
     *
     * If the bookings service is unavailable, an empty bookings list is returned
     * with totalBookings set to 0, allowing graceful degradation.
     *
     * @param userId the unique identifier of the user
     * @return DTO containing user information and booking history
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    @Override
    public UserWithBookingsResponseDTO getUserWithBookings(Long userId) {
        // Get the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Fetch bookings from the bookings service
        List<BookingDTO> bookings = bookingServiceClient.getBookingsByUserId(userId);

        // Create the response DTO
        UserWithBookingsResponseDTO response = new UserWithBookingsResponseDTO();
        response.setUser(UserMapper.toDTO(user));
        response.setBookings(bookings);
        response.setTotalBookings(bookings.size());

        return response;
    }

    /**
     * Retrieves a user with movies they have watched from the bookings and movies microservices.
     * This is a cross-service operation that combines data from three services.
     *
     * The method performs the following steps:
     * 1. Retrieves the user from the local database
     * 2. Calls the bookings microservice to get user's bookings
     * 3. Extracts unique movie IDs from bookings
     * 4. Calls the movies microservice for each movie's details
     * 5. Combines all data into a single response DTO
     *
     * If either external service is unavailable, the method gracefully degrades
     * by returning empty lists for the unavailable data.
     *
     * @param userId the unique identifier of the user
     * @return DTO containing user information and watched movies list
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    @Override
    public UserWatchedMoviesResponseDTO getUserWatchedMovies(Long userId) {
        // Get the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Fetch bookings from the bookings service
        List<BookingDTO> bookings = bookingServiceClient.getBookingsByUserId(userId);

        // Fetch movie details for each booking
        List<MovieDTO> watchedMovies = bookings.stream()
                .map(BookingDTO::getMovieId)
                .distinct()
                .map(movieServiceClient::getMovieById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Create the response DTO
        UserWatchedMoviesResponseDTO response = new UserWatchedMoviesResponseDTO();
        response.setUser(UserMapper.toDTO(user));
        response.setWatchedMovies(watchedMovies);
        response.setTotalMoviesWatched(watchedMovies.size());

        return response;
    }

    /**
     * Retrieves comprehensive user activity information from multiple microservices.
     * Aggregates user profile, bookings, and watched movies data from three services.
     *
     * This is the most comprehensive cross-service operation that provides
     * a complete view of user engagement including:
     * - User profile information
     * - All booking history
     * - All watched movies with details
     * - Aggregated statistics (totals)
     *
     * @param userId the unique identifier of the user
     * @return DTO containing complete user activity information
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    @Override
    public UserActivityDTO getUserActivity(Long userId) {
        // Get the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<BookingDTO> bookings = bookingServiceClient.getBookingsByUserId(userId);

        List<MovieDTO> watchedMovies = bookings.stream()
                .map(BookingDTO::getMovieId)
                .distinct()
                .map(movieServiceClient::getMovieById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Create the response DTO
        UserActivityDTO response = new UserActivityDTO();
        response.setUser(UserMapper.toDTO(user));
        response.setBookings(bookings);
        response.setWatchedMovies(watchedMovies);
        response.setTotalBookings(bookings.size());
        response.setTotalMoviesWatched(watchedMovies.size());

        return response;
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the unique identifier of the user
     * @return the user as a DTO
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    @Override
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    /**
     * Upgrades a user to premium status and applies discounts to their bookings.
     * This is a cross-service operation that modifies user state and queries
     * the bookings microservice.
     *
     * The method performs the following steps:
     * 1. Retrieves and validates the user
     * 2. Checks if user is already premium
     * 3. Updates user role to PREMIUM
     * 4. Queries bookings service for discount application
     * 5. Returns comprehensive upgrade response
     *
     * Premium users receive a 10% discount on bookings.
     *
     * @param userId the unique identifier of the user to upgrade
     * @return DTO containing upgrade status and discount information
     * @throws RuntimeException if user not found
     * @author Alexandru Tesula
     */
    @Override
    public UserPremiumUpgradeResponseDTO upgradeToPremium(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean wasAlreadyPremium = user.getRole() != null &&
                                   user.getRole().equals(com.cinema.users.enums.Role.PREMIUM);

        // Update user role to PREMIUM
        user.setRole(com.cinema.users.enums.Role.PREMIUM);
        userRepository.save(user);

        // Get user's bookings to apply discount
        List<BookingDTO> bookings = bookingServiceClient.getBookingsByUserId(userId);
        long appliedToBookingsCount = bookings.size();

        // Create the response DTO
        UserPremiumUpgradeResponseDTO response = new UserPremiumUpgradeResponseDTO();
        response.setUser(UserMapper.toDTO(user));
        response.setPremium(true);
        response.setDiscountPercentage(10.0); // 10% discount for premium users
        response.setAppliedToBookingsCount(appliedToBookingsCount);

        String message;
        if (wasAlreadyPremium) {
            message = String.format("User '%s' is already premium. Discount applies to %d existing bookings",
                    user.getEmail(), appliedToBookingsCount);
        } else {
            message = String.format("User '%s' successfully upgraded to premium. 10%% discount applied to %d bookings",
                    user.getEmail(), appliedToBookingsCount);
        }
        response.setMessage(message);

        log.info("User {} upgraded to premium status", userId);
        return response;
    }

}
