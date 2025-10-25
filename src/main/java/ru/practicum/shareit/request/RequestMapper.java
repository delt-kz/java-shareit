package ru.practicum.shareit.request;

import lombok.AccessLevel;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ItemRequestDto toDto(ItemRequest ir) {
        return new ItemRequestDto(ir.getId(),
                ir.getDescription(),
                ir.getRequestor().getId(),
                LocalDateTime.ofInstant(ir.getCreated(), ZoneOffset.UTC));
    }


    public static ItemRequest fromCreate(CreateItemRequestDto dto, long requestorId) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        User requestor = new User();
        requestor.setId(requestorId);
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }

    public static List<ItemRequestDto> toDto(List<ItemRequest> irs) {
        List<ItemRequestDto> result = new ArrayList<>();

        for (ItemRequest ir : irs) {
            result.add(toDto(ir));
        }

        return result;
    }
}
