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
import ru.practicum.shareit.item.utils.ItemUtils;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return itemRepository.getAllByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemRepository.getById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> searchAll(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.searchAll(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.getById(ownerId)
                .orElseThrow(() -> new NotFoundException(UserUtils.getUserNotFountMessage(ownerId)));

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.getById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id = " + itemDto.getRequestId() + " для вещи не найден"));
        }

        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);

        Item created = itemRepository.create(item);

        return ItemMapper.toItemDto(created);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, UpdateItemDto itemDto) {
        Item oldItem = itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemDto.getId() + " не найдена"));

        if (!ownerId.equals(oldItem.getOwner().getId())) {
            throw new LackOfRightsException("Редактировать вещь может только её владелец");
        }

        Item updated = itemRepository.update(ItemUtils.updateItem(oldItem, itemDto));

        return ItemMapper.toItemDto(updated);
    }
}
