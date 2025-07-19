package ru.practicum.shareit.booking.enums;

import ru.practicum.shareit.core.error.exception.NotValidException;

public enum BookingStatusView {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingStatusView fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return BookingStatusView.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotValidException("Статус не может быть равен " + value);
        }
    }
}
