package com.cinema.bookings.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RestTemplateConfig
 * Tests that the RestTemplate bean is properly configured
 *
 * @author Ioana-Loredana Stan - Viza 3
 */
class RestTemplateConfigTest {

    @Test
    void testRestTemplateBeanIsCreated() {
        // Given
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplateBuilder builder = new RestTemplateBuilder();

        // When
        RestTemplate restTemplate = config.restTemplate(builder);

        // Then
        assertThat(restTemplate).isNotNull();
    }

    @Test
    void testRestTemplateIsConfigured() {
        // Given
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplateBuilder builder = new RestTemplateBuilder();

        // When
        RestTemplate restTemplate = config.restTemplate(builder);

        // Then
        assertThat(restTemplate).isNotNull();
        assertThat(restTemplate.getRequestFactory()).isNotNull();
    }

    @Test
    void testRestTemplateWithTimeouts() {
        // Given
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5));

        // When
        RestTemplate restTemplate = config.restTemplate(builder);

        // Then
        assertThat(restTemplate).isNotNull();
    }
}
