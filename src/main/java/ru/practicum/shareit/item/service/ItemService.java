package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    ItemDto create(CreateItemDto dto, Long ownerId);

    @Transactional
    ItemDto update(CreateItemDto patch, Long ownerId, Long itemId);

    @Transactional
    CommentDto createComment(CommentDto dto, Long userId, Long itemId);

    ItemWithBookingDto getById(Long itemId, Long requesterId);

    List<ItemWithBookingDto> getItemsByOwner(Long ownerId);

    List<ItemDto> search(String text);
}
