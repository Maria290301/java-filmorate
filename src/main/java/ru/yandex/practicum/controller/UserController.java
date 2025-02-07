package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final List<User> users = new ArrayList<>();

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        users.add(user);
        log.info("Добавлен пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser (@PathVariable("id") int id, @Valid @RequestBody User updatedUser ) {
        for (User  user : users) {
            if (user.getId() == id) {
                user.setEmail(updatedUser .getEmail());
                user.setLogin(updatedUser .getLogin());
                user.setName(updatedUser .getName());
                user.setBirthday(updatedUser .getBirthday());
                log.info("Обновлен пользователь: {}", user);
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<User> getUsers(){
        return users;
    }
}