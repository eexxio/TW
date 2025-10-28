package com.cinema.bookings.service;

import com.cinema.bookings.dto.BookingRequestDTO;
import com.cinema.bookings.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {

    BookingResponseDTO createBooking(BookingRequestDTO requestDTO);

    BookingResponseDTO getBookingById(Long id);

    BookingResponseDTO updateBooking(Long id, BookingRequestDTO requestDTO);

    void deleteBooking(Long id);

    List<BookingResponseDTO> getAllBookings();

    List<BookingResponseDTO> getBookingsByUserId(Long userId);

    List<BookingResponseDTO> getBookingsByStatus(String status);

    List<BookingResponseDTO> sortBookings(String sortBy, String order);
}
