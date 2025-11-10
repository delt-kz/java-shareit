package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingRequestDto requestDto;
    private BookingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(1);
        requestDto = new BookingRequestDto(1L, start, end);

        User booker = new User();
        booker.setId(5L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Strong drill");
        item.setAvailable(true);
        item.setOwner(booker);

        responseDto = new BookingResponseDto(10L, booker, item, start, end, BookingStatus.WAITING);
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.create(any(), eq(2L))).thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        ArgumentCaptor<BookingRequestDto> captor = ArgumentCaptor.forClass(BookingRequestDto.class);
        verify(bookingService).create(captor.capture(), eq(2L));
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(requestDto);
    }

    @Test
    void shouldApproveBooking() throws Exception {
        when(bookingService.approve(10L, 1L, true)).thenReturn(responseDto);

        mvc.perform(patch("/bookings/{bookingId}", 10L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(responseDto.getStatus().name()));

        verify(bookingService).approve(10L, 1L, true);
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(10L, 3L)).thenReturn(responseDto);

        mvc.perform(get("/bookings/{bookingId}", 10L)
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService).getBookingById(10L, 3L);
    }

    @Test
    void shouldGetBookingsForBookerWithDefaultState() throws Exception {
        when(bookingService.getBookingByBookerAndState(7L, BookingState.ALL))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto.getId()));

        verify(bookingService).getBookingByBookerAndState(7L, BookingState.ALL);
    }

    @Test
    void shouldGetBookingsForOwnerWithState() throws Exception {
        when(bookingService.getBookingByOwnerAndState(9L, BookingState.CURRENT))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 9L)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto.getId()));

        verify(bookingService).getBookingByOwnerAndState(9L, BookingState.CURRENT);
    }
}
