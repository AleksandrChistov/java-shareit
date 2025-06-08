
package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public Item toItem(ItemDto itemDto, Long ownerId, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : false,
                ownerId,
                itemRequest
        );
    }

    public Item updateItem(Item oldItem, UpdateItemDto newItemDto) {
        boolean isNameChanged = newItemDto.getName() != null && !newItemDto.getName().isBlank();
        boolean isDescriptionChanged = newItemDto.getDescription() != null && !newItemDto.getDescription().isBlank();
        boolean isAvailableChanged = newItemDto.getAvailable() != null;

        String name = isNameChanged ? newItemDto.getName() : oldItem.getName();
        String description = isDescriptionChanged ? newItemDto.getDescription() : oldItem.getDescription();
        boolean isAvailable = isAvailableChanged ? newItemDto.getAvailable() : oldItem.isAvailable();

        return new Item(
                oldItem.getId(),
                name,
                description,
                isAvailable,
                oldItem.getOwnerId(),
                oldItem.getRequest()
        );
    }

}
