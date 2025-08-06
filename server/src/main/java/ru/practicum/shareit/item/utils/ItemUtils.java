package ru.practicum.shareit.item.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemUtils {
    public Item updateItem(Item oldItem, UpdateItemDto newItemDto) {
        if (newItemDto.getName() != null && !newItemDto.getName().isBlank()) {
            oldItem.setName(newItemDto.getName());
        }

        if (newItemDto.getDescription() != null && !newItemDto.getDescription().isBlank()) {
            oldItem.setDescription(newItemDto.getDescription());
        }

        if (newItemDto.getAvailable() != null) {
            oldItem.setAvailable(newItemDto.getAvailable());
        }

        return oldItem;
    }
}
