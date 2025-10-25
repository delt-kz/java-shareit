package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestBody @Valid CreateItemDto dto,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.create(dto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody CreateItemDto patch,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.update(patch, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId,
                           @RequestHeader(value = "X-Sharer-User-Id", required = false) Long requesterId) {
        return service.getById(itemId, requesterId);
    }

    @GetMapping
    public List<ItemWithBookingDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        return service.search(text);
    }
}
