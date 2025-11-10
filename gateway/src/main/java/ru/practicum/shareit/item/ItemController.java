package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @RequestBody @Valid CreateItemDto dto) {
        log.info("Creating item {}, ownerId={}", dto, ownerId);
        return itemClient.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @RequestBody CreateItemDto patch) {
        log.info("Updating item {}, ownerId={}, patch={}", itemId, ownerId, patch);
        return itemClient.update(ownerId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable Long itemId,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long requesterId) {
        log.info("Getting item {}, requesterId={}", itemId, requesterId);
        return itemClient.getById(itemId, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Getting items by ownerId={}", ownerId);
        return itemClient.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text) {
        log.info("Searching items by text='{}'", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CreateCommentDto dto) {
        log.info("Creating comment for itemId={}, userId={}, comment={}", itemId, userId, dto);
        return itemClient.createComment(userId, itemId, dto);
    }
}
