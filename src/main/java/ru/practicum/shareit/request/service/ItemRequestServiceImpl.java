package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage repository;
    private final UserStorage userRepository;

    @Override
    public ItemRequestDto create(ItemRequestDto dto, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + requestorId));

        ItemRequest request = ItemRequestMapper.mapToItemRequest(dto);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return ItemRequestMapper.mapToItemRequestDto(repository.save(request));
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }

        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена: " + requestId));

        return ItemRequestMapper.mapToItemRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        return repository.findAllByRequestorId(userId).stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        return repository.findAll().stream()
                .filter(r -> !r.getRequestor().getId().equals(userId))
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }
}
