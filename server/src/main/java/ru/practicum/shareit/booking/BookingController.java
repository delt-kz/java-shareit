package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingResponseDto bookItem(@RequestBody BookingRequestDto dto,
                                     @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return service.create(dto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                      @RequestParam Boolean approved) {
        return service.approve(bookingId, bookerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {
        return service.getBookingByBookerAndState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                     @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {
        return service.getBookingByOwnerAndState(bookerId, state);
    }

}
