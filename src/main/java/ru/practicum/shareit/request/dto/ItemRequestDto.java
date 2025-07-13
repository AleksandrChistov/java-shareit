package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание - обязательное поле для заполнения")
    private final String description;
    @NotNull(message = "Создатель запроса - обязательно поле для заполнения")
    private final Long requestorId;
    @NotNull(message = "Дата и вермя - обязательно поле для заполнения")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime created;
}
