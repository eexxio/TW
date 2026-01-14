package com.cinema.bookings.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration class for RestTemplate to enable HTTP communication
 * with other microservices (movies-service and users-service).
 *
 * @author Ioana-Loredana Stan - Viza 3
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and configures a RestTemplate bean for inter-service communication.
     *
     * Configured with:
     * - Connect timeout: 5 seconds
     * - Read timeout: 5 seconds
     * - Error handler for graceful failure handling
     *
     * @param builder the RestTemplateBuilder provided by Spring Boot
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}
