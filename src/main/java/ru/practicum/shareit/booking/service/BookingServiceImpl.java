package ru.practicum.shareit.booking.service;


import jakarta.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.exception.ConflictException;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;



    public BookingDto create(CreateBookingDto dto, Long ownerId) {
        User booker = userRepo.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь в статусе не доступен: " + item.getId());
        }
        Booking booking = BookingMapper.fromCreate(dto, item, booker);

        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new ValidationException("Дата старта брони не раньше даты окончания");
        }

        return BookingMapper.toDto(bookingRepo.save(booking));

    }

    public BookingDto approve(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найден " + bookingId));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ForbiddenException("Бронь может подтверждать только владелец вещи");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepo.save(booking));
    }

    public BookingDto getBookingById(long bookingId, long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найден " + bookingId));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new ForbiddenException("Бронь может просматриваться только владельцем вещи или владельцем брони");
        }
        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> getBookingByBookerAndState(long bookerId, BookingState state) {
        if (!userRepo.existsById(bookerId)) {
            throw new NotFoundException("Пользователь не найден: " + bookerId);
        }
        return getBookings(bookerId, state, false);
    }

    public List<BookingDto> getBookingByOwnerAndState(long ownerId, BookingState state) {
        if (!userRepo.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден: " + ownerId);
        }
        return getBookings(ownerId, state, true);
    }

    private List<BookingDto> getBookings(long userId, BookingState state, boolean isOwner) {
        List<Booking> bookings = switch (state) {
            case ALL -> isOwner
                    ? bookingRepo.findAllByItemOwnerIdOrderByStartDesc(userId)
                    : bookingRepo.findAllByBookerIdOrderByStartDesc(userId);

            case PAST -> isOwner
                    ? bookingRepo.findAllByItemOwnerIdAndStatePast(userId)
                    : bookingRepo.findAllByBookerIdAndStatePast(userId);

            case CURRENT -> isOwner
                    ? bookingRepo.findAllByItemOwnerIdAndStateCurrent(userId)
                    : bookingRepo.findAllByBookerIdAndStateCurrent(userId);

            case FUTURE -> isOwner
                    ? bookingRepo.findAllByItemOwnerIdAndStateFuture(userId)
                    : bookingRepo.findAllByBookerIdAndStateFuture(userId);

            case WAITING -> isOwner
                    ? bookingRepo.findAllByItemOwnerIdAndStateWaiting(userId)
                    : bookingRepo.findAllByBookerIdAndStateWaiting(userId);

            case REJECTED -> isOwner
                    ? bookingRepo.findAllByItemOwnerIdAndStateRejected(userId)
                    : bookingRepo.findAllByBookerIdAndStateRejected(userId);

            default -> throw new NotFoundException("Неизвестное состояние: " + state);
        };

        return BookingMapper.toDto(bookings);
    }

}
