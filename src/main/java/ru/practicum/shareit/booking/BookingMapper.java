package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
//        String start = DateTimeFormatter.ISO_INSTANT
//                .format(booking.getStart());
        return new BookingDto(booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus());
    }

    public static Booking toBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        User booker = new User();
        booker.setId(dto.getBooker_id());
        booking.setBooker(booker);
        booking.setStart(booking.getStart());
        booking.setEnd(booking.getEnd());
        booking.setStatus(dto.getStatus());
        return booking;
    }

    public static Booking fromCreate(CreateBookingDto dto) {
        Booking booking = new Booking();
        User booker = new User();
        booker.setId(dto.getBooker_id());
        booking.setBooker(booker);
        booking.setStart(booking.getStart());
        booking.setEnd(booking.getEnd());
        booking.setStatus(dto.getStatus());
        return booking;
    }
}
