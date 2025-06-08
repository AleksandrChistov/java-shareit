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
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserServiceImpl userService;

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
        // todo: get ItemRequest for this item and add it to ItemMapper.toItem or null if is not found
        userService.getById(ownerId); // проверка что пользователь существует
        Item item = ItemMapper.toItem(itemDto, ownerId, null);

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
