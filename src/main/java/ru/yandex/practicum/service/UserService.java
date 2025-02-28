package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public List<User> addUsers(List<User> users) {
        return userStorage.addUsers(users);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(int userId, int friendId) {
        log.info("Попытка добавить друга: пользователь ID={}, друг ID={}", userId, friendId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user == null) {
            log.warn("Пользователь не найден: userId={}", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }

        if (friend == null) {
            log.warn("Друг не найден: friendId={}", friendId);
            throw new UserNotFoundException("Друг не найден");
        }

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }

        if (!user.getFriends().contains(friendId)) {
            user.getFriends().add(friendId);
            log.info("Пользователь {} добавлен в друзья к пользователю {}", friendId, userId);
        } else {
            log.warn("Пользователь {} уже в друзьях у пользователя {}", friendId, userId);
            throw new ConflictException("Пользователь уже в друзьях");
        }

        if (!friend.getFriends().contains(userId)) {
            friend.getFriends().add(userId);
            log.info("Пользователь {} добавлен в друзья к пользователю {}", userId, friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user == null || friend == null) {
            throw new UserNotFoundException("Один из пользователей не найден");
        }

        user.getFriends().remove(Integer.valueOf(friendId));
        friend.getFriends().remove(Integer.valueOf(userId));
        log.info("Пользователь {} удален из друзей пользователя {}", friendId, userId);
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        User user1 = getUserById(userId1);
        User user2 = getUserById(userId2);

        Set<Integer> friendsSet = new HashSet<>(user1.getFriends());
        List<User> commonFriends = user2.getFriends().stream()
                .filter(friendsSet::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());

        log.info("Общие друзья между пользователями {} и {}: {}", userId1, userId2, commonFriends);
        return commonFriends;
    }

    public List<User> getFriends(int userId) {
        User user = getUserById(userId);
        if (user == null || user.getFriends() == null) {
            log.warn("Пользователь с ID {} не найден или у него нет друзей", userId);
            return Collections.emptyList();
        }

        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            User friend = getUserById(friendId);
            if (friend != null) {
                friendsList.add(friend);
            }
        }
        log.info("Друзья пользователя с ID {}: {}", userId, friendsList);
        return friendsList;
    }
}
