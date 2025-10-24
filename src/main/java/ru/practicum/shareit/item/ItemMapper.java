package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
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

    public static Item toItem(ItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static Item fromUpdate(CreateItemDto patch, Item oldItem) {
        Item newItem = new Item();
        if (patch.getName() != null) newItem.setName(patch.getName());
        if (patch.getDescription() != null) newItem.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) newItem.setAvailable(patch.getAvailable());
        newItem.setId(oldItem.getId());
        newItem.setRequest(oldItem.getRequest());
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

    public static List<ItemDto> toDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toDto(item));
        }

        return result;
    }
}
