package ru.practicum.shareit.user.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserUtilsTest {

    @Test
    void updateUser_shouldUpdateAllFieldsWhenTheyAreNotNullAndNotBlank() {
        User oldUser = new User();
        oldUser.setName("Old Name");
        oldUser.setEmail("old@example.com");

        UpdateUserDto newUserDto = new UpdateUserDto();
        newUserDto.setId(1L);
        newUserDto.setName("New Name");
        newUserDto.setEmail("new@example.com");

        User result = UserUtils.updateUser(oldUser, newUserDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertSame(oldUser, result);
    }

    @Test
    void updateUser_shouldUpdateOnlyNameWhenEmailIsNull() {
        User oldUser = new User();
        oldUser.setName("Old Name");
        oldUser.setEmail("old@example.com");

        UpdateUserDto newUserDto = new UpdateUserDto();
        newUserDto.setId(1L);
        newUserDto.setName("New Name");
        newUserDto.setEmail(null);

        User result = UserUtils.updateUser(oldUser, newUserDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("old@example.com", result.getEmail());
    }

    @Test
    void updateUser_shouldUpdateOnlyEmailWhenNameIsNull() {
        User oldUser = new User();
        oldUser.setName("Old Name");
        oldUser.setEmail("old@example.com");

        UpdateUserDto newUserDto = new UpdateUserDto();
        newUserDto.setId(1L);
        newUserDto.setName(null);
        newUserDto.setEmail("new@example.com");

        User result = UserUtils.updateUser(oldUser, newUserDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName()); // Не изменился
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void updateUser_shouldNotUpdateNameWhenItIsBlank() {
        User oldUser = new User();
        oldUser.setName("Old Name");
        oldUser.setEmail("old@example.com");

        UpdateUserDto newUserDto = new UpdateUserDto();
        newUserDto.setId(1L);
        newUserDto.setName("   ");
        newUserDto.setEmail("new@example.com");

        User result = UserUtils.updateUser(oldUser, newUserDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void updateUser_shouldNotUpdateEmailWhenItIsBlank() {
        User oldUser = new User();
        oldUser.setName("Old Name");
        oldUser.setEmail("old@example.com");

        UpdateUserDto newUserDto = new UpdateUserDto();
        newUserDto.setId(1L);
        newUserDto.setName("New Name");
        newUserDto.setEmail("   ");

        User result = UserUtils.updateUser(oldUser, newUserDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("old@example.com", result.getEmail());
    }

    @Test
    void updateUser_shouldNotUpdateAnyFieldWhenDtoHasOnlyNulls() {
        User oldUser = new User();
        oldUser.setName("Old Name");
        oldUser.setEmail("old@example.com");

        UpdateUserDto newUserDto = new UpdateUserDto();
        newUserDto.setId(1L);
        newUserDto.setName(null);
        newUserDto.setEmail(null);

        User result = UserUtils.updateUser(oldUser, newUserDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("old@example.com", result.getEmail());
    }

}