package com.cinema.users.client;

import com.cinema.users.dto.BookingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Client for communicating with the Bookings microservice.
 * Handles all HTTP requests to the bookings service and provides
 * error handling and timeout configuration for inter-service communication.
 *
 * @author Alexandru Tesula
 */
@Component
@Slf4j
public class BookingServiceClient {

    private final WebClient webClient;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    public BookingServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${service.bookings.url:http://localhost:8083}") String bookingsServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(bookingsServiceUrl)
                .build();
    }

    /**
     * Retrieves all bookings for a specific user.
     *
     * @param userId the ID of the user
     * @return list of bookings for the user, empty list if service is unavailable
     */
    public List<BookingDTO> getBookingsByUserId(Long userId) {
        try {
            log.info("Fetching bookings for user ID: {}", userId);

            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/bookings/user/{userId}")
                            .queryParam("userId", userId)
                            .build(userId))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<BookingDTO>>() {})
                    .timeout(TIMEOUT)
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        log.error("Error fetching bookings for user {}: {} - {}",
                                userId, ex.getStatusCode(), ex.getMessage());
                        return Mono.just(Collections.emptyList());
                    })
                    .onErrorResume(Exception.class, ex -> {
                        log.error("Unexpected error fetching bookings for user {}: {}",
                                userId, ex.getMessage());
                        return Mono.just(Collections.emptyList());
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch bookings for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves the count of bookings for a specific user.
     *
     * @param userId the ID of the user
     * @return number of bookings for the user, 0 if service is unavailable
     */
    public long getBookingCountByUserId(Long userId) {
        try {
            log.info("Fetching booking count for user ID: {}", userId);

            List<BookingDTO> bookings = getBookingsByUserId(userId);
            return bookings.size();
        } catch (Exception e) {
            log.error("Failed to get booking count for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    /**
     * Retrieves all bookings from the bookings service.
     *
     * @return list of all bookings, empty list if service is unavailable
     */
    public List<BookingDTO> getAllBookings() {
        try {
            log.info("Fetching all bookings");

            return webClient.get()
                    .uri("/api/bookings")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<BookingDTO>>() {})
                    .timeout(TIMEOUT)
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        log.error("Error fetching all bookings: {} - {}",
                                ex.getStatusCode(), ex.getMessage());
                        return Mono.just(Collections.emptyList());
                    })
                    .onErrorResume(Exception.class, ex -> {
                        log.error("Unexpected error fetching all bookings: {}", ex.getMessage());
                        return Mono.just(Collections.emptyList());
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch all bookings: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}

