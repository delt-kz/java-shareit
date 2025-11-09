package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ItemRequestResponseDto toDto(ItemRequest ir) {
        return new ItemRequestResponseDto(ir.getId(),
                ir.getDescription(),
                ir.getRequestor().getId(),
                LocalDateTime.ofInstant(ir.getCreated(), ZoneOffset.UTC));
    }


    public static ItemRequest fromCreate(ItemRequestRequestDto dto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }

    public static List<ItemRequestResponseDto> toDto(List<ItemRequest> irs) {
        List<ItemRequestResponseDto> result = new ArrayList<>();

        for (ItemRequest ir : irs) {
            result.add(toDto(ir));
        }

        return result;
    }

    public static ItemRequestWithAnswerResponseDto toAnswerDto(ItemRequest ir, List<ItemShortDto> answers) {
        return new ItemRequestWithAnswerResponseDto(ir.getId(),
                ir.getDescription(),
                ir.getRequestor().getId(),
                LocalDateTime.ofInstant(ir.getCreated(), ZoneOffset.UTC),
                answers);
    }

}
