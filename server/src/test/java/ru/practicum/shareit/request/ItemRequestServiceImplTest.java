package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final ItemRequestService requestService;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User requestor;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        requestor = createUser("requestor", "requestor@mail.com");
        anotherUser = createUser("another", "another@mail.com");
    }

    @Test
    void createRequest() {
        ItemRequestRequestDto dto = new ItemRequestRequestDto("Need a drill", java.time.LocalDateTime.now());

        ItemRequestResponseDto response = requestService.create(dto, requestor.getId());

        assertThat(response.getId(), notNullValue());
        assertThat(response.getDescription(), equalTo(dto.getDescription()));
        assertThat(response.getRequestorId(), equalTo(requestor.getId()));
    }

    @Test
    void createRequestUnknownUser() {
        ItemRequestRequestDto dto = new ItemRequestRequestDto("Need a drill", java.time.LocalDateTime.now());

        assertThrows(NotFoundException.class, () -> requestService.create(dto, 9999L));
    }

    @Test
    void getRequestByIdWithAnswers() {
        ItemRequest request = createRequest(requestor, "Need a drill");
        Item item = createItem(anotherUser, "Drill", true, request);

        ItemRequestWithAnswerResponseDto response = requestService.getById(request.getId(), requestor.getId());

        assertThat(response.getId(), equalTo(request.getId()));
        assertThat(response.getItems(), hasSize(1));
        assertThat(response.getItems().get(0).getName(), equalTo(item.getName()));
    }

    @Test
    void getRequestByIdUnknownUser() {
        ItemRequest request = createRequest(requestor, "Need a drill");

        assertThrows(NotFoundException.class, () -> requestService.getById(request.getId(), 9999L));
    }

    @Test
    void getRequestByIdUnknownRequest() {
        assertThrows(NotFoundException.class, () -> requestService.getById(9999L, requestor.getId()));
    }

    @Test
    void getUserRequests() {
        ItemRequest first = createRequest(requestor, "Need a drill");
        ItemRequest second = createRequest(requestor, "Need a hammer");
        createItem(anotherUser, "Drill", true, first);
        createItem(anotherUser, "Hammer", true, second);

        List<ItemRequestWithAnswerResponseDto> responses = requestService.getUserRequests(requestor.getId());

        assertThat(responses, hasSize(2));
        assertThat(responses.stream().map(ItemRequestWithAnswerResponseDto::getDescription).toList(),
                containsInAnyOrder("Need a drill", "Need a hammer"));
    }

    @Test
    void getAllRequestsExcludingUser() {
        ItemRequest first = createRequest(requestor, "Need a drill");
        ItemRequest second = createRequest(anotherUser, "Need a hammer");
        createItem(anotherUser, "Drill", true, first);
        createItem(requestor, "Hammer", true, second);

        List<ItemRequestResponseDto> responses = requestService.getAll(requestor.getId());

        assertThat(responses, hasSize(1));
        assertThat(responses.get(0).getDescription(), equalTo("Need a hammer"));
    }

    @Test
    void getAllRequestsUnknownUser() {
        assertThrows(NotFoundException.class, () -> requestService.getAll(9999L));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private ItemRequest createRequest(User requestor, String description) {
        ItemRequest request = new ItemRequest();
        request.setRequestor(requestor);
        request.setDescription(description);
        return requestRepository.save(request);
    }

    private Item createItem(User owner, String name, boolean available, ItemRequest request) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription("Description");
        item.setAvailable(available);
        item.setRequest(request);
        return itemRepository.save(item);
    }
}
