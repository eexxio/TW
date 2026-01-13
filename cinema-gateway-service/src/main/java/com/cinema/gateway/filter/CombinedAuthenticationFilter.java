package com.cinema.gateway.filter;

import com.cinema.gateway.config.IamConfig;
import com.cinema.gateway.security.JwtUtil;
import com.cinema.gateway.service.IamAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CombinedAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IamAuthenticationService iamAuthenticationService;

    @Autowired
    private IamConfig iamConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authenticateWithJwt(exchange, chain, authorizationHeader);
        }

        String iamHeader = request.getHeaders().getFirst("X-Google-Service-Account");
        if (iamHeader != null && !iamHeader.isEmpty() && iamConfig.isEnabled()) {
            return authenticateWithIam(exchange, chain, iamHeader);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> authenticateWithJwt(ServerWebExchange exchange, GatewayFilterChain chain,
                                            String authorizationHeader) {
        String token = authorizationHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Long userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private Mono<Void> authenticateWithIam(ServerWebExchange exchange, GatewayFilterChain chain,
                                            String iamHeader) {
        try {
            IamAuthenticationService.ServiceAccountInfo serviceAccountInfo =
                    iamAuthenticationService.validateAndExtractServiceAccount(iamHeader);

            if (!serviceAccountInfo.isValid()) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String serviceAccountEmail = serviceAccountInfo.getEmail();
            String role = serviceAccountInfo.getRole();
            Long userId = (long) serviceAccountEmail.hashCode();

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(Math.abs(userId)))
                    .header("X-User-Email", serviceAccountEmail)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
