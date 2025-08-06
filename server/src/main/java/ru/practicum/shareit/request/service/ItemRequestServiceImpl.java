package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemRequestDto> getAll() {
        return repository.findAllByOrderByCreatedDesc().stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllOfOwnerWithItems(long userId) {
        List<ItemRequest> itemRequests = repository.findAllByRequestor_IdOrderByCreatedDesc(userId);

        if (!itemRequests.isEmpty()) {
            List<Long> requestsIds = itemRequests.stream()
                    .map(ItemRequest::getId)
                    .toList();

            Map<Long, List<ItemDto>> requestIdToItems = new HashMap<>();

            itemRepository.findAllByRequest_IdIn(requestsIds).stream()
                    .map(ItemMapper::toItemDto)
                    .forEach(itemDto -> requestIdToItems
                            .computeIfAbsent(itemDto.getRequestId(), k -> new ArrayList<>())
                            .add(itemDto)
                    );

            return itemRequests.stream()
                    .map(req -> {
                        List<ItemDto> items = requestIdToItems.getOrDefault(req.getId(), new ArrayList<>());
                        return ItemRequestMapper.toItemRequestWithItemsDto(req, items);
                    })
                    .toList();
        }

        return List.of();
    }

    @Override
    public ItemRequestWithItemsDto getById(long userId, long itemRequestId) {
        return repository.findById(itemRequestId)
                .map(req -> {
                    List<ItemDto> itemDtos = itemRepository.findAllByOwner_Id(userId)
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .toList();
                    return ItemRequestMapper.toItemRequestWithItemsDto(req, itemDtos);
                })
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + itemRequestId + " для вещи не найден"));
    }

    @Override
    @Transactional
    public ItemRequestDto create(CreateItemRequestDto itemRequestDto, long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);

        ItemRequest created = repository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(created);
    }

}
