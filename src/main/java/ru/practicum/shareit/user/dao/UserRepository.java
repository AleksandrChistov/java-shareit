package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> getById(Long userId);

    Optional<User> getByEmail(String email);

    void delete(Long userId);

    User create(User user);

    User update(User user);

}
