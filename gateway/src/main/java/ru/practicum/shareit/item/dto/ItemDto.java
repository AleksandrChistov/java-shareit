package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя - обязательное поле для заполнения")
    private final String name;
    @NotBlank(message = "Описание - обязательное поле для заполнения")
    private final String description;
    @NotNull(message = "Статус - обязательное поле для заполнения")
    private final Boolean available;
    private final Long requestId;
}
