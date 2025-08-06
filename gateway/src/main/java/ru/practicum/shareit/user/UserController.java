package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.user.utils.UserMessageUtils.NOT_NULL_USER_ID_MESSAGE;
import static ru.practicum.shareit.user.utils.UserMessageUtils.POSITIVE_USER_ID_MESSAGE;

@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(
            @PathVariable @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId
    ) {
        log.info("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(
            @PathVariable @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId
    ) {
        log.info("Delete user {}", userId);
        return userClient.deleteUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Create user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(
            @PathVariable @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @Valid @RequestBody UpdateUserDto userDto
    ) {
        log.info("Update user {} with {}", userId, userDto);
        return userClient.patchUser(userId, userDto);
    }

}
