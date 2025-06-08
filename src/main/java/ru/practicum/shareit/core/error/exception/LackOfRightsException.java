package ru.practicum.shareit.core.error.exception;

public class LackOfRightsException extends RuntimeException {
    public LackOfRightsException(String message) {
        super(message);
    }
}
