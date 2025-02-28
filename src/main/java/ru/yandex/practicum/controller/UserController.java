package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        return userService.addUser(user);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public List<User> addUsers(@Valid @RequestBody List<User> users) {
        log.info("Получен запрос на создание группы пользователей: {}", users);

        List<User> addedUsers = userService.addUsers(users);

        log.info("Успешно добавлены пользователи: {}", addedUsers);
        return addedUsers;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer userId) {
        log.info("Получен запрос на удаление пользователя с ID: {}", userId);
        userService.deleteUser(userId);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Получен запрос на обновление пользователя: {}", updatedUser);
        return userService.updateUser(updatedUser);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        log.info("Получен запрос на получение пользователя с ID: {}", userId);
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable("userId") Integer userId,
                          @PathVariable("friendId") Integer friendId) {
        log.info("Получен запрос на добавление друга: пользователь ID={}, друг ID={}", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable("userId") Integer userId,
                             @PathVariable("friendId") Integer friendId) {
        log.info("Получен запрос на удаление друга: пользователь ID={}, друг ID={}", userId, friendId);
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable("userId") int userId) {
        log.info("Получен запрос на получение друзей пользователя с ID: {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId1}/friends/common/{userId2}")
    public List<User> getCommonFriends(@PathVariable("userId1") Integer userId1, @PathVariable("userId2") Integer userId2) {
        log.info("Получен запрос на получение общих друзей: пользователь1 ID={}, пользователь2 ID={}", userId1, userId2);
        return userService.getCommonFriends(userId1, userId2);
    }
}