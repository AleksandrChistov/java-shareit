package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCommentDto {
    @NotBlank(message = "Текст комментария - обязательное поле для заполнения")
    private String text;
}
