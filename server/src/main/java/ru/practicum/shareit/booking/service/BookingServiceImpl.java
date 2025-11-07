package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public BookingDto create(CreateBookingDto dto, Long bookerId) {
        User booker = userRepo.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new ForbiddenException("Нельзя бронировать свои вещи");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь в статусе не доступен: " + item.getId());
        }
        //нужно разобраться почему JOIN FETCH не работает
        Booking booking = BookingMapper.fromCreate(dto, item, booker);

        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new ValidationException("Дата старта брони не раньше даты окончания");
        }

        return BookingMapper.toDto(bookingRepo.save(booking));

    }


    @Override
    @Transactional
    public BookingDto approve(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найден " + bookingId));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ForbiddenException("Бронь может подтверждать только владелец вещи");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepo.save(booking));
    }

    @Override
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

    @Override
    public List<BookingDto> getBookingByBookerAndState(long bookerId, BookingState state) {
        if (!userRepo.existsById(bookerId)) {
            throw new NotFoundException("Пользователь не найден: " + bookerId);
        }
        return getBookings(bookerId, state, false);
    }

    @Override
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
                    ? bookingRepo.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING)
                    : bookingRepo.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);

            case REJECTED -> isOwner
                    ? bookingRepo.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED)
                    : bookingRepo.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);

            default -> throw new NotFoundException("Неизвестное состояние: " + state);
        };

        return BookingMapper.toDto(bookings);
    }

}
