package ru.practicum.shareit.core.error;

import lombok.Data;

@Data
public class ErrorResponse {

    private final String message;

    private final Integer code;

}
