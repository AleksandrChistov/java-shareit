package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateItemRequestDto {
    @NotBlank(message = "Описание - обязательное поле для заполнения")
    private final String description;
    @NotNull(message = "Создатель запроса - обязательно поле для заполнения")
    private final Long requestorId;
}
