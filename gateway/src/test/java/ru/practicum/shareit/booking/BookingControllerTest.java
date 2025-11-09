package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void shouldRequestUserBookingsWithDefaultState() throws Exception {
        when(bookingClient.getBookings(1L, BookingState.ALL)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(1L, BookingState.ALL);
    }

    @Test
    void shouldReturnServerErrorForUnknownState() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                mvc.perform(get("/bookings")
                                .header("X-Sharer-User-Id", 1L)
                                .param("state", "UNSUPPORTED")))
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown state");
    }

    @Test
    void shouldApproveBooking() throws Exception {
        when(bookingClient.approve(5L, 1L, true)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/bookings/{bookingId}", 5L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approve(5L, 1L, true);
    }

    @Test
    void shouldValidateBookingDates() throws Exception {
        BookItemDto dto = new BookItemDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldForwardCreateBookingToClient() throws Exception {
        BookItemDto dto = new BookItemDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(bookingClient.bookItem(eq(1L), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<BookItemDto> captor = ArgumentCaptor.forClass(BookItemDto.class);
        verify(bookingClient).bookItem(eq(1L), captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void shouldForwardGetBookingToClient() throws Exception {
        when(bookingClient.getBooking(1L, 5L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/{bookingId}", 5L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(1L, 5L);
    }

    @Test
    void shouldForwardOwnerBookings() throws Exception {
        when(bookingClient.getOwnerBookings(1L, BookingState.CURRENT)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());

        verify(bookingClient).getOwnerBookings(1L, BookingState.CURRENT);
    }
}
