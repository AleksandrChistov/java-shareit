package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.error.exception.LackOfRightsException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserServiceImpl userService;
    private final ItemRequestService itemRequestService;

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return repository.getAllByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long itemId) {
        return repository.getById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> searchAll(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return repository.searchAll(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        userService.getById(ownerId); // проверка что пользователь существует

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            ItemRequestDto itemRequestDto = itemRequestService.getById(itemDto.getRequestId());

            User requestor = null;
            if (itemRequestDto.getRequestorId() != null) {
                UserDto requestorDto = userService.getById(itemRequestDto.getRequestorId());
                requestor = UserMapper.toUser(requestorDto);
            }

            itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        }

        Item item = ItemMapper.toItem(itemDto, ownerId, itemRequest);

        Item created = repository.create(item);

        return ItemMapper.toItemDto(created);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, UpdateItemDto itemDto) {
        Item oldItem = repository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemDto.getId() + " не найдена"));

        if (!ownerId.equals(oldItem.getOwnerId())) {
            throw new LackOfRightsException("Редактировать вещь может только её владелец");
        }

        Item updated = repository.update(ItemMapper.updateItem(oldItem, itemDto));

        return ItemMapper.toItemDto(updated);
    }
}
