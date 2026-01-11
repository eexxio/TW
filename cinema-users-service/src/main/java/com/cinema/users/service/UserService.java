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
import java.util.stream.Collectors;

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

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

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

    @Override
    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public List<UserDTO> searchByName(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> (u.getFirstname() != null && u.getFirstname().toLowerCase().contains(keyword.toLowerCase())) ||
                        (u.getLastname() != null && u.getLastname().toLowerCase().contains(keyword.toLowerCase())))
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    public List<UserDTO> filterByRole(String role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase(role))
                .map(UserMapper::toDTO)
                .toList();
    }

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
                .filter(movie -> movie != null)
                .collect(Collectors.toList());

        // Create the response DTO
        UserWatchedMoviesResponseDTO response = new UserWatchedMoviesResponseDTO();
        response.setUser(UserMapper.toDTO(user));
        response.setWatchedMovies(watchedMovies);
        response.setTotalMoviesWatched(watchedMovies.size());

        return response;
    }

    @Override
    public UserActivityDTO getUserActivity(Long userId) {
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
                .filter(movie -> movie != null)
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

    @Override
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public UserPremiumUpgradeResponseDTO upgradeToPremium(Long userId) {
        // Get the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if already premium
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
