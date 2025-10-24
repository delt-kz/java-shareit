package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    ItemDto create(CreateItemDto dto, Long ownerId);

    @Transactional
    ItemDto update(CreateItemDto patch, Long ownerId, Long itemId);

    ItemDto getById(Long itemId, Long requesterId);

    List<ItemDto> getOwnerItems(Long ownerId);

    List<ItemDto> search(String text);
}
