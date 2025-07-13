package ru.practicum.shareit.user.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserUtils {
    public final static String INVALID_USER_ID_MESSAGE = "ID пользователя не указан в заголовке или равен < 1";

    public User updateUser(User oldUser, UpdateUserDto newUserDto) {
        if (newUserDto.getName() != null && !newUserDto.getName().isBlank()) {
            oldUser.setName(newUserDto.getName());
        }

        if (newUserDto.getEmail() != null && !newUserDto.getEmail().isBlank()) {
            oldUser.setEmail(newUserDto.getEmail());
        }

        return oldUser;
    }

    public String getUserNotFountMessage(Long userId) {
        return String.format("Пользователь с id = %d не найден", userId);
    }
}
