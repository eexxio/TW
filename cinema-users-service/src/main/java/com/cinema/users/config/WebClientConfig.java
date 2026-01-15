package com.cinema.users.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for WebClient used for inter-service communication.
 * Provides a reusable WebClient builder for calling Bookings and Movies services.
 * Configured with tracing support to propagate trace context across services.
 *
 * @author Alexandru Tesula
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder(ObservationRegistry observationRegistry) {
        return WebClient.builder()
                .observationRegistry(observationRegistry);
    }
}

