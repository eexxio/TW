package com.cinema.gateway.controller;

import com.cinema.gateway.dto.LoginRequest;
import com.cinema.gateway.dto.LoginResponse;
import com.cinema.gateway.dto.UserDetailsDTO;
import com.cinema.gateway.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for authentication endpoints.
 * Handles user login and current user information retrieval.
 * @author Tudor
 * Task 2 - Viza 2: Authentication endpoints
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Login endpoint - authenticates user and returns JWT token.
     * @author Tudor
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticate(loginRequest)
                .map(ResponseEntity::ok);
    }

    /**
     * Get current user details from JWT token.
     * @author Tudor
     */
    @GetMapping("/me")
    public Mono<ResponseEntity<UserDetailsDTO>> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        String token = authorizationHeader.substring(7);
        return authService.getCurrentUser(token)
                .map(ResponseEntity::ok);
    }
}
