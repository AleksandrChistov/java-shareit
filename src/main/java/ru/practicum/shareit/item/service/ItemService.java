package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    List<ItemWithDatesDto> getAllByOwner(Long ownerId);

    ItemDto getById(Long itemId);

    List<ItemDto> searchAll(String text);

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, UpdateItemDto itemDto);

}
