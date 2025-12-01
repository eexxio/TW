package com.cinema.gateway.security;

import com.cinema.gateway.dto.BookingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Custom authorization manager for role-based and resource-based access control.
 * Implements filters for admin-only operations, user resource ownership, and booking ownership.
 * @author Alexandru
 * Task 2 - Viza 2: Authorization filters implementation
 */
@Component
public class CustomAuthorizationManager {

    private final WebClient webClient;

    @Value("${service.bookings.url}")
    private String bookingsServiceUrl;

    public CustomAuthorizationManager(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Filter that restricts access to ADMIN role only.
     * Used for operations like creating/updating/deleting movies, managing users, etc.
     * @author Alexandru
     */
    public GatewayFilter adminOnly() {
        return (exchange, chain) -> {
            String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");

            // Only ADMIN role can proceed - Alexandru
            if (!"ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    /**
     * Filter that verifies booking ownership before allowing access.
     * ADMIN users can access all bookings, regular users can only access their own.
     * @author Alexandru
     */
    public GatewayFilter bookingOwnershipFilter() {
        return (exchange, chain) -> {
            String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");
            String userIdHeader = exchange.getRequest().getHeaders().getFirst("X-User-Id");

            if (userIdHeader == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Long userId = Long.parseLong(userIdHeader);

            // ADMIN can access all bookings - Alexandru
            if ("ADMIN".equals(role)) {
                return chain.filter(exchange);
            }

            String path = exchange.getRequest().getPath().toString();

            // Check booking ownership for specific booking ID - Alexandru
            if (path.matches(".*/api/bookings/\\d+")) {
                String bookingId = path.substring(path.lastIndexOf("/") + 1);
                return canAccessBooking(userId, Long.parseLong(bookingId))
                        .flatMap(canAccess -> {
                            if (canAccess) {
                                return chain.filter(exchange);
                            } else {
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                        });
            }

            if (path.matches(".*/api/bookings/user/\\d+")) {
                String pathUserId = path.substring(path.lastIndexOf("/") + 1);
                if (userId.equals(Long.parseLong(pathUserId))) {
                    return chain.filter(exchange);
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }

            if (path.equals("/api/bookings") && "GET".equals(exchange.getRequest().getMethod().name())) {
                return chain.filter(exchange);
            }

            return chain.filter(exchange);
        };
    }

    /**
     * Filter that ensures users can only update their own profile.
     * Checks if the email in the path matches the authenticated user's email.
     * ADMIN users can update any profile.
     * @author Alexandru
     */
    public GatewayFilter userResourceOwnership() {
        return (exchange, chain) -> {
            String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");
            String userEmail = exchange.getRequest().getHeaders().getFirst("X-User-Email");

            // ADMIN can update any user profile - Alexandru
            if ("ADMIN".equals(role)) {
                return chain.filter(exchange);
            }

            String path = exchange.getRequest().getPath().toString();

            // Verify user can only update their own profile - Alexandru
            if (path.contains("/users/update/")) {
                String emailInPath = path.substring(path.lastIndexOf("/") + 1);
                if (userEmail != null && userEmail.equals(emailInPath)) {
                    return chain.filter(exchange);
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }

            return chain.filter(exchange);
        };
    }

    /**
     * Helper method to verify if a user owns a specific booking.
     * Queries the bookings service to check booking ownership.
     * @author Alexandru
     */
    private Mono<Boolean> canAccessBooking(Long userId, Long bookingId) {
        return webClient.get()
                .uri(bookingsServiceUrl + "/api/bookings/" + bookingId)
                .retrieve()
                .bodyToMono(BookingDTO.class)
                .map(booking -> booking.getUserId().equals(userId))
                .onErrorReturn(false);
    }
}
