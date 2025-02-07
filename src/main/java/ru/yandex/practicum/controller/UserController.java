package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final List<User> users = new ArrayList<>();
    private int userIdCounter = 1; // Счетчик для уникальных ID

    @PostMapping
    public ResponseEntity<User> addUser (@Valid @RequestBody User user) {
        user.setId(userIdCounter++); // Устанавливаем уникальный ID
        users.add(user);
        log.info("Добавлен пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser (@PathVariable("id") int id, @Valid @RequestBody User updatedUser ) {
        Optional<User> existingUser  = users.stream().filter(user -> user.getId() == id).findFirst();
        if (existingUser .isPresent()) {
            User user = existingUser .get();
            user.setEmail(updatedUser .getEmail());
            user.setLogin(updatedUser .getLogin());
            user.setName(updatedUser .getName());
            user.setBirthday(updatedUser .getBirthday());
            log.info("Обновлен пользователь: {}", user);
            return ResponseEntity.ok(user);
        }
        log.warn("Пользователь с id {} не найден", id);
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<User> getUsers() {
        return users;
    }
}
