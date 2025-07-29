package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateItemRequestDto {
    @NotBlank(message = "Описание - обязательное поле для заполнения")
    private final String description;
}
