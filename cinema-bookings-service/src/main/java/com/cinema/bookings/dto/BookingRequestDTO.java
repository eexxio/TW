package com.cinema.bookings.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    private String movieTitle;

    private String userEmail;

    @NotNull(message = "Screening time is required")
    private LocalDateTime screeningTime;

    private Integer seatNumber;

    private String seatRow;

    private Double price;

    private String status;
}
