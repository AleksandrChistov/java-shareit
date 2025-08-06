
package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                DateTimeUtils.toLocalDateTime(itemRequest.getCreated()),
                itemRequest.getRequestor() != null ? itemRequest.getRequestor().getId() : null
        );
    }

    public ItemRequestWithItemsDto toItemRequestWithItemsDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestWithItemsDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                DateTimeUtils.toLocalDateTime(itemRequest.getCreated()),
                itemRequest.getRequestor() != null ? itemRequest.getRequestor().getId() : null,
                items
        );
    }

    public ItemRequest toItemRequest(CreateItemRequestDto itemRequestDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }

}
