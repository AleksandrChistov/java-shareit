package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateCommentDto {
    @NotBlank(message = "Текст комментария - обязательное поле для заполнения")
    private String text;
}
