package com.cinema.users.client;

import com.cinema.users.dto.MovieDTO;
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
 * Client for communicating with the Movies microservice.
 * Handles all HTTP requests to the movies service and provides
 * error handling and timeout configuration for inter-service communication.
 *
 * @author Alexandru Tesula
 */
@Component
@Slf4j
public class MovieServiceClient {

    private final WebClient webClient;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    public MovieServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${service.movies.url:http://localhost:8081}") String moviesServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(moviesServiceUrl)
                .build();
    }

    /**
     * Retrieves a movie by its ID.
     *
     * @param movieId the ID of the movie
     * @return the movie DTO, or null if service is unavailable or movie not found
     */
    public MovieDTO getMovieById(Long movieId) {
        try {
            log.info("Fetching movie with ID: {}", movieId);

            return webClient.get()
                    .uri("/api/movies/{id}", movieId)
                    .retrieve()
                    .bodyToMono(MovieDTO.class)
                    .timeout(TIMEOUT)
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        log.error("Error fetching movie {}: {} - {}",
                                movieId, ex.getStatusCode(), ex.getMessage());
                        return Mono.empty();
                    })
                    .onErrorResume(Exception.class, ex -> {
                        log.error("Unexpected error fetching movie {}: {}",
                                movieId, ex.getMessage());
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch movie {}: {}", movieId, e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves all movies from the movies service.
     *
     * @return list of all movies, empty list if service is unavailable
     */
    public List<MovieDTO> getAllMovies() {
        try {
            log.info("Fetching all movies");

            return webClient.get()
                    .uri("/api/movies")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<MovieDTO>>() {})
                    .timeout(TIMEOUT)
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        log.error("Error fetching all movies: {} - {}",
                                ex.getStatusCode(), ex.getMessage());
                        return Mono.just(Collections.emptyList());
                    })
                    .onErrorResume(Exception.class, ex -> {
                        log.error("Unexpected error fetching all movies: {}", ex.getMessage());
                        return Mono.just(Collections.emptyList());
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch all movies: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}

