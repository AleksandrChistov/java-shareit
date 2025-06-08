package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> getAllByOwner(long ownerId);

    Optional<Item> getById(long itemId);

    List<Item> searchAll(String text);

    Item create(Item item);

    Item update(Item item);

}
