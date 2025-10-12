package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemRepo;
    private final UserStorage userRepo;


    @Override
    public ItemDto create(ItemDto dto, Long ownerId) {
        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владелец не найден: " + ownerId));

        Item item = ItemMapper.mapToItem(dto);
        item.setOwner(owner);
        return ItemMapper.mapToItemDto(itemRepo.save(item));
    }

    @Override
    public ItemDto update(ItemDto patch, Long ownerId, Long itemId) {
        Item existing = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        if (existing.getOwner() == null || !existing.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Только владелец может редактировать вещь");
        }
        ItemMapper.mapFromUpdate(patch, existing);

        return ItemMapper.mapToItemDto(itemRepo.save(existing));
    }

    @Override
    public ItemDto getById(Long itemId, Long requesterId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найден: " + itemId));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        if (!userRepo.existsById(ownerId)) {
            throw new NotFoundException("Вещь не найден: " + ownerId);
        }
        return itemRepo.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return itemRepo.searchAvailableByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
