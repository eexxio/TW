package com.cinema.movies.service;

import com.cinema.movies.client.BookingServiceClient;
import com.cinema.movies.dto.*;
import com.cinema.movies.entity.Movie;
import com.cinema.movies.exception.ResourceNotFoundException;
import com.cinema.movies.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MovieServiceImpl.
 * Tests all business logic methods using Mockito for mocking dependencies.
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private BookingServiceClient bookingServiceClient;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie testMovie;
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
        testMovie.setPosterUrl("http://test.com/poster.jpg");

        testMovieResponseDTO = new MovieResponseDTO();
        testMovieResponseDTO.setId(1L);
        testMovieResponseDTO.setTitle("Test Movie");
        testMovieResponseDTO.setRating(8.5);
    }

    @Test
    void testCreateMovie_Success() {
        // Arrange
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        // Act
        Movie result = movieService.createMovie(testMovie);

        // Assert
        assertNotNull(result);
        assertEquals(testMovie.getId(), result.getId());
        assertEquals(testMovie.getTitle(), result.getTitle());
        verify(movieRepository, times(1)).save(testMovie);
    }

    @Test
    void testGetMovieById_Success() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

        // Act
        Movie result = movieService.getMovieById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMovie.getId(), result.getId());
        assertEquals(testMovie.getTitle(), result.getTitle());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void testGetMovieById_NotFound() {
        // Arrange
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(999L));
        verify(movieRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateMovie_Success() {
        // Arrange
        Movie updatedMovie = new Movie();
        updatedMovie.setTitle("Updated Movie");
        updatedMovie.setDescription("Updated Description");
        updatedMovie.setGenre("Drama");
        updatedMovie.setDuration(150);
        updatedMovie.setDirector("Updated Director");
        updatedMovie.setReleaseDate(LocalDate.of(2024, 6, 1));
        updatedMovie.setRating(9.0);
        updatedMovie.setPosterUrl("http://test.com/updated.jpg");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        // Act
        Movie result = movieService.updateMovie(1L, updatedMovie);

        // Assert
        assertNotNull(result);
        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testUpdateMovie_NotFound() {
        // Arrange
        Movie updatedMovie = new Movie();
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(999L, updatedMovie));
    }

    @Test
    void testDeleteMovie_Success() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        doNothing().when(movieRepository).delete(testMovie);

        // Act
        movieService.deleteMovie(1L);

        // Assert
        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).delete(testMovie);
    }

    @Test
    void testDeleteMovie_NotFound() {
        // Arrange
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(999L));
        verify(movieRepository, times(1)).findById(999L);
        verify(movieRepository, never()).delete(any());
    }

    @Test
    void testGetAllMovies_Success() {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie, new Movie(), new Movie());
        when(movieRepository.findAll()).thenReturn(movies);

        // Act
        List<Movie> result = movieService.getAllMovies();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void testGetAllMovies_EmptyList() {
        // Arrange
        when(movieRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Movie> result = movieService.getAllMovies();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void testSearchMoviesByTitle_Success() {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(movies);

        // Act
        List<Movie> result = movieService.searchMoviesByTitle("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
        verify(movieRepository, times(1)).findByTitleContainingIgnoreCase("Test");
    }

    @Test
    void testSearchMoviesByTitle_NoResults() {
        // Arrange
        when(movieRepository.findByTitleContainingIgnoreCase("NonExistent")).thenReturn(new ArrayList<>());

        // Act
        List<Movie> result = movieService.searchMoviesByTitle("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterMoviesByGenre_Success() {
        // Arrange
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByGenreIgnoreCase("Action")).thenReturn(movies);

        // Act
        List<Movie> result = movieService.filterMoviesByGenre("Action");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Action", result.get(0).getGenre());
        verify(movieRepository, times(1)).findByGenreIgnoreCase("Action");
    }

    @Test
    void testFilterMoviesByGenre_NoResults() {
        // Arrange
        when(movieRepository.findByGenreIgnoreCase("Horror")).thenReturn(new ArrayList<>());

        // Act
        List<Movie> result = movieService.filterMoviesByGenre("Horror");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSortMoviesByRating_Descending() {
        // Arrange
        Movie movie1 = new Movie();
        movie1.setRating(7.0);
        Movie movie2 = new Movie();
        movie2.setRating(9.0);
        Movie movie3 = new Movie();
        movie3.setRating(8.0);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3);
        when(movieRepository.findAll()).thenReturn(movies);

        // Act
        List<Movie> result = movieService.sortMoviesByRating("desc");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(9.0, result.get(0).getRating());
        assertEquals(8.0, result.get(1).getRating());
        assertEquals(7.0, result.get(2).getRating());
    }

    @Test
    void testSortMoviesByRating_Ascending() {
        // Arrange
        Movie movie1 = new Movie();
        movie1.setRating(7.0);
        Movie movie2 = new Movie();
        movie2.setRating(9.0);
        Movie movie3 = new Movie();
        movie3.setRating(8.0);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3);
        when(movieRepository.findAll()).thenReturn(movies);

        // Act
        List<Movie> result = movieService.sortMoviesByRating("asc");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(7.0, result.get(0).getRating());
        assertEquals(8.0, result.get(1).getRating());
        assertEquals(9.0, result.get(2).getRating());
    }

    @Test
    void testSortMoviesByRating_WithNullRatings() {
        // Arrange
        Movie movie1 = new Movie();
        movie1.setRating(null);
        Movie movie2 = new Movie();
        movie2.setRating(9.0);
        Movie movie3 = new Movie();
        movie3.setRating(null);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3);
        when(movieRepository.findAll()).thenReturn(movies);

        // Act
        List<Movie> result = movieService.sortMoviesByRating("desc");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(9.0, result.get(0).getRating());
        // Null ratings should be treated as 0.0 and come last
    }

    @Test
    void testGetMovieWithBookings_Success() {
        // Arrange
        List<BookingDTO> bookings = new ArrayList<>();
        BookingDTO booking1 = new BookingDTO();
        booking1.setId(1L);
        booking1.setMovieId(1L);
        bookings.add(booking1);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(bookingServiceClient.getBookingsByMovieId(1L)).thenReturn(bookings);
        when(movieMapper.toResponseDTO(testMovie)).thenReturn(testMovieResponseDTO);

        // Act
        MovieWithBookingsResponseDTO result = movieService.getMovieWithBookings(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMovieResponseDTO, result.getMovie());
        assertEquals(1, result.getBookings().size());
        assertEquals(1L, result.getTotalBookings());
        verify(movieRepository, times(1)).findById(1L);
        verify(bookingServiceClient, times(1)).getBookingsByMovieId(1L);
    }

    @Test
    void testGetMovieWithBookings_EmptyBookings() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(bookingServiceClient.getBookingsByMovieId(1L)).thenReturn(new ArrayList<>());
        when(movieMapper.toResponseDTO(testMovie)).thenReturn(testMovieResponseDTO);

        // Act
        MovieWithBookingsResponseDTO result = movieService.getMovieWithBookings(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMovieResponseDTO, result.getMovie());
        assertTrue(result.getBookings().isEmpty());
        assertEquals(0L, result.getTotalBookings());
    }

    @Test
    void testGetMovieWithBookings_MovieNotFound() {
        // Arrange
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieWithBookings(999L));
        verify(bookingServiceClient, never()).getBookingsByMovieId(anyLong());
    }

    @Test
    void testMarkMovieAsPopular_IsPopular() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(bookingServiceClient.getBookingCountByMovieId(1L)).thenReturn(10L);
        when(movieMapper.toResponseDTO(testMovie)).thenReturn(testMovieResponseDTO);

        // Act
        MoviePopularityResponseDTO result = movieService.markMovieAsPopular(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMovieResponseDTO, result.getMovie());
        assertEquals(10L, result.getBookingCount());
        assertTrue(result.isPopular());
        assertTrue(result.getMessage().contains("is popular"));
        verify(movieRepository, times(1)).findById(1L);
        verify(bookingServiceClient, times(1)).getBookingCountByMovieId(1L);
    }

    @Test
    void testMarkMovieAsPopular_NotPopular() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(bookingServiceClient.getBookingCountByMovieId(1L)).thenReturn(3L);
        when(movieMapper.toResponseDTO(testMovie)).thenReturn(testMovieResponseDTO);

        // Act
        MoviePopularityResponseDTO result = movieService.markMovieAsPopular(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMovieResponseDTO, result.getMovie());
        assertEquals(3L, result.getBookingCount());
        assertFalse(result.isPopular());
        assertTrue(result.getMessage().contains("needs"));
        verify(movieRepository, times(1)).findById(1L);
        verify(bookingServiceClient, times(1)).getBookingCountByMovieId(1L);
    }

    @Test
    void testMarkMovieAsPopular_MovieNotFound() {
        // Arrange
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> movieService.markMovieAsPopular(999L));
        verify(bookingServiceClient, never()).getBookingCountByMovieId(anyLong());
    }

    @Test
    void testMarkMovieAsPopular_ExactlyAtThreshold() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(bookingServiceClient.getBookingCountByMovieId(1L)).thenReturn(5L);
        when(movieMapper.toResponseDTO(testMovie)).thenReturn(testMovieResponseDTO);

        // Act
        MoviePopularityResponseDTO result = movieService.markMovieAsPopular(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPopular());
        assertEquals(5L, result.getBookingCount());
    }
}
