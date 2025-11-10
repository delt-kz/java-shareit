package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto create(ItemRequestDto dto, Long ownerId);

    ItemResponseDto update(ItemRequestDto patch, Long ownerId, Long itemId);

    CommentDto createComment(CommentDto dto, Long userId, Long itemId);

    ItemWithBookingDto getById(Long itemId, Long requesterId);

    List<ItemWithBookingDto> getItemsByOwner(Long ownerId);

    List<ItemResponseDto> search(String text);
}
