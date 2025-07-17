package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;

    @Override
    public List<ItemRequestDto> getAll() {
        return repository.findAll().stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(long itemRequestId) {
        return repository.findById(itemRequestId)
                .map(ItemRequestMapper::toItemRequestDto)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + itemRequestId + " для вещи не найден"));
    }

    @Override
    @Transactional
    public ItemRequestDto create(CreateItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(itemRequestDto.getRequestorId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + itemRequestDto.getRequestorId() + " не найде"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);

        ItemRequest created = repository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(created);
    }

}
