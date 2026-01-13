package com.cinema.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {

    private Long id;

    private Long userId;

    private Long movieId;

    private String movieTitle;

    private String userEmail;

    private LocalDateTime screeningTime;

    private Integer seatNumber;

    private String seatRow;

    private Double price;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
