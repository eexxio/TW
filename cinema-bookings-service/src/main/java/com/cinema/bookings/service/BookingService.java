package com.cinema.bookings.service;

import com.cinema.bookings.dto.BookingRequestDTO;
import com.cinema.bookings.dto.BookingResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface for booking operations.
 *
 * @author Ioana-Loredana Stan - Viza 3
 */
public interface BookingService {

    /**
     * Creates a new booking with the provided details.
     *
     * @param requestDTO the booking request data
     * @return the created booking response
     * @author Ioana-Loredana Stan
     */
    BookingResponseDTO createBooking(BookingRequestDTO requestDTO);

    /**
     * Creates a new booking with validation from external services.
     * Validates movie and user existence by calling movies-service and users-service.
     *
     * @param requestDTO the booking request data containing userId and movieId
     * @return the created booking with populated movie and user data
     * @throws com.cinema.bookings.exception.BookingNotFoundException if movie or user not found
     * @author Ioana-Loredana Stan - Viza 3
     */
    BookingResponseDTO createBookingWithValidation(BookingRequestDTO requestDTO);

    /**
     * Retrieves a booking by its unique identifier.
     *
     * @param id the booking identifier
     * @return the booking response
     * @throws com.cinema.bookings.exception.BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan
     */
    BookingResponseDTO getBookingById(Long id);

    /**
     * Retrieves a booking with enriched movie and user details from external services.
     *
     * @param id the booking identifier
     * @return the booking response with complete movie and user information
     * @throws com.cinema.bookings.exception.BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan - Viza 3
     */
    Map<String, Object> getEnrichedBooking(Long id);

    /**
     * Updates an existing booking with new details.
     *
     * @param id the booking identifier
     * @param requestDTO the updated booking data
     * @return the updated booking response
     * @throws com.cinema.bookings.exception.BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan
     */
    BookingResponseDTO updateBooking(Long id, BookingRequestDTO requestDTO);

    /**
     * Confirms a booking by updating its status to CONFIRMED.
     * Retrieves user details from users-service for notification purposes.
     *
     * @param id the booking identifier
     * @return the confirmed booking response with user email
     * @throws com.cinema.bookings.exception.BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan - Viza 3
     */
    BookingResponseDTO confirmBooking(Long id);

    /**
     * Deletes a booking by its identifier.
     *
     * @param id the booking identifier
     * @throws com.cinema.bookings.exception.BookingNotFoundException if booking not found
     * @author Ioana-Loredana Stan
     */
    void deleteBooking(Long id);

    /**
     * Retrieves all bookings in the system.
     *
     * @return list of all bookings
     * @author Ioana-Loredana Stan
     */
    List<BookingResponseDTO> getAllBookings();

    /**
     * Retrieves all bookings for a specific user.
     *
     * @param userId the user identifier
     * @return list of bookings for the user
     * @author Ioana-Loredana Stan
     */
    List<BookingResponseDTO> getBookingsByUserId(Long userId);

    /**
     * Retrieves all bookings with a specific status.
     *
     * @param status the booking status (PENDING, CONFIRMED, CANCELLED)
     * @return list of bookings with the specified status
     * @author Ioana-Loredana Stan
     */
    List<BookingResponseDTO> getBookingsByStatus(String status);

    /**
     * Retrieves all bookings sorted by the specified field and order.
     *
     * @param sortBy the field to sort by (screeningTime, price, createdAt)
     * @param order the sort order (asc, desc)
     * @return list of sorted bookings
     * @author Ioana-Loredana Stan
     */
    List<BookingResponseDTO> sortBookings(String sortBy, String order);
}
