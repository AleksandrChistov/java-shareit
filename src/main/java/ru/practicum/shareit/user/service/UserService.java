package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getById(Long userId);

    void delete(Long userId);

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UpdateUserDto userDto);

}
