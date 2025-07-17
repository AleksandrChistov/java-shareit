package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.error.exception.DuplicateDataException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto getById(Long userId) {
        return repository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException(UserUtils.getUserNotFountMessage(userId)));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        checkDuplicatedEmail(userDto.getEmail());

        User created = repository.save(UserMapper.toUser(userDto));

        return UserMapper.toUserDto(created);
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UpdateUserDto userDto) {
        User oldUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserUtils.getUserNotFountMessage(userId)));

        checkDuplicatedEmail(userDto.getEmail());

        User updated = repository.save(UserUtils.updateUser(oldUser, userDto));

        return UserMapper.toUserDto(updated);
    }

    private void checkDuplicatedEmail(String email) {
        repository.findByEmail(email)
                .ifPresent(u -> {
                    throw new DuplicateDataException("Пользователь с email = " + email + " уже существует");
                });
    }
}
