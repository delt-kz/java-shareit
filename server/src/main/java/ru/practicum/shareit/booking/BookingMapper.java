package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponseDto toDto(Booking booking) {
        return new BookingResponseDto(booking.getId(),
                booking.getBooker(),
                booking.getItem(),
                LocalDateTime.ofInstant(booking.getStart(), ZoneOffset.UTC),
                LocalDateTime.ofInstant(booking.getEnd(), ZoneOffset.UTC),
                booking.getStatus());
    }

    public static Booking fromCreate(BookingRequestDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(dto.getStart().toInstant(ZoneOffset.UTC));
        booking.setEnd(dto.getEnd().toInstant(ZoneOffset.UTC));
        return booking;
    }

    public static List<BookingResponseDto> toDto(List<Booking> bookings) {
        List<BookingResponseDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(toDto(booking));
        }

        return result;
    }
}
