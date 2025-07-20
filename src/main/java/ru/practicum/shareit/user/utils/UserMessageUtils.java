package ru.practicum.shareit.user.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMessageUtils {

    public static final String NOT_NULL_USER_ID_MESSAGE = "ID пользователя не может быть null";

    public static final String POSITIVE_USER_ID_MESSAGE = "ID пользователя не может быть меньше 1";

    public String getUserNotFountMessage(Long userId) {
        return String.format("Пользователь с id = %d не найден", userId);
    }

}
