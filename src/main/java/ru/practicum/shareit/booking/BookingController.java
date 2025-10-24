//package ru.practicum.shareit.booking;
//
//import jakarta.validation.Valid;
//import lombok.Builder;
//import org.springframework.web.bind.annotation.*;
//import ru.practicum.shareit.booking.dto.BookingDto;
//import ru.practicum.shareit.booking.dto.CreateBookingDto;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/bookings")
//public class BookingController {
//    @PostMapping
//    public BookingDto create(@RequestBody @Valid CreateBookingDto dto,
//                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
//
//    }
//
//    @PatchMapping("/{bookingId}")
//    public BookingDto approve(@PathVariable Long bookingId,
//                           @RequestHeader("X-Sharer-User-Id") Long ownerId,
//                           @RequestParam Boolean approved) {
//
//    }
//
//    @GetMapping("/{bookingId")
//    public BookingDto get(@PathVariable Long bookingId,
//                       @RequestHeader("X-Sharer-User-Id") Long userId) {
//
//    }
//
//    @GetMapping
//    public List<BookingDto> getUserBookings(@PathVariable Long bookingId,
//                          @RequestHeader("X-Sharer-User-Id") Long userId,
//                          @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {
//
//    }
//
//    @GetMapping("/owner")
//    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
//                                             @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {
//
//    }
//
//}
