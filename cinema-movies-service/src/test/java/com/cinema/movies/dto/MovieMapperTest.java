package com.cinema.movies.dto;

import com.cinema.movies.entity.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MovieMapper.
 * Tests entity-DTO conversion methods.
 *
 * @author Tudor
 */
class MovieMapperTest {

    private MovieMapper movieMapper;

    @BeforeEach
    void setUp() {
        movieMapper = new MovieMapper();
    }

    @Test
    void testToEntity_WithAllFields() {
        // Arrange
        MovieRequestDTO requestDTO = new MovieRequestDTO();
        requestDTO.setTitle("Test Movie");
        requestDTO.setDescription("Test Description");
        requestDTO.setGenre("ACTION");
        requestDTO.setDuration(120);
        requestDTO.setDirector("Test Director");
        requestDTO.setReleaseDate(LocalDate.of(2024, 1, 1));
        requestDTO.setRating(8.5);
        requestDTO.setPosterUrl("http://example.com/poster.jpg");

        // Act
        Movie movie = movieMapper.toEntity(requestDTO);

        // Assert
        assertNotNull(movie);
        assertEquals("Test Movie", movie.getTitle());
        assertEquals("Test Description", movie.getDescription());
        assertEquals("ACTION", movie.getGenre());
        assertEquals(120, movie.getDuration());
        assertEquals("Test Director", movie.getDirector());
        assertEquals(LocalDate.of(2024, 1, 1), movie.getReleaseDate());
        assertEquals(8.5, movie.getRating());
        assertEquals("http://example.com/poster.jpg", movie.getPosterUrl());
    }

    @Test
    void testToEntity_WithRequiredFieldsOnly() {
        // Arrange
        MovieRequestDTO requestDTO = new MovieRequestDTO();
        requestDTO.setTitle("Minimal Movie");

        // Act
        Movie movie = movieMapper.toEntity(requestDTO);

        // Assert
        assertNotNull(movie);
        assertEquals("Minimal Movie", movie.getTitle());
        assertNull(movie.getDescription());
        assertNull(movie.getGenre());
        assertNull(movie.getDuration());
        assertNull(movie.getDirector());
        assertNull(movie.getReleaseDate());
        assertNull(movie.getRating());
        assertNull(movie.getPosterUrl());
    }

    @Test
    void testToResponseDTO_WithAllFields() {
        // Arrange
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setGenre("COMEDY");
        movie.setDuration(90);
        movie.setDirector("Test Director");
        movie.setReleaseDate(LocalDate.of(2024, 5, 15));
        movie.setRating(7.5);
        movie.setPosterUrl("http://example.com/poster.jpg");
        movie.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
        movie.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 12, 0));

        // Act
        MovieResponseDTO responseDTO = movieMapper.toResponseDTO(movie);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Test Movie", responseDTO.getTitle());
        assertEquals("Test Description", responseDTO.getDescription());
        assertEquals("COMEDY", responseDTO.getGenre());
        assertEquals(90, responseDTO.getDuration());
        assertEquals("Test Director", responseDTO.getDirector());
        assertEquals(LocalDate.of(2024, 5, 15), responseDTO.getReleaseDate());
        assertEquals(7.5, responseDTO.getRating());
        assertEquals("http://example.com/poster.jpg", responseDTO.getPosterUrl());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), responseDTO.getCreatedAt());
        assertEquals(LocalDateTime.of(2024, 1, 2, 12, 0), responseDTO.getUpdatedAt());
    }

    @Test
    void testToResponseDTO_WithNullFields() {
        // Arrange
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");

        // Act
        MovieResponseDTO responseDTO = movieMapper.toResponseDTO(movie);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Test Movie", responseDTO.getTitle());
        assertNull(responseDTO.getDescription());
        assertNull(responseDTO.getGenre());
        assertNull(responseDTO.getDuration());
        assertNull(responseDTO.getDirector());
        assertNull(responseDTO.getReleaseDate());
        assertNull(responseDTO.getRating());
        assertNull(responseDTO.getPosterUrl());
    }

    @Test
    void testToResponseDTOList_WithMultipleMovies() {
        // Arrange
        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Movie 1");
        movie1.setGenre("ACTION");

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("Movie 2");
        movie2.setGenre("COMEDY");

        List<Movie> movies = Arrays.asList(movie1, movie2);

        // Act
        List<MovieResponseDTO> responseDTOs = movieMapper.toResponseDTOList(movies);

        // Assert
        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.size());

        assertEquals(1L, responseDTOs.get(0).getId());
        assertEquals("Movie 1", responseDTOs.get(0).getTitle());
        assertEquals("ACTION", responseDTOs.get(0).getGenre());

        assertEquals(2L, responseDTOs.get(1).getId());
        assertEquals("Movie 2", responseDTOs.get(1).getTitle());
        assertEquals("COMEDY", responseDTOs.get(1).getGenre());
    }

    @Test
    void testToResponseDTOList_WithEmptyList() {
        // Arrange
        List<Movie> movies = List.of();

        // Act
        List<MovieResponseDTO> responseDTOs = movieMapper.toResponseDTOList(movies);

        // Assert
        assertNotNull(responseDTOs);
        assertTrue(responseDTOs.isEmpty());
    }

    @Test
    void testToResponseDTOList_WithNullList() {
        // Arrange
        List<Movie> movies = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            movieMapper.toResponseDTOList(movies);
        });
    }

    @Test
    void testToEntityAndToResponseDTORoundTrip() {
        // Arrange
        MovieRequestDTO requestDTO = new MovieRequestDTO();
        requestDTO.setTitle("Round Trip Movie");
        requestDTO.setDescription("Description");
        requestDTO.setGenre("DRAMA");
        requestDTO.setDuration(150);
        requestDTO.setRating(9.0);

        // Act - Convert to entity
        Movie movie = movieMapper.toEntity(requestDTO);
        MovieResponseDTO responseDTO = movieMapper.toResponseDTO(movie);

        // Assert - Verify data is preserved
        assertEquals(requestDTO.getTitle(), responseDTO.getTitle());
        assertEquals(requestDTO.getDescription(), responseDTO.getDescription());
        assertEquals(requestDTO.getGenre(), responseDTO.getGenre());
        assertEquals(requestDTO.getDuration(), responseDTO.getDuration());
        assertEquals(requestDTO.getRating(), responseDTO.getRating());
    }
}
