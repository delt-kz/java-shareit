package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;


    @Override
    public ItemDto create(CreateItemDto dto, Long ownerId) {
        Item item = ItemMapper.fromCreate(dto, ownerId);
        return ItemMapper.toDto(itemRepo.save(item));
    }

    @Override
    public ItemDto update(CreateItemDto patch, Long ownerId, Long itemId) {
        Item existing = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        if (existing.getOwner() == null || !existing.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Только владелец может редактировать вещь");
        }
        Item newItem = ItemMapper.fromUpdate(patch, existing);

        return ItemMapper.toDto(itemRepo.save(newItem));
    }

    @Override
    public ItemDto getById(Long itemId, Long requesterId) {
        return itemRepo.findById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        if (!userRepo.existsById(ownerId)) {
            throw new NotFoundException("Вещь не найдена: " + ownerId);
        }
        return ItemMapper.toDto(itemRepo.findAllByOwnerId(ownerId));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return ItemMapper.toDto(itemRepo.searchByText(text));
    }
}
