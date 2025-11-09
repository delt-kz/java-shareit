package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;


public interface BookingService {
    BookingResponseDto create(BookingRequestDto dto, Long ownerId);

    BookingResponseDto approve(long bookingId, long ownerId, boolean approved);

    BookingResponseDto getBookingById(long bookingId, long userId);

    List<BookingResponseDto> getBookingByBookerAndState(long bookerId, BookingState state);

    List<BookingResponseDto> getBookingByOwnerAndState(long ownerId, BookingState state);

}
