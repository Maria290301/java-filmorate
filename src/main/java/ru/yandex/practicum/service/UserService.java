package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.db.friendship.FriendshipDao;

import ru.yandex.practicum.storage.db.user.UserDbStorage;
import ru.yandex.practicum.storage.db.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserDbStorage userStorage,
                       FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
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

    public Optional<User> getUserById(int id) {
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

        User user = getUserById(userId).orElseThrow(() -> {
            log.warn("Пользователь не найден: userId={}", userId);
            throw new NotFoundException("Пользователь не найден");
        });

        User friend = getUserById(friendId).orElseThrow(() -> {
            log.warn("Друг не найден: friendId={}", friendId);
            throw new NotFoundException("Друг не найден");
        });
        friendshipDao.addFriend(userId, friendId, true);
        log.info("Пользователь {} добавлен в друзья к пользователю {}", friendId, userId);
    }

    public void removeFriend(int userId, int friendId) {
        if (!userStorage.userExists(userId) || !userStorage.userExists(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        if (!friendshipDao.isFriend(userId, friendId)) {
            log.warn("Попытка удалить дружбу, которая не существует между пользователями {} и {}", userId, friendId);
            return;
        }

        try {
            friendshipDao.deleteFriend(userId, friendId);
            log.info("Дружба между пользователями {} и {} удалена", userId, friendId);
        } catch (Exception e) {
            log.error("Ошибка при удалении дружбы между пользователями {} и {}: {}", userId, friendId, e.getMessage());
            throw new RuntimeException("Не удалось удалить дружбу. Пожалуйста, попробуйте позже.", e);
        }
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        User user1 = getUserById(userId1).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId1 + " не найден"));
        User user2 = getUserById(userId2).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId2 + " не найден"));

        List<Integer> friendsUser1 = friendshipDao.getFriends(userId1);
        List<Integer> friendsUser2 = friendshipDao.getFriends(userId2);

        List<User> commonFriends = friendsUser1.stream()
                .filter(friendsUser2::contains)
                .map(this::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        log.info("Общие друзья между пользователями {} и {}: {}", userId1, userId2, commonFriends);
        return commonFriends;
    }

    public List<User> getFriends(int userId) {
        User user = getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        List<Integer> friendIds = friendshipDao.getFriends(userId);
        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : friendIds) {
            User friend = getUserById(friendId).orElse(null);
            if (friend != null) {
                friendsList.add(friend);
            }
        }

        log.info("Друзья пользователя с ID {}: {}", userId, friendsList);
        return friendsList;
    }
}
