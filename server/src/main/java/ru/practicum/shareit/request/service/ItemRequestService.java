package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerResponseDto;

import java.util.List;


public interface ItemRequestService {
    ItemRequestResponseDto create(ItemRequestRequestDto dto, Long requestorId);

    ItemRequestWithAnswerResponseDto getById(Long requestId, Long userId);

    List<ItemRequestWithAnswerResponseDto> getUserRequests(Long userId);

    List<ItemRequestResponseDto> getAll(Long userId);
}
