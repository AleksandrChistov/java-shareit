package ru.practicum.shareit.core.error.exception;

/**
 * Исключение, возникающее при попытке выполнить операцию, которая не может быть выполнена
 * из-за недостатка прав доступа.
 */
public class LackOfRightsException extends RuntimeException {
    public LackOfRightsException(String message) {
        super(message);
    }
}
