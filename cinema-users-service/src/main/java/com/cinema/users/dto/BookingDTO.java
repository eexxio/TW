package com.cinema.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing booking information from the bookings service.
 * Used for cross-service communication between Users and Bookings services.
 *
 * @author Alexandru Tesula
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

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

