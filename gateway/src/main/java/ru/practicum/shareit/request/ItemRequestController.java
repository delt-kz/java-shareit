package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                         @RequestBody @Valid CreateItemRequestDto dto) {
        log.info("Create item request {}, userId={}", dto, requestorId);
        return requestClient.create(requestorId, dto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                          @PathVariable Long requestId) {
        log.info("Get item request {}, userId={}", requestId, requestorId);
        return requestClient.getById(requestorId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Get user item requests, userId={}", requestorId);
        return requestClient.getUserRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Get all item requests, userId={}", requestorId);
        return requestClient.getAll(requestorId);
    }
}
