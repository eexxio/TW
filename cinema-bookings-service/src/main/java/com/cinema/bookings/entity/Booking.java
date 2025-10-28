package com.cinema.bookings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "movie_title")
    private String movieTitle;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "screening_time", nullable = false)
    private LocalDateTime screeningTime;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "seat_row", length = 5)
    private String seatRow;

    @Column(precision = 10, scale = 2)
    private Double price;

    @Column(length = 20)
    private String status = "PENDING";
}
