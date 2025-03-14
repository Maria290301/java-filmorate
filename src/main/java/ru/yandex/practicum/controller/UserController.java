package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int counter = 0;

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        user.setId(++counter);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updatedUser) {
        if (!users.containsKey(updatedUser.getId())) {
            throw new UserNotFoundException("Пользователь с id " + updatedUser.getId() + " не найден");
        }
        for (User user : users.values()) {
            if (user.getId() != updatedUser.getId()) {
                if (user.getLogin().equals(updatedUser.getLogin())) {
                    log.warn("Логин {} уже используется", updatedUser.getLogin());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                if (user.getEmail().equals(updatedUser.getEmail())) {
                    log.warn("Электронная почта {} уже используется", updatedUser.getEmail());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
        }
        users.put(updatedUser.getId(), updatedUser);
        log.info("Обновлен пользователь: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}