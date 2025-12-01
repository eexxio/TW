package com.cinema.gateway.service;

import com.cinema.gateway.dto.LoginRequest;
import com.cinema.gateway.dto.LoginResponse;
import com.cinema.gateway.dto.UserDetailsDTO;
import com.cinema.gateway.exception.UnauthorizedException;
import com.cinema.gateway.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Authentication service for handling user login and token generation.
 * @author Tudor
 * Task 2 - Viza 2: Authentication service implementation
 */
@Service
public class AuthService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Authenticate user credentials and generate JWT token.
     * Validates credentials via Users Service and creates JWT with user claims.
     * @author Tudor
     */
    public Mono<LoginResponse> authenticate(LoginRequest loginRequest) {
        return userServiceClient.validateCredentials(loginRequest.getEmail(), loginRequest.getPassword())
                .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid email or password")))
                .map(userDetails -> {
                    // Generate JWT token with user information - Tudor
                    String token = jwtUtil.generateToken(
                            userDetails.getUserId(),
                            userDetails.getEmail(),
                            userDetails.getRole()
                    );

                    return LoginResponse.builder()
                            .token(token)
                            .userId(userDetails.getUserId())
                            .email(userDetails.getEmail())
                            .role(userDetails.getRole())
                            .expiresIn(jwtExpiration)
                            .build();
                });
    }

    public Mono<UserDetailsDTO> getCurrentUser(String token) {
        if (!jwtUtil.validateToken(token)) {
            return Mono.error(new UnauthorizedException("Invalid or expired token"));
        }

        String email = jwtUtil.extractEmail(token);
        Long userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        return Mono.just(UserDetailsDTO.builder()
                .userId(userId)
                .email(email)
                .role(role)
                .build());
    }
}
