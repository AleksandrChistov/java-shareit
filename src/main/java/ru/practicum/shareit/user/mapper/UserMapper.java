package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.core.error.exception.NotValidException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.core.validation.Validate;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public User updateUser(UserDto oldUserDto, UpdateUserDto newUserDto) {
        boolean isNameChanged = newUserDto.getName() != null && !newUserDto.getName().isBlank();
        boolean isEmailChanged = newUserDto.getEmail() != null && !newUserDto.getEmail().isBlank();

        String name = isNameChanged ? newUserDto.getName() : oldUserDto.getName();
        String email = isEmailChanged ? newUserDto.getEmail() : oldUserDto.getEmail();

        if (isEmailChanged && !Validate.emailMatches(email)) {
            throw new NotValidException("Поле email = " + email + " заполнено неккоретно");
        }

        return new User(
                oldUserDto.getId(),
                name,
                email
        );
    }

}
