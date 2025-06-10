package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Long id = 0L;
    private final Map<Long, List<Item>> items = new HashMap<>();

    @Override
    public List<Item> getAllByOwner(long ownerId) {
        return items.getOrDefault(ownerId, new ArrayList<>());
    }

    @Override
    public Optional<Item> getById(long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(i -> i.getId() == itemId)
                .findFirst();
    }

    @Override
    public List<Item> searchAll(String text) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(Item::getAvailable)
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        List<Item> ownerItems = getAllByOwner(item.getOwnerId());

        item.setId(++id);
        ownerItems.add(item);
        items.putIfAbsent(item.getOwnerId(), ownerItems);

        return item;
    }

    @Override
    public Item update(Item item) {
        // todo: will be removed after JPA implementation
        List<Item> ownerItems = getAllByOwner(item.getOwnerId());

        ownerItems.remove(item);
        ownerItems.add(item);

        return item;
    }
}
