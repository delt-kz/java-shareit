package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        owner = createUser("owner", "owner@mail.com");
        booker = createUser("booker", "booker@mail.com");
    }

    @Test
    void createItem() {
        ItemRequest request = createRequest(owner, "Need a drill");
        ItemRequestDto dto = new ItemRequestDto("Drill", "Simple drill", true, request.getId());

        ItemResponseDto response = itemService.create(dto, owner.getId());

        assertThat(response.getId(), notNullValue());
        assertThat(response.getName(), equalTo(dto.getName()));
        assertThat(response.getDescription(), equalTo(dto.getDescription()));
        assertThat(response.getRequestId(), equalTo(request.getId()));

        Item saved = itemRepository.findById(response.getId()).orElseThrow();
        assertThat(saved.getOwner().getId(), equalTo(owner.getId()));
    }

    @Test
    void createItemWithUnknownUser() {
        ItemRequestDto dto = new ItemRequestDto("Drill", "Simple drill", true, null);

        assertThrows(NotFoundException.class, () -> itemService.create(dto, 9999L));
    }

    @Test
    void createItemWithUnknownRequest() {
        ItemRequestDto dto = new ItemRequestDto("Drill", "Simple drill", true, 4242L);

        assertThrows(NotFoundException.class, () -> itemService.create(dto, owner.getId()));
    }

    @Test
    void updateItemByOwner() {
        Item item = createItem(owner, "Old name", "Old description", true);
        ItemRequestDto patch = new ItemRequestDto("New name", null, null, null);

        ItemResponseDto response = itemService.update(patch, owner.getId(), item.getId());

        assertThat(response.getName(), equalTo("New name"));
        assertThat(response.getDescription(), equalTo("Old description"));
        assertThat(response.getAvailable(), equalTo(true));
    }

    @Test
    void updateItemByAnotherUserFails() {
        Item item = createItem(owner, "Old name", "Old description", true);
        ItemRequestDto patch = new ItemRequestDto("New name", null, null, null);

        assertThrows(ForbiddenException.class, () -> itemService.update(patch, booker.getId(), item.getId()));
    }

    @Test
    void getItemByIdWithComments() {
        Item item = createItem(owner, "Hammer", "Heavy hammer", true);
        createBooking(item, booker,
                Instant.now().minusSeconds(7200),
                Instant.now().minusSeconds(3600),
                BookingStatus.APPROVED);
        CommentDto commentDto = new CommentDto(null, "Nice item", null, null, null);
        itemService.createComment(commentDto, booker.getId(), item.getId());

        ItemWithBookingDto response = itemService.getById(item.getId(), owner.getId());

        assertThat(response.getId(), equalTo(item.getId()));
        assertThat(response.getComments(), hasSize(1));
        assertThat(response.getComments().get(0).getText(), equalTo("Nice item"));
        assertThat(response.getLastBooking(), nullValue());
        assertThat(response.getNextBooking(), nullValue());
    }

    @Test
    void getItemByIdUnknownUser() {
        Item item = createItem(owner, "Hammer", "Heavy hammer", true);

        assertThrows(NotFoundException.class, () -> itemService.getById(item.getId(), 9999L));
    }

    @Test
    void getItemByIdUnknownItem() {
        assertThrows(NotFoundException.class, () -> itemService.getById(9999L, owner.getId()));
    }

    @Test
    void getItemsByOwnerIncludesBookingsAndComments() {
        Item first = createItem(owner, "Hammer", "Heavy hammer", true);
        Item second = createItem(owner, "Saw", "Sharp saw", true);

        createBooking(first, booker,
                Instant.now().minusSeconds(10_800),
                Instant.now().minusSeconds(5_400),
                BookingStatus.APPROVED);
        createBooking(first, booker,
                Instant.now().plusSeconds(3_600),
                Instant.now().plusSeconds(7_200),
                BookingStatus.WAITING);

        Comment comment = new Comment();
        comment.setAuthor(booker);
        comment.setItem(first);
        comment.setText("Great hammer");
        commentRepository.save(comment);

        List<ItemWithBookingDto> responses = itemService.getItemsByOwner(owner.getId());

        assertThat(responses, hasSize(2));
        ItemWithBookingDto hammer = responses.stream()
                .filter(dto -> dto.getId().equals(first.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(hammer.getComments(), hasSize(1));
        assertThat(hammer.getLastBooking(), instanceOf(BookingResponseDto.class));
        assertThat(hammer.getNextBooking(), instanceOf(BookingResponseDto.class));
    }

    @Test
    void searchReturnsAvailableItems() {
        createItem(owner, "Hammer", "Heavy hammer", true);
        createItem(owner, "Screwdriver", "Magnetic", false);

        List<ItemResponseDto> results = itemService.search("Hammer");

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getName(), equalTo("Hammer"));
    }

    @Test
    void createCommentRequiresApprovedBooking() {
        Item item = createItem(owner, "Hammer", "Heavy hammer", true);
        createBooking(item, booker,
                Instant.now().minusSeconds(7200),
                Instant.now().minusSeconds(3600),
                BookingStatus.APPROVED);

        CommentDto commentDto = new CommentDto(null, "Nice item", item.getId(), null, null);
        CommentDto saved = itemService.createComment(commentDto, booker.getId(), item.getId());

        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getText(), equalTo("Nice item"));
        assertThat(saved.getAuthorName(), equalTo(booker.getName()));
    }

    @Test
    void createCommentWithoutBookingFails() {
        Item item = createItem(owner, "Hammer", "Heavy hammer", true);
        CommentDto commentDto = new CommentDto(null, "Nice item", item.getId(), null, null);

        assertThrows(ValidationException.class, () ->
                itemService.createComment(commentDto, booker.getId(), item.getId()));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(User owner, String name, String description, boolean available) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        return itemRepository.save(item);
    }

    private ItemRequest createRequest(User requestor, String description) {
        ItemRequest request = new ItemRequest();
        request.setRequestor(requestor);
        request.setDescription(description);
        return requestRepository.save(request);
    }

    private Booking createBooking(Item item, User booker, Instant start, Instant end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}
