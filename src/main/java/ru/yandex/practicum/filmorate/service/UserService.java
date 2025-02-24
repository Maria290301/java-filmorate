package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        log.info("Попытка добавить друга: userId={}, friendId={}", userId, friendId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            log.error("Пользователь с ID {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (friend == null) {
            log.error("Пользователь с ID {} не найден", friendId);
            throw new UserNotFoundException("Друг не найден");
        }
        log.info("Найдены пользователи: user={}, friend={}", user, friend);

        addFriendToUser(user, friendId);
        addFriendToUser(friend, userId);

        user.updateFriendCount();
        friend.updateFriendCount();

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("Друг успешно добавлен: userId={}, friendId={}", userId, friendId);
    }

    private void addFriendToUser(User user, int friendId) {
        if (!user.getFriends().contains(friendId)) {
            user.getFriends().add(friendId);
            log.info("Пользователь {} добавлен в друзья к пользователю {}", friendId, user.getId());
        } else {
            log.warn("Пользователь {} уже в друзьях у пользователя {}", friendId, user.getId());
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null || friend == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        user.getFriends().remove(Integer.valueOf(friendId));
        friend.getFriends().remove(Integer.valueOf(userId));

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public boolean exists(int userId) {
        return userStorage.getUserById(userId) != null;
    }

    public Set<Integer> getFriends(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с ID {} не найден, возвращаем пустой набор друзей", userId);
            return Collections.emptySet();
        }
        return new HashSet<>(user.getFriends());
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        User user1 = userStorage.getUserById(userId1);
        User user2 = userStorage.getUserById(userId2);

        if (user1 == null || user2 == null) {
            log.warn("Один из пользователей не найден: userId1={}, userId2={}", userId1, userId2);
            throw new UserNotFoundException("Один из пользователей не найден");
        }
        List<User> commonFriends = user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .map(userStorage::getUserById) // Получаем объекты пользователей
                .collect(Collectors.toList());

        log.info("Пользователь {} и пользователь {} имеют {} общих друзей", userId1, userId2, commonFriends.size());
        return commonFriends;
    }
}
