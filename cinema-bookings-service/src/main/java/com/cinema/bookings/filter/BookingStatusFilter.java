package com.cinema.bookings.filter;

import com.cinema.bookings.dto.BookingResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.List;

@Component
public class BookingStatusFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(BookingStatusFilter.class);
    private static final String HEADER_NAME = "X-Booking-Status";
    private static final String BOOKINGS_API_PREFIX = "/api/bookings";

    private final ObjectMapper objectMapper;

    public BookingStatusFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (!requestUri.startsWith(BOOKINGS_API_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, responseWrapper);
            addBookingStatusHeader(responseWrapper);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private void addBookingStatusHeader(ContentCachingResponseWrapper responseWrapper) {
        try {
            byte[] content = responseWrapper.getContentAsByteArray();

            if (content.length == 0) {
                return;
            }

            String contentType = responseWrapper.getContentType();
            if (contentType == null || !contentType.contains("application/json")) {
                return;
            }

            String status = extractStatusFromJson(content);

            if (status != null) {
                responseWrapper.setHeader(HEADER_NAME, status);
                logger.debug("Added header {}: {} to response", HEADER_NAME, status);
            }

        } catch (Exception e) {
            logger.error("Error processing response for booking status header", e);
        }
    }

    private String extractStatusFromJson(byte[] content) {
        try {
            String json = new String(content);

            if (json.trim().startsWith("{")) {
                BookingResponseDTO booking = objectMapper.readValue(content, BookingResponseDTO.class);
                return booking.getStatus();
            }

            if (json.trim().startsWith("[")) {
                List<BookingResponseDTO> bookings = objectMapper.readValue(
                        content,
                        new TypeReference<List<BookingResponseDTO>>() {}
                );
                return extractFirstStatus(bookings);
            }

        } catch (Exception e) {
            logger.warn("Failed to parse JSON response for status extraction", e);
        }

        return null;
    }

    private String extractFirstStatus(List<BookingResponseDTO> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings.stream()
                .map(BookingResponseDTO::getStatus)
                .filter(status -> status != null)
                .findFirst()
                .orElse(null);
    }
}
