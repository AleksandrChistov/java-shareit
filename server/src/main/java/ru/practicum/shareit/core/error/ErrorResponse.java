package ru.practicum.shareit.core.error;

import lombok.Data;

@Data
public class ErrorResponse {

    private final String error;

    private final Integer code;

}
