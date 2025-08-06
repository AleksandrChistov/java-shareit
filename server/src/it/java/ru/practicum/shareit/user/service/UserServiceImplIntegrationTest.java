package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.error.exception.DuplicateDataException;
import ru.practicum.shareit.core.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Original Name");
        user.setEmail("original@example.com");
        em.persist(user);
        userId = user.getId();

        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        em.persist(anotherUser);

        em.flush();
    }

    @Test
    void update_shouldUpdateUserSuccessfully() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(userId);
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail("updated@example.com");

        UserDto updatedUser = userService.update(userId, updateUserDto);

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void update_shouldUpdateOnlyName() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(userId);
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail(null);

        UserDto updatedUser = userService.update(userId, updateUserDto);

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("original@example.com", updatedUser.getEmail());
    }

    @Test
    void update_shouldUpdateOnlyEmail() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(userId);
        updateUserDto.setEmail("newemail@example.com");
        updateUserDto.setName(null);

        UserDto updatedUser = userService.update(userId, updateUserDto);

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("Original Name", updatedUser.getName());
        assertEquals("newemail@example.com", updatedUser.getEmail());
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenUserNotFound() {
        Long nonExistentUserId = 999L;
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(nonExistentUserId);
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail("updated@example.com");

        assertThrows(NotFoundException.class, () -> userService.update(nonExistentUserId, updateUserDto));
    }

    @Test
    void update_shouldThrowDuplicateDataExceptionWhenEmailAlreadyExists() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(userId);
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail("another@example.com");

        assertThrows(DuplicateDataException.class, () -> userService.update(userId, updateUserDto));
    }

    @Test
    void update_shouldAllowSameEmailForSameUser() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(userId);
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail("original@example.com");

        UserDto updatedUser = userService.update(userId, updateUserDto);

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("original@example.com", updatedUser.getEmail());
    }

    @Test
    void update_shouldPersistChangesInDatabase() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(userId);
        updateUserDto.setName("Database Test");
        updateUserDto.setEmail("database@example.com");

        userService.update(userId, updateUserDto);

        em.flush(); // Фиксируем изменения в БД
        em.clear(); // Очищаем persistence context

        User persistedUser = em.find(User.class, userId);

        assertNotNull(persistedUser);
        assertEquals("Database Test", persistedUser.getName());
        assertEquals("database@example.com", persistedUser.getEmail());
        assertEquals(userId, persistedUser.getId());
    }

}