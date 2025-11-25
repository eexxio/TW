package com.cinema.movies.filter;

import com.cinema.movies.dto.MovieResponseDTO;
import com.cinema.movies.enums.ContentRating;
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
public class ContentRatingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ContentRatingFilter.class);
    private static final String HEADER_NAME = "X-Content-Rating";
    private static final String MOVIES_API_PREFIX = "/api/movies";

    private final ObjectMapper objectMapper;

    public ContentRatingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (!requestUri.startsWith(MOVIES_API_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, responseWrapper);
            addContentRatingHeader(responseWrapper);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private void addContentRatingHeader(ContentCachingResponseWrapper responseWrapper) {
        try {
            byte[] content = responseWrapper.getContentAsByteArray();

            if (content.length == 0) {
                return;
            }

            String contentType = responseWrapper.getContentType();
            if (contentType == null || !contentType.contains("application/json")) {
                return;
            }

            ContentRating rating = extractRatingFromJson(content);

            if (rating != null) {
                responseWrapper.setHeader(HEADER_NAME, rating.name());
                logger.debug("Added header {}: {} to response", HEADER_NAME, rating.name());
            }

        } catch (Exception e) {
            logger.error("Error processing response for content rating header", e);
        }
    }

    private ContentRating extractRatingFromJson(byte[] content) {
        try {
            String json = new String(content);

            if (json.trim().startsWith("{")) {
                MovieResponseDTO movie = objectMapper.readValue(content, MovieResponseDTO.class);
                return ContentRating.fromNumericRating(movie.getRating());
            }

            if (json.trim().startsWith("[")) {
                List<MovieResponseDTO> movies = objectMapper.readValue(
                        content,
                        new TypeReference<List<MovieResponseDTO>>() {}
                );
                return extractHighestRating(movies);
            }

        } catch (Exception e) {
            logger.warn("Failed to parse JSON response for rating extraction", e);
        }

        return null;
    }

    private ContentRating extractHighestRating(List<MovieResponseDTO> movies) {
        if (movies == null || movies.isEmpty()) {
            return null;
        }

        Double highestRating = movies.stream()
                .map(MovieResponseDTO::getRating)
                .filter(rating -> rating != null)
                .max(Double::compareTo)
                .orElse(null);

        return ContentRating.fromNumericRating(highestRating);
    }
}
