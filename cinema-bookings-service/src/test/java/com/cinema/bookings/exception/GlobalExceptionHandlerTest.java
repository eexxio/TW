package com.cinema.bookings.exception;

import com.cinema.bookings.controller.BookingController;
import com.cinema.bookings.dto.BookingRequestDTO;
import com.cinema.bookings.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new BookingRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setMovieId(1L);
        requestDTO.setPrice(100.0);
        requestDTO.setScreeningTime(java.time.LocalDateTime.now());
    }

    @Test
    void testHandleBookingNotFoundException() throws Exception {
        when(bookingService.getBookingById(999L)).thenThrow(new BookingNotFoundException(999L));

        mockMvc.perform(get("/api/bookings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testHandleBookingNotFoundExceptionOnUpdate() throws Exception {
        when(bookingService.updateBooking(eq(999L), any(BookingRequestDTO.class)))
                .thenThrow(new BookingNotFoundException(999L));

        mockMvc.perform(put("/api/bookings/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testHandleBookingNotFoundExceptionOnDelete() throws Exception {
        when(bookingService.getBookingById(999L)).thenThrow(new BookingNotFoundException(999L));

        mockMvc.perform(get("/api/bookings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testHandleValidationException_MissingUserId() throws Exception {
        requestDTO.setUserId(null);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testHandleValidationException_MissingMovieId() throws Exception {
        requestDTO.setMovieId(null);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());
    }


    @Test
    void testHandleGlobalException() throws Exception {
        when(bookingService.getBookingById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Unexpected error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testHandleGlobalException_NullPointerException() throws Exception {
        when(bookingService.getAllBookings()).thenThrow(new NullPointerException("Null value encountered"));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testHandleGlobalException_IllegalArgumentException() throws Exception {
        when(bookingService.getBookingById(1L)).thenThrow(new IllegalArgumentException("Invalid argument"));

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Invalid argument"));
    }
}
