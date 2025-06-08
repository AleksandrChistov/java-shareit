package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
    @EqualsAndHashCode.Include
    private Long id;
    private final String name;
    private final String description;
    private final boolean available;
    private final Long ownerId;
    private final ItemRequest request;
}
