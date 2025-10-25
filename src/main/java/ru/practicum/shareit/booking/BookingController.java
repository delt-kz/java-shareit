package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestBody @Valid CreateBookingDto dto,
                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.create(dto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestParam Boolean approved) {
        return service.approve(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {
        return service.getBookingByBookerAndState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {
        return service.getBookingByOwnerAndState(ownerId, state);
    }

}
