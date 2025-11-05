package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepo;
    private final ItemRepository itemRepo;
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
    public ItemRequestWithAnswerDto getById(Long requestId, Long requestorId) {
        if (!userRepo.existsById(requestorId)) {
            throw new NotFoundException("Пользователь не найден: " + requestorId);
        }

        ItemRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена: " + requestId));

        List<ItemShortDto> answers = ItemMapper.toShortDto(itemRepo.findAllByRequestId(requestId));
        return RequestMapper.toAnswerDto(request, answers);
    }

    @Override
    public List<ItemRequestWithAnswerDto> getUserRequests(Long requestorId) {
        if (!userRepo.existsById(requestorId)) {
            throw new NotFoundException("Пользователь не найден: " + requestorId);
        }

        List<ItemRequest> itemRequests = requestRepo.findAllByRequestorId(requestorId);
        List<Long> itemRequestIds = itemRequests.stream().map(ItemRequest::getId).toList();

        Map<Long, List<ItemShortDto>> answers = itemRepo.findAllByRequestIdIn(itemRequestIds)
                .stream()
                .map(ItemMapper::toShortDto)
                .collect(Collectors.groupingBy(ItemShortDto::getItemId));

        return itemRequests.stream()
                .map(request ->
                    RequestMapper.toAnswerDto(request, answers.get(request.getId())))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll(Long requestorId) {
        if (!userRepo.existsById(requestorId)) {
            throw new NotFoundException("Пользователь не найден: " + requestorId);
        }
        return RequestMapper.toDto(requestRepo.findAllByRequestorIdIsNot(requestorId));
    }
}
