package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


public interface ItemRequestService {
    ItemRequestDto create(CreateItemRequestDto dto, Long requestorId);

    ItemRequestDto getById(Long requestId, Long userId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAll(Long userId);
}
