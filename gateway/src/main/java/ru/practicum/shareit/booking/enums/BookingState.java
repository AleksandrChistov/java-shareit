package ru.practicum.shareit.booking.enums;

import ru.practicum.shareit.core.error.exception.NotValidException;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> fromString(String value) {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        try {
            BookingState state = BookingState.valueOf(value.toUpperCase());
            return Optional.of(state);
        } catch (IllegalArgumentException e) {
            throw new NotValidException("Статус не может быть равен " + value);
        }
    }
}
