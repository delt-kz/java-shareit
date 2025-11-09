package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemResponseDto create(@RequestBody ItemRequestDto dto,
                                  @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.create(dto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@PathVariable Long itemId,
                                  @RequestBody ItemRequestDto patch,
                                  @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.update(patch, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getById(@PathVariable Long itemId,
                           @RequestHeader(value = "X-Sharer-User-Id", required = false) Long requesterId) {
        return service.getById(itemId, requesterId);
    }

    @GetMapping
    public List<ItemWithBookingDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam("text") String text) {
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto dto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return service.createComment(dto, userId, itemId);
    }
}
