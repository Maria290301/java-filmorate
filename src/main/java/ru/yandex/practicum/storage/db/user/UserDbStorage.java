package ru.yandex.practicum.storage.db.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.mapper.UserMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        log.debug("Добавление пользователя: {}", user);

        if (userExistsByEmail(user.getEmail())) {
            log.warn("Электронная почта {} уже используется", user.getEmail());
            throw new ConflictException("Электронная почта уже используется");
        }
        if (userExistsByLogin(user.getLogin())) {
            log.warn("Логин {} уже используется", user.getLogin());
            throw new ConflictException("Логин уже используется");
        }

        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getLogin(),
                Date.valueOf(user.getBirthday()));

        User thisUser = jdbcTemplate.queryForObject(
                "SELECT user_id, email, login, name, birthday FROM users WHERE email=?",
                new UserMapper(), user.getEmail());

        log.info("Добавлен пользователь: {}", thisUser);
        return thisUser;
    }

    @Override
    public List<User> addUsers(List<User> users) {
        log.debug("Добавление списка пользователей: {}", users);

        if (users == null || users.isEmpty()) {
            throw new ValidationException("Список пользователей не может быть пустым");
        }

        List<User> addedUsers = new ArrayList<>();
        for (User user : users) {
            if (userExistsByEmail(user.getEmail())) {
                log.warn("Электронная почта {} уже используется", user.getEmail());
                throw new ConflictException("Электронная почта уже используется");
            }
            if (userExistsByLogin(user.getLogin())) {
                log.warn("Логин {} уже используется", user.getLogin());
                throw new ConflictException("Логин уже используется");
            }

            jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getLogin(),
                    Date.valueOf(user.getBirthday()));

            addedUsers.add(user);
        }

        log.info("Добавлено пользователей: {}", addedUsers.size());
        return addedUsers;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Обновление пользователя: {}", user);

        if (!userExists(user.getId())) {
            log.warn("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        if (userExistsByLogin(user.getLogin(), user.getId())) {
            log.warn("Логин {} уже используется", user.getLogin());
            throw new ConflictException("Логин уже используется");
        }
        if (userExistsByEmail(user.getEmail(), user.getId())) {
            log.warn("Электронная почта {} уже используется", user.getEmail());
            throw new ConflictException("Электронная почта уже используется");
        }

        jdbcTemplate.update("UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());


        User updatedUser = getUserById(user.getId()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Обновлен пользователь: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public Optional<User> getUserById(int id) {
        log.debug("Получение пользователя по ID: {}", id);
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT user_id, email, login, name, birthday FROM users WHERE user_id=?",
                    new UserMapper(), id);
            log.info("Получен пользователь: {}", user);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Пользователь с ID {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Получение всех пользователей");
        List<User> users = jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users",
                new UserMapper());
        log.info("Получено пользователей: {}", users.size());
        return users;
    }

    @Override
    public void deleteUser(int id) {
        log.debug("Удаление пользователя с ID: {}", id);
        String query = "DELETE FROM users WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(query, id);

        if (rowsAffected == 0) {
            log.warn("Пользователь с id {} не найден для удаления", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        log.info("Пользователь с id {} успешно удален", id);
    }

    @Override
    public boolean userExists(Integer userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE user_id = ?",
                Integer.class, userId);
        return count != null && count > 0;
    }

    private boolean userExistsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }

    private boolean userExistsByEmail(String email, Integer userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?", Integer.class, email, userId);
        return count != null && count > 0;
    }

    private boolean userExistsByLogin(String login) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE login = ?", Integer.class, login);
        return count != null && count > 0;
    }

    private boolean userExistsByLogin(String login, Integer userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE login = ? AND user_id != ?", Integer.class, login, userId);
        return count != null && count > 0;
    }
}
