package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    ItemDto create(CreateItemDto dto, Long ownerId);

    @Transactional
    ItemDto update(CreateItemDto patch, Long ownerId, Long itemId);

    @Transactional
    CommentDto createComment(CommentDto dto, Long userId, Long itemId);

    ItemDto getById(Long itemId, Long requesterId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> search(String text);
}
