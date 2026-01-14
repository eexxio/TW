package com.cinema.movies.controller;

import com.cinema.movies.dto.*;
import com.cinema.movies.entity.Movie;
import com.cinema.movies.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for MovieController.
 * Tests all REST endpoints using MockMvc.
 */
@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @MockBean
    private MovieMapper movieMapper;

    private Movie testMovie;
    private MovieRequestDTO testMovieRequestDTO;
    private MovieResponseDTO testMovieResponseDTO;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("Test Description");
        testMovie.setGenre("Action");
        testMovie.setDuration(120);
        testMovie.setDirector("Test Director");
        testMovie.setReleaseDate(LocalDate.of(2024, 1, 1));
        testMovie.setRating(8.5);

        testMovieRequestDTO = new MovieRequestDTO();
        testMovieRequestDTO.setTitle("Test Movie");
        testMovieRequestDTO.setDescription("Test Description");
        testMovieRequestDTO.setGenre("Action");
        testMovieRequestDTO.setDuration(120);

        testMovieResponseDTO = new MovieResponseDTO();
        testMovieResponseDTO.setId(1L);
        testMovieResponseDTO.setTitle("Test Movie");
        testMovieResponseDTO.setRating(8.5);
    }

    @Test
    void testCreateMovie_Success() throws Exception {
        // Arrange
        when(movieMapper.toEntity(any(MovieRequestDTO.class))).thenReturn(testMovie);
        when(movieService.createMovie(any(Movie.class))).thenReturn(testMovie);
        when(movieMapper.toResponseDTO(any(Movie.class))).thenReturn(testMovieResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovieRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void testCreateMovie_ValidationError() throws Exception {
        // Arrange - missing required title field
        MovieRequestDTO invalidRequest = new MovieRequestDTO();
        invalidRequest.setDescription("Test");

        // Act & Assert
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMovieById_Success() throws Exception {
        // Arrange
        when(movieService.getMovieById(1L)).thenReturn(testMovie);
        when(movieMapper.toResponseDTO(any(Movie.class))).thenReturn(testMovieResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void testUpdateMovie_Success() throws Exception {
        // Arrange
        when(movieMapper.toEntity(any(MovieRequestDTO.class))).thenReturn(testMovie);
        when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(testMovie);
        when(movieMapper.toResponseDTO(any(Movie.class))).thenReturn(testMovieResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovieRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteMovie_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSearchMoviesByTitle_Success() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie);
        List<MovieResponseDTO> responseDTOs = Arrays.asList(testMovieResponseDTO);

        when(movieService.searchMoviesByTitle("Test")).thenReturn(movies);
        when(movieMapper.toResponseDTOList(any())).thenReturn(responseDTOs);

        // Act & Assert
        mockMvc.perform(get("/api/movies/search")
                        .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Movie"));
    }

    @Test
    void testFilterMoviesByGenre_Success() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie);
        List<MovieResponseDTO> responseDTOs = Arrays.asList(testMovieResponseDTO);

        when(movieService.filterMoviesByGenre("Action")).thenReturn(movies);
        when(movieMapper.toResponseDTOList(any())).thenReturn(responseDTOs);

        // Act & Assert
        mockMvc.perform(get("/api/movies/filter")
                        .param("genre", "Action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSortMoviesByRating_Success() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie);
        List<MovieResponseDTO> responseDTOs = Arrays.asList(testMovieResponseDTO);

        when(movieService.sortMoviesByRating("desc")).thenReturn(movies);
        when(movieMapper.toResponseDTOList(any())).thenReturn(responseDTOs);

        // Act & Assert
        mockMvc.perform(get("/api/movies/sort")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSortMoviesByRating_DefaultOrder() throws Exception {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie);
        List<MovieResponseDTO> responseDTOs = Arrays.asList(testMovieResponseDTO);

        when(movieService.sortMoviesByRating("desc")).thenReturn(movies);
        when(movieMapper.toResponseDTOList(any())).thenReturn(responseDTOs);

        // Act & Assert - test default order parameter
        mockMvc.perform(get("/api/movies/sort"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMovieWithBookings_Success() throws Exception {
        // Arrange
        MovieWithBookingsResponseDTO response = new MovieWithBookingsResponseDTO();
        response.setMovie(testMovieResponseDTO);
        response.setBookings(Arrays.asList());
        response.setTotalBookings(0);

        when(movieService.getMovieWithBookings(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/movies/1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movie").exists())
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalBookings").value(0));
    }

    @Test
    void testMarkMovieAsPopular_Success() throws Exception {
        // Arrange
        MoviePopularityResponseDTO response = new MoviePopularityResponseDTO();
        response.setMovie(testMovieResponseDTO);
        response.setBookingCount(10);
        response.setPopular(true);
        response.setMessage("Movie is popular");

        when(movieService.markMovieAsPopular(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/movies/1/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movie").exists())
                .andExpect(jsonPath("$.bookingCount").value(10))
                .andExpect(jsonPath("$.popular").value(true))
                .andExpect(jsonPath("$.message").exists());
    }
}
