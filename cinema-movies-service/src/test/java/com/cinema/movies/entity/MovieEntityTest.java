package com.cinema.movies.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Movie entity.
 * Tests Lombok-generated methods and custom toString().
 *
 * @author Tudor
 */
class MovieEntityTest {

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
    }

    @Test
    void testGettersAndSetters() {
        // Test title
        movie.setTitle("Test Movie");
        assertEquals("Test Movie", movie.getTitle());

        // Test description
        movie.setDescription("Test Description");
        assertEquals("Test Description", movie.getDescription());

        // Test genre
        movie.setGenre("ACTION");
        assertEquals("ACTION", movie.getGenre());

        // Test duration
        movie.setDuration(120);
        assertEquals(120, movie.getDuration());

        // Test director
        movie.setDirector("Test Director");
        assertEquals("Test Director", movie.getDirector());

        // Test releaseDate
        LocalDate date = LocalDate.of(2024, 1, 1);
        movie.setReleaseDate(date);
        assertEquals(date, movie.getReleaseDate());

        // Test rating
        movie.setRating(8.5);
        assertEquals(8.5, movie.getRating());

        // Test posterUrl
        movie.setPosterUrl("http://example.com/poster.jpg");
        assertEquals("http://example.com/poster.jpg", movie.getPosterUrl());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        LocalDate releaseDate = LocalDate.of(2024, 5, 15);

        // Act - Movie only has constructor for its own fields, not BaseEntity fields
        Movie movie = new Movie(
            "Full Movie",
            "Full Description",
            "COMEDY",
            90,
            "Full Director",
            releaseDate,
            7.5,
            "http://example.com/full.jpg"
        );

        // Assert - BaseEntity fields (id, timestamps) are inherited but not in constructor
        assertEquals("Full Movie", movie.getTitle());
        assertEquals("Full Description", movie.getDescription());
        assertEquals("COMEDY", movie.getGenre());
        assertEquals(90, movie.getDuration());
        assertEquals("Full Director", movie.getDirector());
        assertEquals(releaseDate, movie.getReleaseDate());
        assertEquals(7.5, movie.getRating());
        assertEquals("http://example.com/full.jpg", movie.getPosterUrl());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Movie movie = new Movie();

        // Assert
        assertNotNull(movie);
        assertNull(movie.getTitle());
        assertNull(movie.getDescription());
        assertNull(movie.getGenre());
    }

    @Test
    void testToString_WithAllFields() {
        // Arrange
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setGenre("ACTION");
        movie.setDirector("Test Director");
        movie.setReleaseDate(LocalDate.of(2024, 1, 1));
        movie.setRating(8.5);
        movie.setDuration(120);

        // Act
        String result = movie.toString();

        // Assert
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("title='Test Movie'"));
        assertTrue(result.contains("genre='ACTION'"));
        assertTrue(result.contains("director='Test Director'"));
        assertTrue(result.contains("releaseDate=2024-01-01"));
        assertTrue(result.contains("rating=8.5"));
        assertTrue(result.contains("duration=120"));
    }

    @Test
    void testToString_WithNullFields() {
        // Arrange
        movie.setId(1L);
        movie.setTitle("Minimal Movie");

        // Act
        String result = movie.toString();

        // Assert
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("title='Minimal Movie'"));
        assertTrue(result.contains("genre='null'"));
        assertTrue(result.contains("director='null'"));
    }

    @Test
    void testEquals_WithSameId() {
        // Arrange
        Movie movie1 = new Movie();
        movie1.setId(1L);

        Movie movie2 = new Movie();
        movie2.setId(1L);

        // Assert
        assertEquals(movie1, movie2);
        assertEquals(movie1.hashCode(), movie2.hashCode());
    }

    @Test
    void testEquals_WithDifferentIds() {
        // Arrange
        Movie movie1 = new Movie();
        movie1.setId(1L);

        Movie movie2 = new Movie();
        movie2.setId(2L);

        // Assert
        assertNotEquals(movie1, movie2);
    }

    @Test
    void testEquals_InheritsFromBaseEntity() {
        // Arrange - Movie extends BaseEntity
        Movie movie = new Movie();
        movie.setId(1L);

        Movie otherMovie = new Movie();
        otherMovie.setId(1L);

        // Assert - Movies with same ID should be equal (inherited from BaseEntity)
        assertEquals(movie, otherMovie);
        assertEquals(movie.hashCode(), otherMovie.hashCode());
    }

    @Test
    void testCanSetAndGetId() {
        // Test that Movie inherits id field from BaseEntity
        movie.setId(100L);
        assertEquals(100L, movie.getId());
    }

    @Test
    void testCanSetAndGetTimestamps() {
        // Test that Movie inherits timestamp fields from BaseEntity
        var now = java.time.LocalDateTime.now();
        movie.setCreatedAt(now);
        movie.setUpdatedAt(now);

        assertEquals(now, movie.getCreatedAt());
        assertEquals(now, movie.getUpdatedAt());
    }

    @Test
    void testToStringFormat() {
        // Arrange
        movie.setId(5L);
        movie.setTitle("ToString Test");
        movie.setGenre("DRAMA");
        movie.setDirector("Director Name");
        movie.setReleaseDate(LocalDate.of(2023, 12, 25));
        movie.setRating(9.2);
        movie.setDuration(145);

        // Act
        String result = movie.toString();

        // Assert - verify format matches expected pattern
        assertTrue(result.startsWith("Movie{"));
        assertTrue(result.contains("}"));
    }
}
