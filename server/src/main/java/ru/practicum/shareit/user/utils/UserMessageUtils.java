package ru.practicum.shareit.user.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMessageUtils {

    public String getUserNotFountMessage(Long userId) {
        return String.format("Пользователь с id = %d не найден", userId);
    }

}
