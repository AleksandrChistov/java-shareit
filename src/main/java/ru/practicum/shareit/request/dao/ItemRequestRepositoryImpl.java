package ru.practicum.shareit.request.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemRequestRepositoryImpl implements ItemRequestRepository {
    private Long id = 0L;
    private final List<ItemRequest> itemRequests = new ArrayList<>();

    @Override
    public List<ItemRequest> getAll() {
        return itemRequests;
    }

    @Override
    public Optional<ItemRequest> getById(long itemRequestId) {
        return itemRequests.stream()
                .filter(ir -> ir.getId() == itemRequestId)
                .findFirst();
    }

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        itemRequest.setId(++id);
        itemRequests.add(itemRequest);
        return itemRequest;
    }

}
