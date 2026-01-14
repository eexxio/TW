package com.cinema.bookings.filter;

import com.cinema.bookings.dto.BookingResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingStatusFilterTest {

    private BookingStatusFilter filter;
    private ObjectMapper objectMapper;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        filter = new BookingStatusFilter(objectMapper);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }


    @Test
    void testFilterSkipsNonBookingsEndpoints() throws Exception {
        request.setRequestURI("/api/movies/1");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Booking-Status")).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testFilterHandlesEmptyResponse() throws Exception {
        request.setRequestURI("/api/bookings/1");

        doAnswer(invocation -> {
            response.setContentType("application/json");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Booking-Status")).isNull();
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void testFilterHandlesNonJsonResponse() throws Exception {
        request.setRequestURI("/api/bookings/1");

        doAnswer(invocation -> {
            response.getOutputStream().write("Plain text response".getBytes());
            response.setContentType("text/plain");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Booking-Status")).isNull();
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void testFilterHandlesInvalidJson() throws Exception {
        request.setRequestURI("/api/bookings/1");

        doAnswer(invocation -> {
            response.getOutputStream().write("invalid json".getBytes());
            response.setContentType("application/json");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Booking-Status")).isNull();
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void testFilterHandlesEmptyBookingList() throws Exception {
        request.setRequestURI("/api/bookings");

        List<BookingResponseDTO> bookings = Arrays.asList();
        String jsonResponse = objectMapper.writeValueAsString(bookings);

        doAnswer(invocation -> {
            response.getOutputStream().write(jsonResponse.getBytes());
            response.setContentType("application/json");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Booking-Status")).isNull();
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void testFilterHandlesNullStatus() throws Exception {
        request.setRequestURI("/api/bookings/1");

        BookingResponseDTO booking = new BookingResponseDTO();
        booking.setId(1L);
        booking.setStatus(null);

        String jsonResponse = objectMapper.writeValueAsString(booking);

        doAnswer(invocation -> {
            response.getOutputStream().write(jsonResponse.getBytes());
            response.setContentType("application/json");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Booking-Status")).isNull();
        verify(filterChain).doFilter(any(), any());
    }
}
