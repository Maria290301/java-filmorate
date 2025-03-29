package ru.yandex.practicum.storage.db.friendship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Friendship;
import ru.yandex.practicum.storage.mapper.FriendshipMapper;

import java.util.List;

@Slf4j
@Repository
public class FriendshipDaoImpl implements FriendshipDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int userId, int friendId, boolean isFriend) {
        log.debug("addFriend({}, {}, {})", userId, friendId, isFriend);
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, is_friend) VALUES(?, ?, ?)",
                userId, friendId, isFriend);
        Friendship friendship = getFriend(userId, friendId);
        log.trace("These users are friends now: {}", friendship);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        log.debug("Удаление дружбы: пользователь ID={}, друг ID={}", userId, friendId);

        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, friendId);

        if (rowsAffected == 0) {
            log.warn("Не удалось удалить дружбу между пользователями {} и {}: запись не найдена", userId, friendId);
            return;
        }

        log.info("Дружба между пользователями {} и {} удалена", userId, friendId);
    }

    @Override
    public List<Integer> getFriends(int userId) {
        log.debug("Получение списка друзей для пользователя ID={}", userId);
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? AND is_friend = true";
        List<Integer> friendsList = jdbcTemplate.queryForList(sql, Integer.class, userId);
        log.info("Друзья пользователя с ID {}: {}", userId, friendsList);
        return friendsList;
    }

    @Override
    public Friendship getFriend(int userId, int friendId) {
        log.debug("Получение информации о дружбе между пользователями ID={} и ID={}", userId, friendId);
        String sql = "SELECT user_id, friend_id, is_friend FROM friends WHERE user_id = ? AND friend_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new FriendshipMapper(), userId, friendId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Дружба между пользователями ID={} и ID={} не найдена", userId, friendId);
            return null;
        }
    }

    @Override
    public boolean isFriend(int userId, int friendId) {
        log.debug("Проверка дружбы между пользователями ID={} и ID={}", userId, friendId);
        Friendship friendship = getFriend(userId, friendId);
        boolean isFriend = friendship != null && friendship.isFriend();
        log.info("Пользователь ID={} и пользователь ID={} являются друзьями: {}", userId, friendId, isFriend);
        return isFriend;
    }
}
