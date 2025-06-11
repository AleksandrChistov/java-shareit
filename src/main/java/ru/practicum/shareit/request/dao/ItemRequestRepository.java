package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository {

    List<ItemRequest> getAll();

    Optional<ItemRequest> getById(long itemRequestId);

    ItemRequest create(ItemRequest itemRequest);

}
