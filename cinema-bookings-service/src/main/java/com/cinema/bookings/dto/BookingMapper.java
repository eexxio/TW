package com.cinema.bookings.dto;

import com.cinema.bookings.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDTO requestDTO) {
        Booking booking = new Booking();
        booking.setUserId(requestDTO.getUserId());
        booking.setMovieId(requestDTO.getMovieId());
        booking.setMovieTitle(requestDTO.getMovieTitle());
        booking.setUserEmail(requestDTO.getUserEmail());
        booking.setScreeningTime(requestDTO.getScreeningTime());
        booking.setSeatNumber(requestDTO.getSeatNumber());
        booking.setSeatRow(requestDTO.getSeatRow());
        booking.setPrice(requestDTO.getPrice());
        booking.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : "PENDING");
        return booking;
    }

    public BookingResponseDTO toResponseDTO(Booking booking) {
        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setId(booking.getId());
        responseDTO.setUserId(booking.getUserId());
        responseDTO.setMovieId(booking.getMovieId());
        responseDTO.setMovieTitle(booking.getMovieTitle());
        responseDTO.setUserEmail(booking.getUserEmail());
        responseDTO.setScreeningTime(booking.getScreeningTime());
        responseDTO.setSeatNumber(booking.getSeatNumber());
        responseDTO.setSeatRow(booking.getSeatRow());
        responseDTO.setPrice(booking.getPrice());
        responseDTO.setStatus(booking.getStatus());
        responseDTO.setCreatedAt(booking.getCreatedAt());
        responseDTO.setUpdatedAt(booking.getUpdatedAt());
        return responseDTO;
    }

    public void updateEntityFromDTO(BookingRequestDTO requestDTO, Booking booking) {
        if (requestDTO.getUserId() != null) {
            booking.setUserId(requestDTO.getUserId());
        }
        if (requestDTO.getMovieId() != null) {
            booking.setMovieId(requestDTO.getMovieId());
        }
        if (requestDTO.getMovieTitle() != null) {
            booking.setMovieTitle(requestDTO.getMovieTitle());
        }
        if (requestDTO.getUserEmail() != null) {
            booking.setUserEmail(requestDTO.getUserEmail());
        }
        if (requestDTO.getScreeningTime() != null) {
            booking.setScreeningTime(requestDTO.getScreeningTime());
        }
        if (requestDTO.getSeatNumber() != null) {
            booking.setSeatNumber(requestDTO.getSeatNumber());
        }
        if (requestDTO.getSeatRow() != null) {
            booking.setSeatRow(requestDTO.getSeatRow());
        }
        if (requestDTO.getPrice() != null) {
            booking.setPrice(requestDTO.getPrice());
        }
        if (requestDTO.getStatus() != null) {
            booking.setStatus(requestDTO.getStatus());
        }
    }
}
