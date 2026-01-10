package com.cinema.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Spring Security configuration for the gateway.
 * Configures public endpoints and delegates authorization to custom filters.
 * @author Alexandru
 * Task 2 - Viza 2: Security configuration
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configure Spring Security filter chain.
     * Permits public endpoints and delegates authorization to gateway filters.
     * @author Alexandru
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Public endpoints - Alexandru
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/users/create").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                        .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}
