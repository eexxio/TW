package com.cinema.gateway.config;

import com.cinema.gateway.filter.CombinedAuthenticationFilter;
import com.cinema.gateway.security.CustomAuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Gateway route configuration with authentication and authorization filters.
 * Defines routes for all microservices with appropriate security filters.
 * @author Ioana
 * Task 2 - Viza 2: Gateway routes and security configuration
 */
@Configuration
public class GatewayConfig {

    @Autowired
    private CombinedAuthenticationFilter combinedAuthenticationFilter;

    @Autowired
    private CustomAuthorizationManager authorizationManager;

    @Value("${service.users.url}")
    private String usersServiceUrl;

    @Value("${service.bookings.url}")
    private String bookingsServiceUrl;

    @Value("${service.movies.url}")
    private String moviesServiceUrl;

    /**
     * Configure all gateway routes with appropriate security filters.
     * @author Ioana
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Public movie browsing - no authentication required - Ioana
                .route("movies-public-read", r -> r
                        .path("/api/movies/**")
                        .and()
                        .method(HttpMethod.GET)
                        .uri(moviesServiceUrl))

                // Public user registration - Ioana
                .route("user-registration", r -> r
                        .path("/users/create")
                        .and()
                        .method(HttpMethod.POST)
                        .uri(usersServiceUrl))

                // ADMIN-only movie operations - Ioana
                .route("movies-admin-create", r -> r
                        .path("/api/movies")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f
                                .filter(combinedAuthenticationFilter)
                                .filter(authorizationManager.adminOnly()))
                        .uri(moviesServiceUrl))

                .route("movies-admin-update", r -> r
                        .path("/api/movies/**")
                        .and()
                        .method(HttpMethod.PUT)
                        .filters(f -> f
                                .filter(combinedAuthenticationFilter)
                                .filter(authorizationManager.adminOnly()))
                        .uri(moviesServiceUrl))

                .route("movies-admin-delete", r -> r
                        .path("/api/movies/**")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(combinedAuthenticationFilter)
                                .filter(authorizationManager.adminOnly()))
                        .uri(moviesServiceUrl))

                // ADMIN-only user management operations - Ioana
                .route("users-delete-admin", r -> r
                        .path("/users/delete/**")
                        .and()
                        .method(HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(combinedAuthenticationFilter)
                                .filter(authorizationManager.adminOnly()))
                        .uri(usersServiceUrl))

                .route("users-filter-admin", r -> r
                        .path("/users/filter")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f
                                .filter(combinedAuthenticationFilter)
                                .filter(authorizationManager.adminOnly()))
                        .uri(usersServiceUrl))

                .route("users-sort-admin", r -> r
                        .path("/users/sort")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f
                                .filter(combinedAuthenticationFilter)
                                .filter(authorizationManager.adminOnly()))
                        .uri(usersServiceUrl))

                // User profile update with ownership verification - Ioana
                .route("users-update", r -> r
                        .path("/users/update/**")
                        .and()
                        .method(HttpMethod.PUT)
                        .filters(f -> f
                                .filter(combinedAuthenticationFilter)
                                .filter(authorizationManager.userResourceOwnership()))
                        .uri(usersServiceUrl))

                // Authenticated user operations - Ioana
                .route("users-authenticated", r -> r
                        .path("/users", "/users/search")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f.filter(combinedAuthenticationFilter))
                        .uri(usersServiceUrl))

                // Booking operations with ownership verification - Ioana
                .route("bookings-authenticated", r -> r
                        .path("/api/bookings/**")
                        .filters(f -> f
                                .filter(combinedAuthenticationFilter)
                                .filter(authorizationManager.bookingOwnershipFilter()))
                        .uri(bookingsServiceUrl))

                .build();
    }
}
