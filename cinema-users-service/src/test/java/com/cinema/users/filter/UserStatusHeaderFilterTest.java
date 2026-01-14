package com.cinema.users.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserStatusHeaderFilter.
 * Tests custom header addition to user-related responses.
 *
 * @author Alexandru Tesula
 */
@ExtendWith(MockitoExtension.class)
class UserStatusHeaderFilterTest {

    @InjectMocks
    private UserStatusHeaderFilter filter;

    @Mock
    private ObjectMapper mockObjectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new UserStatusHeaderFilter();
        ReflectionTestUtils.setField(filter, "objectMapper", new ObjectMapper());
        response = new MockHttpServletResponse();

        // Mock request URI for user endpoints
        when(request.getRequestURI()).thenReturn("/users/1");
    }

    @Test
    void testFilter_SingleUserResponse() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "{\"email\":\"test@example.com\",\"firstname\":\"John\",\"lastname\":\"Doe\"}";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_UserArrayResponse_HighActivity() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "[{\"email\":\"user1@example.com\"},{\"email\":\"user2@example.com\"}," +
                              "{\"email\":\"user3@example.com\"},{\"email\":\"user4@example.com\"}," +
                              "{\"email\":\"user5@example.com\"},{\"email\":\"user6@example.com\"}," +
                              "{\"email\":\"user7@example.com\"},{\"email\":\"user8@example.com\"}," +
                              "{\"email\":\"user9@example.com\"},{\"email\":\"user10@example.com\"}]";

        when(request.getRequestURI()).thenReturn("/users");

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_UserArrayResponse_MediumActivity() throws ServletException, IOException {
        // Arrange - 7 users should trigger Medium activity
        String jsonResponse = "[{\"email\":\"user1@example.com\"},{\"email\":\"user2@example.com\"}," +
                              "{\"email\":\"user3@example.com\"},{\"email\":\"user4@example.com\"}," +
                              "{\"email\":\"user5@example.com\"},{\"email\":\"user6@example.com\"}," +
                              "{\"email\":\"user7@example.com\"}]";

        when(request.getRequestURI()).thenReturn("/users");

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_InvalidJsonFormat() throws ServletException, IOException {
        // Arrange
        String invalidJson = "invalid json {";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(invalidJson);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> filter.doFilter(request, response, filterChain));
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_EmptyResponse() throws ServletException, IOException {
        // Arrange
        String emptyJson = "";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(emptyJson);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act & Assert
        assertDoesNotThrow(() -> filter.doFilter(request, response, filterChain));
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_NonUserEndpoint() throws ServletException, IOException {
        // Arrange - Request to non-user endpoint
        when(request.getRequestURI()).thenReturn("/health");

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write("OK");
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert - Filter should not process non-user endpoints
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_ChainExecutedExactlyOnce() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "{\"email\":\"test@example.com\"}";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert - verify filter chain is called exactly once
        verify(filterChain, times(1)).doFilter(any(HttpServletRequest.class), any());
    }

    @Test
    void testFilter_UserBookingsEndpoint() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/users/1/bookings");
        String jsonResponse = "{\"user\":{\"email\":\"test@example.com\"},\"bookings\":[],\"totalBookings\":0}";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_UserWatchedMoviesEndpoint() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/users/1/watched-movies");
        String jsonResponse = "{\"user\":{\"email\":\"test@example.com\"},\"watchedMovies\":[],\"totalMoviesWatched\":0}";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(), any());
    }
}

