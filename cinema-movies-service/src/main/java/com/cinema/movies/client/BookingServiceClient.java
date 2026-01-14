package com.cinema.movies.client;

import com.cinema.movies.dto.BookingDTO;
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
 * error handling and timeout configuration.
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
     * Retrieves all bookings for a specific movie.
     *
     * @param movieId the ID of the movie
     * @return list of bookings for the movie, empty list if service is unavailable
     */
    public List<BookingDTO> getBookingsByMovieId(Long movieId) {
        try {
            log.info("Fetching bookings for movie ID: {}", movieId);

            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/bookings")
                            .queryParam("movieId", movieId)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<BookingDTO>>() {})
                    .timeout(TIMEOUT)
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        log.error("Error fetching bookings for movie {}: {} - {}",
                                movieId, ex.getStatusCode(), ex.getMessage());
                        return Mono.just(Collections.emptyList());
                    })
                    .onErrorResume(Exception.class, ex -> {
                        log.error("Unexpected error fetching bookings for movie {}: {}",
                                movieId, ex.getMessage());
                        return Mono.just(Collections.emptyList());
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch bookings for movie {}: {}", movieId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves the count of bookings for a specific movie.
     *
     * @param movieId the ID of the movie
     * @return number of bookings for the movie, 0 if service is unavailable
     */
    public long getBookingCountByMovieId(Long movieId) {
        try {
            log.info("Fetching booking count for movie ID: {}", movieId);

            List<BookingDTO> bookings = getBookingsByMovieId(movieId);
            return bookings.size();
        } catch (Exception e) {
            log.error("Failed to get booking count for movie {}: {}", movieId, e.getMessage());
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
