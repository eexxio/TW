package com.cinema.bookings.service;

import com.cinema.bookings.dto.*;
import com.cinema.bookings.entity.Booking;
import com.cinema.bookings.exception.BookingNotFoundException;
import com.cinema.bookings.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingRequestDTO requestDTO;
    private BookingResponseDTO responseDTO;
    private MovieResponseDTO movieResponseDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingService, "moviesServiceUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(bookingService, "usersServiceUrl", "http://localhost:8082");

        booking = new Booking();
        booking.setId(1L);
        booking.setUserId(1L);
        booking.setMovieId(1L);
        booking.setMovieTitle("Test Movie");
        booking.setUserEmail("test@example.com");
        booking.setStatus("PENDING");
        booking.setPrice(100.0);
        booking.setScreeningTime(LocalDateTime.now());

        requestDTO = new BookingRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setMovieId(1L);
        requestDTO.setPrice(100.0);

        responseDTO = new BookingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setMovieId(1L);
        responseDTO.setStatus("PENDING");

        movieResponseDTO = new MovieResponseDTO();
        movieResponseDTO.setId(1L);
        movieResponseDTO.setTitle("Test Movie");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setEmail("test@example.com");
    }

    @Test
    void testCreateBooking_Success() {
        when(bookingMapper.toEntity(requestDTO)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.createBooking(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingRepository).save(booking);
        verify(bookingMapper).toEntity(requestDTO);
        verify(bookingMapper).toResponseDTO(booking);
    }

    @Test
    void testGetBookingById_Found() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.getBookingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingRepository).findById(1L);
    }

    @Test
    void testGetBookingById_NotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(1L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void testUpdateBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.updateBooking(1L, requestDTO);

        assertThat(result).isNotNull();
        verify(bookingMapper).updateEntityFromDTO(requestDTO, booking);
        verify(bookingRepository).save(booking);
    }

    @Test
    void testUpdateBooking_NotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBooking(1L, requestDTO))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void testDeleteBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);

        verify(bookingRepository).delete(booking);
    }

    @Test
    void testDeleteBooking_NotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.deleteBooking(1L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void testGetAllBookings() {
        List<Booking> bookings = Arrays.asList(booking, booking);
        when(bookingRepository.findAll()).thenReturn(bookings);
        when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

        List<BookingResponseDTO> result = bookingService.getAllBookings();

        assertThat(result).hasSize(2);
        verify(bookingRepository).findAll();
    }

    @Test
    void testGetBookingsByUserId() {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findByUserId(1L)).thenReturn(bookings);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        List<BookingResponseDTO> result = bookingService.getBookingsByUserId(1L);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByUserId(1L);
    }

    @Test
    void testGetBookingsByStatus() {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findByStatus("PENDING")).thenReturn(bookings);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        List<BookingResponseDTO> result = bookingService.getBookingsByStatus("PENDING");

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByStatus("PENDING");
    }

    @Test
    void testSortBookings_ByScreeningTime() {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findAll()).thenReturn(bookings);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        List<BookingResponseDTO> result = bookingService.sortBookings("screeningTime", "asc");

        assertThat(result).hasSize(1);
        verify(bookingRepository).findAll();
    }

    @Test
    void testCreateBookingWithValidation_Success() {
        when(restTemplate.getForObject(eq("http://localhost:8081/api/movies/1"), eq(MovieResponseDTO.class)))
                .thenReturn(movieResponseDTO);
        when(restTemplate.getForObject(eq("http://localhost:8082/api/users/1"), eq(UserResponseDTO.class)))
                .thenReturn(userResponseDTO);
        when(bookingMapper.toEntity(requestDTO)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.createBookingWithValidation(requestDTO);

        assertThat(result).isNotNull();
        verify(restTemplate).getForObject(eq("http://localhost:8081/api/movies/1"), eq(MovieResponseDTO.class));
        verify(restTemplate).getForObject(eq("http://localhost:8082/api/users/1"), eq(UserResponseDTO.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBookingWithValidation_MovieNotFound() {
        when(restTemplate.getForObject(eq("http://localhost:8081/api/movies/1"), eq(MovieResponseDTO.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> bookingService.createBookingWithValidation(requestDTO))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("Movie not found");
    }

    @Test
    void testCreateBookingWithValidation_UserNotFound() {
        when(restTemplate.getForObject(eq("http://localhost:8081/api/movies/1"), eq(MovieResponseDTO.class)))
                .thenReturn(movieResponseDTO);
        when(restTemplate.getForObject(eq("http://localhost:8082/api/users/1"), eq(UserResponseDTO.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> bookingService.createBookingWithValidation(requestDTO))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testConfirmBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(restTemplate.getForObject(eq("http://localhost:8082/api/users/1"), eq(UserResponseDTO.class)))
                .thenReturn(userResponseDTO);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.confirmBooking(1L);

        assertThat(result).isNotNull();
        verify(bookingRepository).save(booking);
        verify(restTemplate).getForObject(eq("http://localhost:8082/api/users/1"), eq(UserResponseDTO.class));
    }

    @Test
    void testConfirmBooking_NotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.confirmBooking(1L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void testGetEnrichedBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(restTemplate.getForObject(eq("http://localhost:8081/api/movies/1"), eq(MovieResponseDTO.class)))
                .thenReturn(movieResponseDTO);
        when(restTemplate.getForObject(eq("http://localhost:8082/api/users/1"), eq(UserResponseDTO.class)))
                .thenReturn(userResponseDTO);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        Map<String, Object> result = bookingService.getEnrichedBooking(1L);

        assertThat(result).isNotNull();
        assertThat(result).containsKeys("booking", "movie", "user");
        verify(restTemplate).getForObject(eq("http://localhost:8081/api/movies/1"), eq(MovieResponseDTO.class));
        verify(restTemplate).getForObject(eq("http://localhost:8082/api/users/1"), eq(UserResponseDTO.class));
    }

    @Test
    void testGetEnrichedBooking_NotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getEnrichedBooking(1L))
                .isInstanceOf(BookingNotFoundException.class);
    }
}
