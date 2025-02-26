package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        User addUser = userStorage.addUser(user);
        return new ResponseEntity<>(addUser, HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<User>> addUsers(@Valid @RequestBody List<User> users) {
        List<User> addUsers = users.stream()
                .map(userStorage::addUser)
                .collect(Collectors.toList());
        return new ResponseEntity<>(addUsers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        try {
            userStorage.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя с ID " + userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updatedUser) {
        User user = userStorage.updateUser(updatedUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        try {
            User user = userStorage.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя с ID " + userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Map<String, String>> addFriend(@PathVariable("userId") int userId,
                                                         @PathVariable("friendId") int friendId) {
        try {
            userService.addFriend(userId, friendId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Друг успешно добавлен");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            log.warn("Пользователь не найден: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Пользователь с ID " + friendId + " не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Ошибка при добавлении друга: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Произошла ошибка при добавлении друга");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Map<String, String>> removeFriend(@PathVariable("userId") int userId,
                                                            @PathVariable("friendId") int friendId) {
        try {
            userService.removeFriend(userId, friendId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Друг успешно удален");

            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            log.warn("Пользователь не найден: {}", e.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", "Пользователь с ID " + userId + " или друг с ID " + friendId + " не найден");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка: " + e.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", "Некорректный запрос");

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Ошибка при удалении друга: ", e);

            Map<String, String> response = new HashMap<>();
            response.put("error", "Произошла ошибка при удалении друга");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<?> getFriends(@PathVariable("userId") int userId) {
        try {
            User user = userStorage.getUserById(userId);
            if (user == null) {
                log.warn("Пользователь не найден: userId={}", userId);
                Map<String, String> response = new HashMap<>();
                response.put("error", "Пользователь с ID " + userId + " не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            List<User> friends = userStorage.getFriendsByUserId(userId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            log.error("Ошибка при получении друзей пользователя с ID " + userId, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Произошла ошибка при получении друзей");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{userId1}/friends/common/{userId2}")
    public ResponseEntity<?> getCommonFriends(@PathVariable("userId1") Integer userId1,
                                              @PathVariable("userId2") Integer userId2) {
        try {
            List<User> commonFriends = userService.getCommonFriends(userId1, userId2);
            return ResponseEntity.ok(commonFriends);
        } catch (UserNotFoundException e) {
            log.warn("Пользователь не найден: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Один из пользователей не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Ошибка при получении общих друзей: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Произошла ошибка при получении общих друзей");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

