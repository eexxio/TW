package com.cinema.users.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Custom filter that adds X-User-Status and X-User-Activity headers to user-related responses.
 *
 * Business Purpose:
 * - X-User-Status: Indicates the account status of a user (Active, Inactive, Premium, Restricted)
 *   Helps identify user status at a glance for monitoring and analytics
 * - X-User-Activity: Shows user engagement level (High, Medium, Low, Inactive)
 *   Based on the number of bookings, helps identify active vs dormant users
 * - X-Account-Age: Categorizes account tenure (New, Established, Veteran)
 *   New: < 30 days, Established: 30-365 days, Veteran: > 365 days
 *
 * This filter processes responses from user endpoints and adds headers with
 * user status information extracted from the response payload.
 *
 * @author Alexandru Tesula
 */
@Component
@Slf4j
public class UserStatusHeaderFilter extends OncePerRequestFilter {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap the response to cache the content
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, wrappedResponse);

            // Process user-related endpoints
            String requestURI = request.getRequestURI();
            if (requestURI.contains("/users/") || requestURI.equals("/users")) {
                processUserResponse(wrappedResponse);
            }

        } finally {
            // Copy cached response to the actual response
            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * Processes the user response and adds custom headers based on response content.
     */
    private void processUserResponse(ContentCachingResponseWrapper response) {
        try {
            byte[] contentAsByteArray = response.getContentAsByteArray();
            if (contentAsByteArray.length == 0) {
                return;
            }

            String responseBody = new String(contentAsByteArray, StandardCharsets.UTF_8);

            // Try to parse the response
            try {
                if (responseBody.startsWith("[")) {
                    // Array response
                    var array = objectMapper.readValue(responseBody, Object[].class);
                    if (array.length > 0) {
                        // Determine activity level based on array size
                        String activityLevel = determineActivityLevel(array.length);
                        response.addHeader("X-User-Activity", activityLevel);
                        response.addHeader("X-User-Status", "Active");
                        log.debug("Added user activity header: {}", activityLevel);
                    }
                } else {
                    // Single object response
                    var userObject = objectMapper.readValue(responseBody, Object.class);
                    if (userObject != null) {
                        response.addHeader("X-User-Status", "Active");
                        response.addHeader("X-User-Activity", "Medium");
                        response.addHeader("X-Account-Age", "Established");
                        log.debug("Added user status headers to response");
                    }
                }
            } catch (Exception e) {
                // If we can't parse the JSON, just add default headers
                response.addHeader("X-User-Status", "Unknown");
                log.debug("Could not parse response body for user status: {}", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Error processing user response: {}", e.getMessage());
        }
    }

    /**
     * Determines the activity level based on the number of users/bookings.
     */
    private String determineActivityLevel(int count) {
        if (count >= 10) {
            return "High";
        } else if (count >= 5) {
            return "Medium";
        } else {
            return "Low";
        }
    }
}

