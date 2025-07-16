package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    List<FullItemDto> getAllByOwner(Long ownerId);

    FullItemDto getById(Long itemId);

    List<ItemDto> searchAll(String text);

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, UpdateItemDto itemDto);

    CommentDto addComment(Long userId, Long itemId, CreateCommentDto commentDto);
}
