package com.cinema.bookings.service;

import com.cinema.bookings.dto.BookingMapper;
import com.cinema.bookings.dto.BookingRequestDTO;
import com.cinema.bookings.dto.BookingResponseDTO;
import com.cinema.bookings.dto.MovieResponseDTO;
import com.cinema.bookings.dto.UserResponseDTO;
import com.cinema.bookings.entity.Booking;
import com.cinema.bookings.exception.BookingNotFoundException;
import com.cinema.bookings.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final RestTemplate restTemplate;

    @Value("${service.movies.url}")
    private String moviesServiceUrl;

    @Value("${service.users.url}")
    private String usersServiceUrl;

    /**
     * Creates a new booking with the provided details.
     *
     * @param requestDTO the booking request data
     * @return the created booking response
     * @author Ioana-Loredana Stan
     */
    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO) {
        Booking booking = bookingMapper.toEntity(requestDTO);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDTO(savedBooking);
    }

    /**
     * Retrieves a booking by its unique identifier.
     *
     * @param id the booking identifier
     * @return the booking response
     * @throws BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan
     */
    @Override
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        return bookingMapper.toResponseDTO(booking);
    }

    /**
     * Updates an existing booking with new details.
     *
     * @param id the booking identifier
     * @param requestDTO the updated booking data
     * @return the updated booking response
     * @throws BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan
     */
    @Override
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO requestDTO) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        bookingMapper.updateEntityFromDTO(requestDTO, booking);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDTO(updatedBooking);
    }

    /**
     * Deletes a booking by its identifier.
     *
     * @param id the booking identifier
     * @throws BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan
     */
    @Override
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        bookingRepository.delete(booking);
    }

    /**
     * Retrieves all bookings in the system.
     *
     * @return list of all bookings
     * @author Ioana-Loredana Stan
     */
    @Override
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all bookings for a specific user.
     *
     * @param userId the user identifier
     * @return list of bookings for the user
     * @author Ioana-Loredana Stan
     */
    @Override
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all bookings with a specific status.
     *
     * @param status the booking status (PENDING, CONFIRMED, CANCELLED)
     * @return list of bookings with the specified status
     * @author Ioana-Loredana Stan
     */
    @Override
    public List<BookingResponseDTO> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status).stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all bookings sorted by the specified field and order.
     *
     * @param sortBy the field to sort by (screeningTime, price, createdAt)
     * @param order the sort order (asc, desc)
     * @return list of sorted bookings
     * @author Ioana-Loredana Stan
     */
    @Override
    public List<BookingResponseDTO> sortBookings(String sortBy, String order) {
        List<Booking> bookings = bookingRepository.findAll();

        return bookings.stream()
                .sorted((b1, b2) -> {
                    int comparison = 0;
                    if ("screeningTime".equals(sortBy)) {
                        comparison = b1.getScreeningTime().compareTo(b2.getScreeningTime());
                    } else if ("price".equals(sortBy)) {
                        comparison = Double.compare(b1.getPrice() != null ? b1.getPrice() : 0,
                                b2.getPrice() != null ? b2.getPrice() : 0);
                    } else if ("createdAt".equals(sortBy)) {
                        comparison = b1.getCreatedAt().compareTo(b2.getCreatedAt());
                    }
                    return "desc".equalsIgnoreCase(order) ? -comparison : comparison;
                })
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new booking with validation from external services.
     * Validates movie and user existence by calling movies-service and users-service.
     *
     * @param requestDTO the booking request data containing userId and movieId
     * @return the created booking with populated movie and user data
     * @throws BookingNotFoundException if movie or user not found
     * @author Ioana-Loredana Stan - Viza 3
     */
    @Override
    public BookingResponseDTO createBookingWithValidation(BookingRequestDTO requestDTO) {
        MovieResponseDTO movie = restTemplate.getForObject(
                moviesServiceUrl + "/api/movies/" + requestDTO.getMovieId(),
                MovieResponseDTO.class
        );

        UserResponseDTO user = restTemplate.getForObject(
                usersServiceUrl + "/api/users/" + requestDTO.getUserId(),
                UserResponseDTO.class
        );

        if (movie == null) {
            throw new BookingNotFoundException("Movie not found with id: " + requestDTO.getMovieId());
        }

        if (user == null) {
            throw new BookingNotFoundException("User not found with id: " + requestDTO.getUserId());
        }

        Booking booking = bookingMapper.toEntity(requestDTO);
        booking.setMovieTitle(movie.getTitle());
        booking.setUserEmail(user.getEmail());
        booking.setStatus("PENDING");

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDTO(savedBooking);
    }

    /**
     * Confirms a booking by updating its status to CONFIRMED.
     * Retrieves user details from users-service for notification purposes.
     *
     * @param id the booking identifier
     * @return the confirmed booking response with user email
     * @throws BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan - Viza 3
     */
    @Override
    public BookingResponseDTO confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        UserResponseDTO user = restTemplate.getForObject(
                usersServiceUrl + "/api/users/" + booking.getUserId(),
                UserResponseDTO.class
        );

        if (user != null) {
            booking.setUserEmail(user.getEmail());
        }

        booking.setStatus("CONFIRMED");
        Booking confirmedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDTO(confirmedBooking);
    }

    /**
     * Retrieves a booking with enriched movie and user details from external services.
     *
     * @param id the booking identifier
     * @return the booking response with complete movie and user information
     * @throws BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan - Viza 3
     */
    @Override
    public Map<String, Object> getEnrichedBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        MovieResponseDTO movie = restTemplate.getForObject(
                moviesServiceUrl + "/api/movies/" + booking.getMovieId(),
                MovieResponseDTO.class
        );

        UserResponseDTO user = restTemplate.getForObject(
                usersServiceUrl + "/api/users/" + booking.getUserId(),
                UserResponseDTO.class
        );

        Map<String, Object> enrichedBooking = new HashMap<>();
        enrichedBooking.put("booking", bookingMapper.toResponseDTO(booking));
        enrichedBooking.put("movie", movie);
        enrichedBooking.put("user", user);

        return enrichedBooking;
    }
}
