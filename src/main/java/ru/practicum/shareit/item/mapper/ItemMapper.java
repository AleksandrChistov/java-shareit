
package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public FullItemDto toFullItemDto(Item item, Instant lastBookingDate, Instant nextBookingDate, List<Comment> comments) {
        List<CommentDto> commentDtos = comments.stream().map(CommentMapper::toCommentDto).toList();

        return new FullItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingDate == null ? null : DateTimeUtils.toLocalDateTime(lastBookingDate),
                nextBookingDate == null ? null : DateTimeUtils.toLocalDateTime(nextBookingDate),
                item.getRequest() != null ? item.getRequest().getId() : null,
                commentDtos
        );
    }

    public Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequest(itemRequest);
        return item;
    }

}
