package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя - обязательное поле для заполнения")
    private final String name;
    @NotNull(message = "Email - обязательное поле для заполнения")
    @Email(message = "Некорректно заполнено поле email")
    private final String email;
}
