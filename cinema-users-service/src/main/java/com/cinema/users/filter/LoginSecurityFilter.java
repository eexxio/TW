package com.cinema.users.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class LoginSecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getRequestURI().contains("/login")) {

            httpResponse.setHeader("X-Login-Attempts-Max", "5");
            httpResponse.setHeader("X-Lockout-Duration-Minutes", "15");
            httpResponse.setHeader("X-Password-Hash-Algorithm", "BCrypt");

            String email = httpRequest.getParameter("email");
            String password = httpRequest.getParameter("password");

            if (email == null || email.trim().isEmpty()) {
                System.out.println("Login BLOCKED: Missing email");
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is required in query params");
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                System.out.println("Login BLOCKED: Missing password");
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Password cannot be empty");
                return; // Stop
            }

            if (password.length() < 3) {
                System.out.println("Login BLOCKED: Password too short (Suspicious)");
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Password too short. Security policy enforcement.");
                return; // Stop
            }

            System.out.println("Login Security Check PASSED for: " + email);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("LoginSecurityHeaderFilter INITIALIZED - Login security enabled");
    }

    @Override
    public void destroy() {
        System.out.println("🔒 LoginSecurityHeaderFilter DESTROYED");
    }
}
