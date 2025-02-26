package ru.yandex.practicum.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int counter = 0;

    @Override
    public User addUser(User user) {
        user.setId(++counter);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        for (User existingUser : users.values()) {
            if (existingUser.getId() != user.getId()) {
                if (Objects.equals(existingUser.getLogin(), user.getLogin())) {
                    log.warn("Логин {} уже используется", user.getLogin());
                    throw new ConflictException("Логин уже используется");
                }
                if (Objects.equals(existingUser.getEmail(), user.getEmail())) {
                    log.warn("Электронная почта {} уже используется", user.getEmail());
                    throw new ConflictException("Электронная почта уже используется");
                }
            }
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id {} не найден", id);
            return null;
        } else {
            log.info("Получен пользователь: {}", user);
            return user;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(int id) {
        if (users.remove(id) != null) {
            log.info("Пользователь с id {} был удален", id);
        } else {
            log.warn("Попытка удалить несуществующего пользователя с id {}", id);
        }
    }

    @Override
    public List<User> getFriendsByUserId(int userId) {
        User user = getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id {} не найден", userId);
            return Collections.emptyList();
        }
        List<User> friendsList = user.getFriends().stream()
                .map(this::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Получены друзья пользователя с id {}: {}", userId, friendsList);
        return friendsList;
    }
}