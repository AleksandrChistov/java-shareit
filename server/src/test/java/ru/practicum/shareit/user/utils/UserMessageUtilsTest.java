package ru.practicum.shareit.user.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMessageUtilsTest {

    @Test
    void getUserNotFountMessage_shouldReturnFormattedMessage() {
        Long userId = 999L;

        String result = UserMessageUtils.getUserNotFountMessage(userId);

        assertEquals("Пользователь с id = 999 не найден", result);
    }

    @Test
    void getUserNotFountMessage_shouldReturnFormattedMessageWithNullId() {
        Long userId = null;

        String result = UserMessageUtils.getUserNotFountMessage(userId);

        assertEquals("Пользователь с id = null не найден", result);
    }

}