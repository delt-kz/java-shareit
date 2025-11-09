package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = createUser("owner", "owner@mail.com");
        booker = createUser("booker", "booker@mail.com");
        item = createItem(owner, "Drill", true);
    }

    @Test
    void createBooking() {
        BookingRequestDto request = new BookingRequestDto(item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));

        BookingResponseDto response = bookingService.create(request, booker.getId());

        assertThat(response.getId(), notNullValue());
        assertThat(response.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(response.getItem().getId(), equalTo(item.getId()));
    }

    @Test
    void createBookingByOwnerForbidden() {
        BookingRequestDto request = new BookingRequestDto(item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));

        assertThrows(ForbiddenException.class, () -> bookingService.create(request, owner.getId()));
    }

    @Test
    void createBookingForUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingRequestDto request = new BookingRequestDto(item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));

        assertThrows(ValidationException.class, () -> bookingService.create(request, booker.getId()));
    }

    @Test
    void createBookingWithInvalidDates() {
        BookingRequestDto request = new BookingRequestDto(item.getId(),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(1));

        assertThrows(ValidationException.class, () -> bookingService.create(request, booker.getId()));
    }

    @Test
    void approveBookingByOwner() {
        Booking booking = createBooking(booker, item,
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                BookingStatus.WAITING);

        BookingResponseDto response = bookingService.approve(booking.getId(), owner.getId(), true);

        assertThat(response.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approveBookingByNonOwnerFails() {
        Booking booking = createBooking(booker, item,
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                BookingStatus.WAITING);

        assertThrows(ForbiddenException.class, () -> bookingService.approve(booking.getId(), booker.getId(), true));
    }

    @Test
    void getBookingByIdForParticipants() {
        Booking booking = createBooking(booker, item,
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                BookingStatus.WAITING);

        BookingResponseDto ownerView = bookingService.getBookingById(booking.getId(), owner.getId());
        BookingResponseDto bookerView = bookingService.getBookingById(booking.getId(), booker.getId());

        assertThat(ownerView.getId(), equalTo(booking.getId()));
        assertThat(bookerView.getId(), equalTo(booking.getId()));
    }

    @Test
    void getBookingByIdNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(9999L, owner.getId()));
    }

    @Test
    void getBookingByIdForbiddenForStrangers() {
        Booking booking = createBooking(booker, item,
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                BookingStatus.WAITING);
        User stranger = createUser("stranger", "stranger@mail.com");

        assertThrows(ForbiddenException.class, () -> bookingService.getBookingById(booking.getId(), stranger.getId()));
    }

    @Test
    void getBookingsByBookerAndState() {
        Booking past = createBooking(booker, item,
                Instant.now().minusSeconds(7200),
                Instant.now().minusSeconds(3600),
                BookingStatus.APPROVED);
        Booking current = createBooking(booker, item,
                Instant.now().minusSeconds(1800),
                Instant.now().plusSeconds(1800),
                BookingStatus.APPROVED);
        Booking futureWaiting = createBooking(booker, item,
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                BookingStatus.WAITING);
        Booking futureRejected = createBooking(booker, item,
                Instant.now().plusSeconds(10800),
                Instant.now().plusSeconds(14400),
                BookingStatus.REJECTED);

        assertThat(bookingService.getBookingByBookerAndState(booker.getId(), BookingState.ALL), hasSize(4));
        assertThat(bookingService.getBookingByBookerAndState(booker.getId(), BookingState.PAST), contains(hasProperty("id", is(past.getId()))));
        assertThat(bookingService.getBookingByBookerAndState(booker.getId(), BookingState.CURRENT), contains(hasProperty("id", is(current.getId()))));
        assertThat(bookingService.getBookingByBookerAndState(booker.getId(), BookingState.FUTURE), hasSize(2));
        assertThat(bookingService.getBookingByBookerAndState(booker.getId(), BookingState.WAITING), contains(hasProperty("id", is(futureWaiting.getId()))));
        assertThat(bookingService.getBookingByBookerAndState(booker.getId(), BookingState.REJECTED), contains(hasProperty("id", is(futureRejected.getId()))));
    }

    @Test
    void getBookingsByOwnerAndState() {
        Booking past = createBooking(booker, item,
                Instant.now().minusSeconds(7200),
                Instant.now().minusSeconds(3600),
                BookingStatus.APPROVED);
        Booking current = createBooking(booker, item,
                Instant.now().minusSeconds(1800),
                Instant.now().plusSeconds(1800),
                BookingStatus.APPROVED);
        Booking futureWaiting = createBooking(booker, item,
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200),
                BookingStatus.WAITING);
        Booking futureRejected = createBooking(booker, item,
                Instant.now().plusSeconds(10800),
                Instant.now().plusSeconds(14400),
                BookingStatus.REJECTED);

        assertThat(bookingService.getBookingByOwnerAndState(owner.getId(), BookingState.ALL), hasSize(4));
        assertThat(bookingService.getBookingByOwnerAndState(owner.getId(), BookingState.PAST), contains(hasProperty("id", is(past.getId()))));
        assertThat(bookingService.getBookingByOwnerAndState(owner.getId(), BookingState.CURRENT), contains(hasProperty("id", is(current.getId()))));
        assertThat(bookingService.getBookingByOwnerAndState(owner.getId(), BookingState.FUTURE), hasSize(2));
        assertThat(bookingService.getBookingByOwnerAndState(owner.getId(), BookingState.WAITING), contains(hasProperty("id", is(futureWaiting.getId()))));
        assertThat(bookingService.getBookingByOwnerAndState(owner.getId(), BookingState.REJECTED), contains(hasProperty("id", is(futureRejected.getId()))));
    }

    @Test
    void getBookingsByBookerUnknownUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingByBookerAndState(9999L, BookingState.ALL));
    }

    @Test
    void getBookingsByOwnerUnknownUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingByOwnerAndState(9999L, BookingState.ALL));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(User owner, String name, boolean available) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription("Description");
        item.setAvailable(available);
        return itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, Instant start, Instant end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}
