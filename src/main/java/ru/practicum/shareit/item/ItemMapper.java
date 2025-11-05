package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static List<ItemDto> toDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toDto(item));
        }

        return result;
    }

    public static Item fromUpdate(CreateItemDto patch, Item oldItem) {
        Item newItem = new Item();
        if (patch.getName() != null) {
            newItem.setName(patch.getName());
        } else {
            newItem.setName(oldItem.getName());
        }
        if (patch.getDescription() != null) {
            newItem.setDescription(patch.getDescription());
        } else {
            newItem.setDescription(oldItem.getDescription());
        }
        if (patch.getAvailable() != null) {
            newItem.setAvailable(patch.getAvailable());
        } else {
            newItem.setAvailable(oldItem.getAvailable());
        }
        newItem.setId(oldItem.getId());
        newItem.setRequest(oldItem.getRequest() != null ? oldItem.getRequest() : null);
        newItem.setOwner(oldItem.getOwner());
        return newItem;
    }


    public static Item fromCreate(CreateItemDto dto, long ownerId) {
        Item item = new Item();
        item.setName(dto.getName());
        User user = new User();
        user.setId(ownerId);
        item.setOwner(user);
        if (dto.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(dto.getRequestId());
            item.setRequest(itemRequest);
        }
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static ItemWithBookingDto toDtoWithBooking(Item item, List<CommentDto> commentDtos, BookingDto lastBooking, BookingDto nextBooking) {
        return new ItemWithBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                commentDtos);
    }

    public static ItemShortDto toShortDto(Item item) {
        return new ItemShortDto(item.getId(), item.getId(), item.getName(), item.getOwner().getId());
    }

    public static List<ItemShortDto> toShortDto(List<Item> items) {
        List<ItemShortDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toShortDto(item));
        }

        return result;
    }
}
