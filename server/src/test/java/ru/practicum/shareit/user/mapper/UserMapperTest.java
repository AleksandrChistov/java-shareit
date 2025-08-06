package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto_shouldConvertUserToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        UserDto result = UserMapper.toUserDto(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void toUserDto_shouldConvertUserWithNullIdToUserDto() {
        User user = new User();
        user.setId(null);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        UserDto result = UserMapper.toUserDto(user);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void toUserDto_shouldConvertUserWithNullFieldsToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName(null);
        user.setEmail(null);

        UserDto result = UserMapper.toUserDto(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void toUser_shouldConvertUserDtoToUser() {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        User result = UserMapper.toUser(userDto);

        assertNotNull(result);
        assertNull(result.getId()); // ID не устанавливается в методе toUser
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void toUser_shouldConvertUserDtoWithNullFieldsToUser() {
        UserDto userDto = new UserDto(1L, null, null);

        User result = UserMapper.toUser(userDto);

        assertNotNull(result);
        assertNull(result.getId()); // ID не устанавливается в методе toUser
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

}