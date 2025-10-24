package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepo;
    private final UserRepository userRepo;

    @Override
    public ItemRequestDto create(CreateItemRequestDto dto, Long requestorId) {
        ItemRequest request = RequestMapper.fromCreate(dto, requestorId);
        return RequestMapper.toDto(requestRepo.save(request));
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long requestorId) {
        if (!userRepo.existsById(requestorId)) {
            throw new NotFoundException("Пользователь не найден: " + requestorId);
        }

        ItemRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена: " + requestId));

        return RequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long requestorId) {
        if (!userRepo.existsById(requestorId)) {
            throw new NotFoundException("Пользователь не найден: " + requestorId);
        }
        return RequestMapper.toDto(requestRepo.findAllByRequestorId(requestorId));
    }

    @Override
    public List<ItemRequestDto> getAll(Long requestorId) {
        if (!userRepo.existsById(requestorId)) {
            throw new NotFoundException("Пользователь не найден: " + requestorId);
        }
        return RequestMapper.toDto(requestRepo.findAllByRequestorIdIsNot(requestorId));
    }
}
