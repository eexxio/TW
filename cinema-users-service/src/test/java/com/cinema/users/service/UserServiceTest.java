package com.cinema.users.service;

import com.cinema.users.client.BookingServiceClient;
import com.cinema.users.client.MovieServiceClient;
import com.cinema.users.dto.*;
import com.cinema.users.entity.User;
import com.cinema.users.enums.Role;
import com.cinema.users.mapper.UserMapper;
import com.cinema.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Tests business logic including CRUD operations and cross-service calls.
 * Mocks external services to focus on UserService logic.
 *
 * @author Alexandru Tesula
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingServiceClient bookingServiceClient;

    @Mock
    private MovieServiceClient movieServiceClient;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateDTO userCreateDTO;
    private BookingDTO testBookingDTO;
    private MovieDTO testMovieDTO;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User("John", "Doe", "john@example.com", "hashedPassword123",
                "+1234567890", "1990-01-01", Role.USER);
        testUser.setUser_id(1L);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // Setup user create DTO
        userCreateDTO = new UserCreateDTO(
                "John",
                "Doe",
                "john@example.com",
                "plainPassword123",
                "+1234567890",
                "1990-01-01",
                Role.USER
        );

        // Setup test booking
        testBookingDTO = new BookingDTO(
                1L, 1L, 1L, "Test Movie", "john@example.com",
                LocalDateTime.now(), 5, "A", 15.99, "CONFIRMED",
                LocalDateTime.now(), LocalDateTime.now()
        );

        // Setup test movie
        testMovieDTO = new MovieDTO();
        testMovieDTO.setId(1L);
        testMovieDTO.setTitle("Test Movie");
        testMovieDTO.setGenre("ACTION");
        testMovieDTO.setRating(8.5);
    }

    // CRUD Tests

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(userCreateDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.createUser(userCreateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).existsByEmail(userCreateDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(userCreateDTO.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.createUser(userCreateDTO));
        verify(userRepository, times(1)).existsByEmail(userCreateDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.updateUser("john@example.com", userCreateDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.updateUser("notfound@example.com", userCreateDTO));
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.deleteUser("john@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.deleteUser("notfound@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testLogin_Success() {
        // Arrange
        testUser.setPassword(new BCryptPasswordEncoder().encode("plainPassword123"));
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.login("john@example.com", "plainPassword123");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        testUser.setPassword(new BCryptPasswordEncoder().encode("plainPassword123"));
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.login("john@example.com", "wrongPassword");

        // Assert
        assertFalse(result);
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.login("notfound@example.com", "password"));
    }

    @Test
    void testSearchByName_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTO> result = userService.searchByName("John");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchByName_NoMatches() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTO> result = userService.searchByName("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterByRole_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTO> result = userService.filterByRole("USER");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSortByDateOfBirth_Ascending() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTO> result = userService.sortByDateOfBirth("asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // Cross-Service Tests

    @Test
    void testGetUserWithBookings_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingServiceClient.getBookingsByUserId(1L)).thenReturn(Arrays.asList(testBookingDTO));

        // Act
        UserWithBookingsResponseDTO result = userService.getUserWithBookings(1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUser());
        assertEquals(1, result.getTotalBookings());
        assertEquals(1, result.getBookings().size());
        verify(bookingServiceClient, times(1)).getBookingsByUserId(1L);
    }

    @Test
    void testGetUserWithBookings_NoBookings() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingServiceClient.getBookingsByUserId(1L)).thenReturn(List.of());

        // Act
        UserWithBookingsResponseDTO result = userService.getUserWithBookings(1L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalBookings());
        assertTrue(result.getBookings().isEmpty());
    }

    @Test
    void testGetUserWithBookings_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserWithBookings(999L));
    }

    @Test
    void testGetUserWithBookings_BookingServiceDown() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingServiceClient.getBookingsByUserId(1L)).thenReturn(List.of());

        // Act - Should handle gracefully
        UserWithBookingsResponseDTO result = userService.getUserWithBookings(1L);

        // Assert - Should return user with empty bookings
        assertNotNull(result);
        assertEquals(0, result.getTotalBookings());
    }

    @Test
    void testGetUserWatchedMovies_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingServiceClient.getBookingsByUserId(1L)).thenReturn(Arrays.asList(testBookingDTO));
        when(movieServiceClient.getMovieById(1L)).thenReturn(testMovieDTO);

        // Act
        UserWatchedMoviesResponseDTO result = userService.getUserWatchedMovies(1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUser());
        assertEquals(1, result.getTotalMoviesWatched());
        assertEquals(1, result.getWatchedMovies().size());
        verify(movieServiceClient, times(1)).getMovieById(1L);
    }

    @Test
    void testGetUserWatchedMovies_MovieServiceDown() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingServiceClient.getBookingsByUserId(1L)).thenReturn(Arrays.asList(testBookingDTO));
        when(movieServiceClient.getMovieById(1L)).thenReturn(null); // Service unavailable

        // Act
        UserWatchedMoviesResponseDTO result = userService.getUserWatchedMovies(1L);

        // Assert - Should return user with empty movies
        assertNotNull(result);
        assertEquals(0, result.getTotalMoviesWatched());
        assertTrue(result.getWatchedMovies().isEmpty());
    }

    @Test
    void testGetUserActivity_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingServiceClient.getBookingsByUserId(1L)).thenReturn(Arrays.asList(testBookingDTO));
        when(movieServiceClient.getMovieById(1L)).thenReturn(testMovieDTO);

        // Act
        UserActivityDTO result = userService.getUserActivity(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalBookings());
        assertEquals(1, result.getTotalMoviesWatched());
        verify(bookingServiceClient, times(1)).getBookingsByUserId(1L);
        verify(movieServiceClient, times(1)).getMovieById(1L);
    }

    @Test
    void testUpgradeToPremium_Success() {
        // Arrange
        testUser.setRole(Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(bookingServiceClient.getBookingsByUserId(1L)).thenReturn(Arrays.asList(testBookingDTO));

        // Act
        UserPremiumUpgradeResponseDTO result = userService.upgradeToPremium(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPremium());
        assertEquals(10.0, result.getDiscountPercentage());
        assertEquals(1, result.getAppliedToBookingsCount());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpgradeToPremium_AlreadyPremium() {
        // Arrange
        testUser.setRole(Role.PREMIUM);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(bookingServiceClient.getBookingsByUserId(1L)).thenReturn(Arrays.asList(testBookingDTO));

        // Act
        UserPremiumUpgradeResponseDTO result = userService.upgradeToPremium(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPremium());
        assertTrue(result.getMessage().contains("already premium"));
    }

    @Test
    void testUpgradeToPremium_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.upgradeToPremium(999L));
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
    }
}

