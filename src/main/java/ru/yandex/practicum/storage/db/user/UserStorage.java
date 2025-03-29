package ru.yandex.practicum.storage.db.user;

import ru.yandex.practicum.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    List<User> addUsers(List<User> users);

    User updateUser(User user);

    Optional<User> getUserById(int id);

    List<User> getAllUsers();

    void deleteUser(int id);

    boolean userExists(Integer userId);
}
