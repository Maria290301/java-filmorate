package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.User;

import java.util.List;

public interface UserStorage {
    User addUser (User user);
    User updateUser (User user);
    User getUserById(int id);
    List<User> getAllUsers();
    void deleteUser (int id);
    List<User> getFriendsByUserId(int userId);
}
