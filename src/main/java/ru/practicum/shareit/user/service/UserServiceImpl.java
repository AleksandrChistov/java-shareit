package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.error.exception.DuplicateDataException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto getById(Long userId) {
        return repository.getById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException(UserUtils.getUserNotFountMessage(userId)));
    }

    @Override
    public void delete(Long userId) {
        repository.delete(userId);
    }

    @Override
    public UserDto create(UserDto userDto) {
        checkDuplicatedEmail(userDto.getEmail());

        User created = repository.create(UserMapper.toUser(userDto));

        return UserMapper.toUserDto(created);
    }

    @Override
    public UserDto update(Long userId, UpdateUserDto userDto) {
        User oldUser = repository.getById(userId)
                .orElseThrow(() -> new NotFoundException(UserUtils.getUserNotFountMessage(userId)));

        checkDuplicatedEmail(userDto.getEmail());

        User updated = repository.update(UserUtils.updateUser(oldUser, userDto));

        return UserMapper.toUserDto(updated);
    }

    private void checkDuplicatedEmail(String email) {
        Optional<User> optionalFound = repository.getByEmail(email);

        if (optionalFound.isPresent()) {
            throw new DuplicateDataException("Пользователь с email = " + email + " уже существует");
        }
    }
}
