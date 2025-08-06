package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.error.exception.DuplicateDataException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserMessageUtils;
import ru.practicum.shareit.user.utils.UserUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        userDto = new UserDto(1L, "Test User", "test@example.com");

        updateUserDto = new UpdateUserDto();
        updateUserDto.setId(1L);
        updateUserDto.setName("Updated User");
        updateUserDto.setEmail("updated@example.com");
    }

    @Test
    void getById_shouldReturnUserDto() {
        Long userId = 1L;
        try (MockedStatic<UserMapper> mockedMapper = mockStatic(UserMapper.class);
             MockedStatic<UserMessageUtils> mockedUtils = mockStatic(UserMessageUtils.class)) {

            mockedMapper.when(() -> UserMapper.toUserDto(any(User.class))).thenReturn(userDto);
            mockedUtils.when(() -> UserMessageUtils.getUserNotFountMessage(anyLong()))
                    .thenReturn("Пользователь с id = 1 не найден");

            when(repository.findById(anyLong())).thenReturn(Optional.of(user));

            UserDto result = userService.getById(userId);

            assertNotNull(result);
            assertEquals(userDto, result);
            verify(repository).findById(userId);
            mockedMapper.verify(() -> UserMapper.toUserDto(user));
        }
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenUserNotFound() {
        Long userId = 999L;
        try (MockedStatic<UserMessageUtils> mockedUtils = mockStatic(UserMessageUtils.class)) {
            mockedUtils.when(() -> UserMessageUtils.getUserNotFountMessage(anyLong()))
                    .thenReturn("Пользователь с id = 999 не найден");

            when(repository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> userService.getById(userId));
            verify(repository).findById(userId);
        }
    }

    @Test
    void delete_shouldDeleteUser() {
        Long userId = 1L;

        userService.delete(userId);

        verify(repository).deleteById(userId);
    }

    @Test
    void create_shouldReturnUserDto() {
        String userEmail = "user@example.com";
        UserDto newUserDto = new UserDto(null, "New User", userEmail);
        UserDto createdUserDto = new UserDto(1L, "New User", userEmail);

        try (MockedStatic<UserMapper> mockedMapper = mockStatic(UserMapper.class)) {
            mockedMapper.when(() -> UserMapper.toUser(any(UserDto.class))).thenReturn(user);
            mockedMapper.when(() -> UserMapper.toUserDto(any(User.class))).thenReturn(createdUserDto);

            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(repository.save(any(User.class))).thenReturn(user);

            UserDto result = userService.create(newUserDto);

            assertNotNull(result);
            assertEquals(createdUserDto, result);
            verify(repository).findByEmail(userEmail);
            verify(repository).save(any(User.class));
            mockedMapper.verify(() -> UserMapper.toUser(newUserDto));
            mockedMapper.verify(() -> UserMapper.toUserDto(user));
        }
    }

    @Test
    void create_shouldThrowDuplicateDataExceptionWhenEmailExists() {
        String userEmail = "duplicate@example.com";
        UserDto newUserDto = new UserDto(null, "New User", userEmail);

        when(repository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        assertThrows(DuplicateDataException.class, () -> userService.create(newUserDto));
        verify(repository).findByEmail(userEmail);
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void update_shouldReturnUserDto() {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto(userId, "Updated User Name", updateUserDto.getEmail());

        try (MockedStatic<UserUtils> mockedUtils = mockStatic(UserUtils.class);
             MockedStatic<UserMapper> mockedMapper = mockStatic(UserMapper.class)) {

            mockedUtils.when(() -> UserUtils.updateUser(any(User.class), any(UpdateUserDto.class)))
                    .thenReturn(user);
            mockedMapper.when(() -> UserMapper.toUserDto(any(User.class))).thenReturn(updatedUserDto);

            when(repository.findById(anyLong())).thenReturn(Optional.of(user));
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(repository.save(any(User.class))).thenReturn(user);

            UserDto result = userService.update(userId, updateUserDto);

            assertNotNull(result);
            assertEquals(updatedUserDto, result);
            verify(repository).findById(userId);
            verify(repository).findByEmail(updateUserDto.getEmail());
            mockedUtils.verify(() -> UserUtils.updateUser(user, updateUserDto));
            verify(repository).save(user);
            mockedMapper.verify(() -> UserMapper.toUserDto(user));
        }
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenUserNotFound() {
        Long userId = 999L;
        try (MockedStatic<UserMessageUtils> mockedUtils = mockStatic(UserMessageUtils.class)) {
            mockedUtils.when(() -> UserMessageUtils.getUserNotFountMessage(anyLong()))
                    .thenReturn("Пользователь с id = 999 не найден");

            when(repository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> userService.update(userId, updateUserDto));
            verify(repository).findById(userId);
            verify(repository, never()).save(any(User.class));
        }
    }

    @Test
    void update_shouldThrowDuplicateDataExceptionWhenEmailExists() {
        Long userId = 1L;
        String userEmail = "test@example.com";

        UpdateUserDto updateDtoWithExistingEmail = new UpdateUserDto();
        updateDtoWithExistingEmail.setId(userId);
        updateDtoWithExistingEmail.setName("Updated User");
        updateDtoWithExistingEmail.setEmail(userEmail);

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setName("Existing User");
        existingUser.setEmail(userEmail);

        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        when(repository.findByEmail(userEmail)).thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateDataException.class, () -> userService.update(userId, updateDtoWithExistingEmail));
        verify(repository).findById(userId);
        verify(repository).findByEmail(userEmail);
        verify(repository, never()).save(any(User.class));
    }
}