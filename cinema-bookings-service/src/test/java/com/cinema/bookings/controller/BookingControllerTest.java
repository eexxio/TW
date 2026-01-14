package com.cinema.bookings.controller;

import com.cinema.bookings.dto.BookingRequestDTO;
import com.cinema.bookings.dto.BookingResponseDTO;
import com.cinema.bookings.exception.BookingNotFoundException;
import com.cinema.bookings.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingRequestDTO requestDTO;
    private BookingResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new BookingRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setMovieId(1L);
        requestDTO.setPrice(100.0);
        requestDTO.setScreeningTime(LocalDateTime.now());

        responseDTO = new BookingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setMovieId(1L);
        responseDTO.setMovieTitle("Test Movie");
        responseDTO.setUserEmail("test@example.com");
        responseDTO.setStatus("PENDING");
        responseDTO.setPrice(100.0);
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(bookingService).createBooking(any(BookingRequestDTO.class));
    }

    @Test
    void testGetBookingById_Success() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.movieTitle").value("Test Movie"));

        verify(bookingService).getBookingById(1L);
    }

    @Test
    void testGetBookingById_NotFound() throws Exception {
        when(bookingService.getBookingById(1L)).thenThrow(new BookingNotFoundException(1L));

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateBooking_Success() throws Exception {
        when(bookingService.updateBooking(eq(1L), any(BookingRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService).updateBooking(eq(1L), any(BookingRequestDTO.class));
    }

    @Test
    void testUpdateBooking_NotFound() throws Exception {
        when(bookingService.updateBooking(eq(1L), any(BookingRequestDTO.class)))
                .thenThrow(new BookingNotFoundException(1L));

        mockMvc.perform(put("/api/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBooking_Success() throws Exception {
        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isNoContent());

        verify(bookingService).deleteBooking(1L);
    }

    @Test
    void testDeleteBooking_NotFound() throws Exception {
        when(bookingService.getBookingById(1L)).thenThrow(new BookingNotFoundException(1L));

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllBookings_Success() throws Exception {
        List<BookingResponseDTO> bookings = Arrays.asList(responseDTO, responseDTO);
        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookingService).getAllBookings();
    }

    @Test
    void testGetBookingsByUserId_Success() throws Exception {
        List<BookingResponseDTO> bookings = Arrays.asList(responseDTO);
        when(bookingService.getBookingsByUserId(1L)).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(bookingService).getBookingsByUserId(1L);
    }

    @Test
    void testFilterBookingsByStatus_Success() throws Exception {
        List<BookingResponseDTO> bookings = Arrays.asList(responseDTO);
        when(bookingService.getBookingsByStatus("PENDING")).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/filter")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(bookingService).getBookingsByStatus("PENDING");
    }

    @Test
    void testSortBookings_Success() throws Exception {
        List<BookingResponseDTO> bookings = Arrays.asList(responseDTO);
        when(bookingService.sortBookings("screeningTime", "asc")).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/sort")
                        .param("by", "screeningTime")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(bookingService).sortBookings("screeningTime", "asc");
    }

    @Test
    void testCreateBookingWithValidation_Success() throws Exception {
        when(bookingService.createBookingWithValidation(any(BookingRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/bookings/create-with-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.movieTitle").value("Test Movie"))
                .andExpect(jsonPath("$.userEmail").value("test@example.com"));

        verify(bookingService).createBookingWithValidation(any(BookingRequestDTO.class));
    }

    @Test
    void testConfirmBooking_Success() throws Exception {
        responseDTO.setStatus("CONFIRMED");
        when(bookingService.confirmBooking(1L)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/bookings/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(bookingService).confirmBooking(1L);
    }

    @Test
    void testConfirmBooking_NotFound() throws Exception {
        when(bookingService.confirmBooking(1L)).thenThrow(new BookingNotFoundException(1L));

        mockMvc.perform(put("/api/bookings/1/confirm"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEnrichedBooking_Success() throws Exception {
        Map<String, Object> enrichedData = new HashMap<>();
        enrichedData.put("booking", responseDTO);
        enrichedData.put("movie", Map.of("id", 1L, "title", "Test Movie"));
        enrichedData.put("user", Map.of("id", 1L, "email", "test@example.com"));

        when(bookingService.getEnrichedBooking(1L)).thenReturn(enrichedData);

        mockMvc.perform(get("/api/bookings/1/enriched"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booking").exists())
                .andExpect(jsonPath("$.movie").exists())
                .andExpect(jsonPath("$.user").exists());

        verify(bookingService).getEnrichedBooking(1L);
    }

    @Test
    void testGetEnrichedBooking_NotFound() throws Exception {
        when(bookingService.getEnrichedBooking(1L)).thenThrow(new BookingNotFoundException(1L));

        mockMvc.perform(get("/api/bookings/1/enriched"))
                .andExpect(status().isNotFound());
    }
}
