package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public ItemRequestDto create(CreateItemRequestDto dto, Long requestorId) {
        User requestor = userRepo.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = RequestMapper.fromCreate(dto, requestor);
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
