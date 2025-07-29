package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getAll();

    List<ItemRequestWithItemsDto> getAllOfOwnerWithItems(long userId);

    ItemRequestWithItemsDto getById(long itemRequestId);

    ItemRequestDto create(CreateItemRequestDto itemRequestDto, long requestorId);

}
