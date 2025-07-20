package ru.practicum.shareit.user.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserUtils {

    public User updateUser(User oldUser, UpdateUserDto newUserDto) {
        if (newUserDto.getName() != null && !newUserDto.getName().isBlank()) {
            oldUser.setName(newUserDto.getName());
        }

        if (newUserDto.getEmail() != null && !newUserDto.getEmail().isBlank()) {
            oldUser.setEmail(newUserDto.getEmail());
        }

        return oldUser;
    }

}
