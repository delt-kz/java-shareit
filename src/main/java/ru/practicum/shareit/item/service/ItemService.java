package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemDto create(CreateItemDto dto, Long ownerId);

    ItemDto update(CreateItemDto patch, Long ownerId, Long itemId);

    CommentDto createComment(CommentDto dto, Long userId, Long itemId);

    ItemWithBookingDto getById(Long itemId, Long requesterId);

    List<ItemWithBookingDto> getItemsByOwner(Long ownerId);

    List<ItemDto> search(String text);
}
