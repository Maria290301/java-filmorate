package ru.yandex.practicum.storage.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.db.user.UserStorage;

import java.util.*;

@Slf4j
@Component("InMemoryUserStorage")
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
    public List<User> addUsers(List<User> users) {
        List<User> addedUsers = new ArrayList<>();
        for (User user : users) {
            user.setId(++counter);
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            this.users.put(user.getId(), user);
            log.info("Добавлен пользователь: {}", user);
            addedUsers.add(user);
        }
        return addedUsers;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
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
    public Optional<User> getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id {} не найден", id);
            return Optional.empty();
        } else {
            log.info("Получен пользователь: {}", user);
            return Optional.of(user);
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
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public boolean userExists(Integer userId) {
        return users.containsKey(userId);
    }
}
