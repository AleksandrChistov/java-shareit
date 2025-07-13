package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;

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
    public ItemRequestDto create(ItemRequestDto itemRequestDto) {
        UserDto requestorDto = userService.getById(itemRequestDto.getRequestorId());
        User requestor = UserMapper.toUser(requestorDto);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);

        ItemRequest created = repository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(created);
    }

}
