package com.cinema.users.controller;

import com.cinema.users.dto.*;
import com.cinema.users.enums.Role;
import com.cinema.users.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController.
 * Tests all REST endpoints using MockMvc for HTTP layer testing.
 *
 * @author Alexandru Tesula
 */
@WebMvcTest(UserController.class)
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    private UserDTO testUserDTO;
    private UserCreateDTO testUserCreateDTO;
    private BookingDTO testBookingDTO;
    private MovieDTO testMovieDTO;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUserDTO = new UserDTO(
                "test@example.com",
                "John",
                "Doe",
                "+1234567890",
                "1990-01-01",
                Role.USER
        );

        testUserCreateDTO = new UserCreateDTO(
                "newuser@example.com",
                "Jane",
                "Smith",
                "password123456",
                "+1987654321",
                "1995-05-15",
                Role.USER
        );

        // Setup test booking
        testBookingDTO = new BookingDTO(
                1L, 1L, 1L, "Test Movie", "test@example.com",
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

    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(testUserDTO);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() throws Exception {
        when(userService.createUser(any(UserCreateDTO.class)))
                .thenThrow(new RuntimeException("User with email test@example.com already exists"));

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserCreateDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        when(userService.updateUser(anyString(), any(UserCreateDTO.class))).thenReturn(testUserDTO);

        mockMvc.perform(put("/users/update/test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateUser_UserNotFound() throws Exception {
        when(userService.updateUser(anyString(), any(UserCreateDTO.class)))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/users/update/notfound@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        when(userService.deleteUser("test@example.com")).thenReturn(true);

        mockMvc.perform(delete("/users/delete/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Deleted successfully"));
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception {
        when(userService.deleteUser("notfound@example.com")).thenReturn(false);

        mockMvc.perform(delete("/users/delete/notfound@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void testGetAllUsers_EmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testLogin_Success() throws Exception {
        when(userService.login("test@example.com", "password123")).thenReturn(true);

        mockMvc.perform(post("/users/login")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Successfully logged in"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        when(userService.login("test@example.com", "wrongpassword")).thenReturn(false);

        mockMvc.perform(post("/users/login")
                        .param("email", "test@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSearchByName_Success() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.searchByName("John")).thenReturn(users);

        mockMvc.perform(get("/users/search")
                        .param("keyword", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstname").value("John"));
    }

    @Test
    void testFilterByRole_Success() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.filterByRole("USER")).thenReturn(users);

        mockMvc.perform(get("/users/filter")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSortByDateOfBirth_Success() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.sortByDateOfBirth("asc")).thenReturn(users);

        mockMvc.perform(get("/users/sort")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // Cross-Service Endpoint Tests

    @Test
    void testGetUserWithBookings_Success() throws Exception {
        UserWithBookingsResponseDTO response = new UserWithBookingsResponseDTO(
                testUserDTO,
                Arrays.asList(testBookingDTO),
                1L
        );

        when(userService.getUserWithBookings(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalBookings").value(1));
    }

    @Test
    void testGetUserWithBookings_NoBookings() throws Exception {
        UserWithBookingsResponseDTO response = new UserWithBookingsResponseDTO(
                testUserDTO,
                List.of(),
                0L
        );

        when(userService.getUserWithBookings(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBookings").value(0))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.bookings.length()").value(0));
    }

    @Test
    void testGetUserWatchedMovies_Success() throws Exception {
        UserWatchedMoviesResponseDTO response = new UserWatchedMoviesResponseDTO(
                testUserDTO,
                Arrays.asList(testMovieDTO),
                1L
        );

        when(userService.getUserWatchedMovies(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1/watched-movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.watchedMovies").isArray())
                .andExpect(jsonPath("$.totalMoviesWatched").value(1));
    }

    @Test
    void testGetUserActivity_Success() throws Exception {
        UserActivityDTO response = new UserActivityDTO(
                testUserDTO,
                Arrays.asList(testBookingDTO),
                Arrays.asList(testMovieDTO),
                1L,
                1L
        );

        when(userService.getUserActivity(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.watchedMovies").isArray())
                .andExpect(jsonPath("$.totalBookings").value(1))
                .andExpect(jsonPath("$.totalMoviesWatched").value(1));
    }

    @Test
    void testUpgradeToPremium_Success() throws Exception {
        UserPremiumUpgradeResponseDTO response = new UserPremiumUpgradeResponseDTO(
                testUserDTO,
                true,
                "User 'test@example.com' successfully upgraded to premium. 10% discount applied to 1 bookings",
                10.0,
                1L
        );

        when(userService.upgradeToPremium(1L)).thenReturn(response);

        mockMvc.perform(post("/users/1/upgrade-premium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.isPremium").value(true))
                .andExpect(jsonPath("$.discountPercentage").value(10.0))
                .andExpect(jsonPath("$.appliedToBookingsCount").value(1));
    }

    @Test
    void testUpgradeToPremium_UserNotFound() throws Exception {
        when(userService.upgradeToPremium(999L))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        mockMvc.perform(post("/users/999/upgrade-premium"))
                .andExpect(status().isInternalServerError());
    }
}

