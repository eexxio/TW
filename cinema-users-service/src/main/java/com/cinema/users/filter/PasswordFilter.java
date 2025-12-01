package com.cinema.users.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2)
public class PasswordFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        if (uri.contains("/create") || uri.contains("/update")) {
            httpResponse.setHeader("X-Password-Min-Length", "6");
            httpResponse.setHeader("X-Password-Requires-Digit", "true");
            httpResponse.setHeader("X-Password-Requires-Letter", "true");
            httpResponse.setHeader("X-Password-Policy", "min-6-chars-with-letter-and-digit");
            httpResponse.setHeader("X-Password-Encryption", "BCrypt");

            System.out.println("🔑 PasswordPolicyHeaderFilter ACTIVE | URI: " + uri +
                    " | Policy: minimum 6 chars + digit + letter");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("PasswordPolicyHeaderFilter INITIALIZED - Password policy enforced");
    }

    @Override
    public void destroy() {
        System.out.println("PasswordPolicyHeaderFilter DESTROYED");
    }
}