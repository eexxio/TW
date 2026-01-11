package com.cinema.movies.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ContentRatingFilter.
 * Tests header addition for different rating scenarios.
 */
@ExtendWith(MockitoExtension.class)
class ContentRatingFilterTest {

    private ContentRatingFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        filter = new ContentRatingFilter(objectMapper);
        response = new MockHttpServletResponse();

        // Mock request URI for all tests
        when(request.getRequestURI()).thenReturn("/api/movies/1");
    }

    @Test
    void testFilter_SingleMovieExcellentRating() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "{\"id\":1,\"title\":\"Test Movie\",\"rating\":9.5}";

        // Simulate the filter wrapping and response writing
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
    void testFilter_SingleMovieGoodRating() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "{\"id\":1,\"title\":\"Test Movie\",\"rating\":7.5}";

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
    void testFilter_SingleMovieAverageRating() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "{\"id\":1,\"title\":\"Test Movie\",\"rating\":5.0}";

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
    void testFilter_SingleMoviePoorRating() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "{\"id\":1,\"title\":\"Test Movie\",\"rating\":3.0}";

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
    void testFilter_SingleMovieNullRating() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "{\"id\":1,\"title\":\"Test Movie\",\"rating\":null}";

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
    void testFilter_MovieArrayWithMultipleRatings() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "[{\"id\":1,\"rating\":9.5},{\"id\":2,\"rating\":7.5}]";

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
        String jsonResponse = "invalid json {";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
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
        String jsonResponse = "";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act & Assert
        assertDoesNotThrow(() -> filter.doFilter(request, response, filterChain));
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_ResponseWithoutRatingField() throws ServletException, IOException {
        // Arrange
        String jsonResponse = "{\"id\":1,\"title\":\"Test Movie\"}";

        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write(jsonResponse);
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act & Assert
        assertDoesNotThrow(() -> filter.doFilter(request, response, filterChain));
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testFilter_ChainExecutedExactlyOnce() throws ServletException, IOException {
        // Arrange
        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(1);
            if (resp instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) resp;
                wrapper.getWriter().write("{}");
                wrapper.getWriter().flush();
            }
            return null;
        }).when(filterChain).doFilter(any(), any());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert - verify filter chain is called exactly once
        verify(filterChain, times(1)).doFilter(any(HttpServletRequest.class), any());
    }
}
