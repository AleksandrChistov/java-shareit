package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class FullItemDto {
    private Long id;
    @NotBlank(message = "Имя - обязательное поле для заполнения")
    private final String name;
    @NotBlank(message = "Описание - обязательное поле для заполнения")
    private final String description;
    @NotNull(message = "Статус - обязательное поле для заполнения")
    private final Boolean available;
    private final LocalDateTime lastBooking;
    private final LocalDateTime nextBooking;
    private final Long requestId;
    private final List<CommentDto> comments;
}
