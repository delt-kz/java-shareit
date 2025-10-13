package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRequestStorage {

    private final Map<Long, ItemRequest> storage = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public ItemRequest save(ItemRequest request) {
        if (request.getId() == null) {
            request.setId(seq.incrementAndGet());
        }
        storage.put(request.getId(), request);
        return request;
    }

    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<ItemRequest> findAllByRequestorId(Long userId) {
        return storage.values().stream()
                .filter(r -> r.getRequestor() != null && r.getRequestor().getId().equals(userId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    public List<ItemRequest> findAll() {
        return new ArrayList<>(storage.values());
    }
}
