package com.cinema.gateway.service;

import com.cinema.gateway.dto.UserDetailsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private final WebClient webClient;

    @Value("${service.users.url}")
    private String usersServiceUrl;

    public UserServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<UserDetailsDTO> validateCredentials(String email, String password) {
        return webClient.post()
                .uri(usersServiceUrl + "/users/login?email=" + email + "&password=" + password)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    if (response != null && response.contains("Successfully logged in")) {
                        return getUserByEmail(email);
                    } else {
                        return Mono.empty();
                    }
                })
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<UserDetailsDTO> getUserByEmail(String email) {
        return webClient.get()
                .uri(usersServiceUrl + "/users")
                .retrieve()
                .bodyToFlux(UserDetailsDTO.class)
                .filter(user -> email.equals(user.getEmail()))
                .next()
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<Boolean> userExists(Long userId) {
        return webClient.get()
                .uri(usersServiceUrl + "/users")
                .retrieve()
                .bodyToFlux(UserDetailsDTO.class)
                .filter(user -> userId.equals(user.getUserId()))
                .hasElements();
    }
}
