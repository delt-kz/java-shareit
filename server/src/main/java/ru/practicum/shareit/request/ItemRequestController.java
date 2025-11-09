package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto create(
            @RequestHeader("X-Sharer-User-Id") Long requestorId,
            @RequestBody ItemRequestRequestDto dto) {
        return service.create(dto, requestorId);
    }

    @GetMapping
    public List<ItemRequestWithAnswerResponseDto> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return service.getUserRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return service.getAll(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswerResponseDto getById(
            @RequestHeader("X-Sharer-User-Id") Long requestorId,
            @PathVariable Long requestId) {
        return service.getById(requestId, requestorId);
    }
}
