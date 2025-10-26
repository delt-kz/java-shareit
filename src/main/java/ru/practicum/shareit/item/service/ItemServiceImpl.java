package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ForbiddenException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final CommentRepository commentRepo;


    @Override
    public ItemDto create(CreateItemDto dto, Long ownerId) {
        Item item = ItemMapper.fromCreate(dto, ownerId);
        if (!userRepo.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден: " + ownerId);
        }
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
        if (!userRepo.existsById(requesterId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!itemRepo.existsById(itemId)) {
            throw new NotFoundException("Вещь не найдена");
        }
        ItemDto itemDto = itemRepo.findById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));
        List<CommentDto> commentDtos = CommentMapper.toDto(commentRepo.getItemComment(itemId));
        itemDto.setComments(commentDtos);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        List<Item> items = itemRepo.findAllByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        // Один запрос на все комментарии
        Map<Long, List<CommentDto>> commentsByItemId = commentRepo.findAllByItemIdIn(itemIds)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toDto(item);
                    dto.setComments(commentsByItemId.getOrDefault(item.getId(), List.of()));
                    return dto;
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return ItemMapper.toDto(itemRepo.searchByText(text));
    }

    public CommentDto createComment(CommentDto dto, Long userId, Long itemId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!itemRepo.existsById(itemId)) {
            throw new NotFoundException("Вещь не найдена");
        }

        User author = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Comment comment = CommentMapper.fromDto(dto, userId, itemId);
        if (!commentRepo.hasUserBookedItem(userId, itemId)) {
            throw new ValidationException("Пользователь не может оставить отзыв на вещь, которую не арендовал: " + itemId);
        }

        Comment saved = commentRepo.save(comment);
        saved.setAuthor(author);
        return CommentMapper.toDto(saved);

    }
}
