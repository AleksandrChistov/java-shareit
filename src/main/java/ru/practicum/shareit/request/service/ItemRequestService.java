package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getAll();

    ItemRequestDto getById(long itemRequestId);

    ItemRequestDto create(ItemRequestDto itemRequestDto);

}
