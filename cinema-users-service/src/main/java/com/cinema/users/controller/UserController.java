package com.cinema.users.controller;

import com.cinema.users.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cinema.users.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    public IUserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserDTO>createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
       UserDTO userDTO = userService.createUser(userCreateDTO);
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(userDTO);
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email,
                                        @RequestBody UserCreateDTO userCreateDTO) {
        try{
            UserDTO updateUser= userService.updateUser(email, userCreateDTO);
            return ResponseEntity.ok(updateUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteAccount(@PathVariable String email) {
        boolean isDeleted = userService.deleteUser(email);
        if(isDeleted){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Deleted successfully");
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User with email " + email + " couldn't be deleted");
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users =userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        boolean isMatching = userService.login(email, password);
        if (isMatching) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Successfully logged in");
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchByName(@RequestParam String keyword) {
        List<UserDTO> users = userService.searchByName(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterByRole(@RequestParam String role) {
        List<UserDTO> users = userService.filterByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<UserDTO>> sortByDateOfBirth(@RequestParam(defaultValue = "asc") String direction) {
        List<UserDTO> users = userService.sortByDateOfBirth(direction);
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by their unique identifier.
     * This endpoint is used by other microservices for cross-service communication.
     *
     * @param userId the ID of the user
     * @return user information
     * @author Tudor - Viza 3 bugfix
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Cross-service endpoint: Retrieves a user with all their bookings.
     * This endpoint calls the bookings-service to fetch booking information.
     *
     * @param userId the ID of the user
     * @return user information combined with booking data
     */
    @GetMapping("/{userId}/bookings")
    public ResponseEntity<UserWithBookingsResponseDTO> getUserWithBookings(@PathVariable Long userId) {
        UserWithBookingsResponseDTO response = userService.getUserWithBookings(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cross-service endpoint: Retrieves a user with movies they have watched.
     * This endpoint calls the bookings-service and movies-service to fetch complete information.
     *
     * @param userId the ID of the user
     * @return user information combined with watched movies list
     */
    @GetMapping("/{userId}/watched-movies")
    public ResponseEntity<UserWatchedMoviesResponseDTO> getUserWatchedMovies(@PathVariable Long userId) {
        UserWatchedMoviesResponseDTO response = userService.getUserWatchedMovies(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cross-service endpoint: Retrieves comprehensive user activity information.
     * Aggregates user profile, bookings, and watched movies from multiple services.
     *
     * @param userId the ID of the user
     * @return aggregated user activity information
     */
    @GetMapping("/{userId}/activity")
    public ResponseEntity<UserActivityDTO> getUserActivity(@PathVariable Long userId) {
        UserActivityDTO response = userService.getUserActivity(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cross-service endpoint: Upgrades a user to premium status.
     * This endpoint calls the bookings-service to apply premium discounts to user's bookings.
     * This is a POST request that modifies user state and calls external services.
     *
     * @param userId the ID of the user to upgrade
     * @return premium upgrade response with discount details
     */
    @PostMapping("/{userId}/upgrade-premium")
    public ResponseEntity<UserPremiumUpgradeResponseDTO> upgradeToPremium(@PathVariable Long userId) {
        UserPremiumUpgradeResponseDTO response = userService.upgradeToPremium(userId);
        return ResponseEntity.ok(response);
    }

}
