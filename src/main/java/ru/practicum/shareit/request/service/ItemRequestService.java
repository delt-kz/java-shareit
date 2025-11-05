package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;

import java.util.List;


public interface ItemRequestService {
    ItemRequestDto create(CreateItemRequestDto dto, Long requestorId);

    ItemRequestWithAnswerDto getById(Long requestId, Long userId);

    List<ItemRequestWithAnswerDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAll(Long userId);
}
