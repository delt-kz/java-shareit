package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getBooker(),
                booking.getItem(),
                LocalDateTime.ofInstant(booking.getStart(), ZoneOffset.UTC),
                LocalDateTime.ofInstant(booking.getEnd(), ZoneOffset.UTC),
                booking.getStatus());
    }

    public static Booking fromCreate(CreateBookingDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(dto.getStart().toInstant(ZoneOffset.UTC));
        booking.setEnd(dto.getEnd().toInstant(ZoneOffset.UTC));
        return booking;
    }

    public static List<BookingDto> toDto(List<Booking> bookings) {
        List<BookingDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(toDto(booking));
        }

        return result;
    }
}
