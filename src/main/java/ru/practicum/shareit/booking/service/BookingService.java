package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;


public interface BookingService {
    BookingDto create(CreateBookingDto dto, Long ownerId);

    BookingDto approve(long bookingId, long ownerId, boolean approved);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getBookingByBookerAndState(long bookerId, BookingState state);

    List<BookingDto> getBookingByOwnerAndState(long ownerId, BookingState state);

}
