package com.cinema.bookings.service;

import com.cinema.bookings.dto.BookingMapper;
import com.cinema.bookings.dto.BookingRequestDTO;
import com.cinema.bookings.dto.BookingResponseDTO;
import com.cinema.bookings.entity.Booking;
import com.cinema.bookings.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO) {
        Booking booking = bookingMapper.toEntity(requestDTO);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDTO(savedBooking);
    }

    @Override
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        return bookingMapper.toResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO requestDTO) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        bookingMapper.updateEntityFromDTO(requestDTO, booking);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDTO(updatedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        bookingRepository.delete(booking);
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status).stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

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
}
