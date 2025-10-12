package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemStorage {

    private final Map<Long, Item> storage = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(seq.incrementAndGet());
        }
        storage.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Item> findAllByOwnerId(Long ownerId) {
        return storage.values().stream()
                .filter(i -> i.getOwner() != null && i.getOwner().getId().equals(ownerId))
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }


    public List<Item> searchAvailableByText(String text) {
        if (text == null || text.isBlank()) return List.of();
        String q = text.toLowerCase();
        return storage.values().stream()
                .filter(Item::getAvailable)
                .filter(i ->
                        (i.getName() != null && i.getName().toLowerCase().contains(q)) ||
                                (i.getDescription() != null && i.getDescription().toLowerCase().contains(q))
                )
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }


    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
