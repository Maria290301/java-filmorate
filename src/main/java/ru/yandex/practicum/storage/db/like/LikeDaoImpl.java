package ru.yandex.practicum.storage.db.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.mapper.FilmMapper;
import ru.yandex.practicum.storage.mapper.LikeMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void like(int filmId, int userId) {
        log.debug("like({}, {})", filmId, userId);
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        log.trace("The movie {} was liked by user {}", filmId, userId);
    }

    @Override
    public void dislike(int filmId, int userId) {
        log.debug("dislike({}, {})", filmId, userId);
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
        log.trace("The user {}, disliked the movie {}", userId, filmId);
    }

    @Override
    public int countLikes(int filmId) {
        log.debug("countLikes({}).", filmId);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id=?", Integer.class, filmId);

        if (count == null) {
            log.warn("No likes found for movie {}", filmId);
            return 0;
        }

        log.trace("The movie {} liked {} times", filmId, count);
        return count;
    }

    @Override
    public boolean isLiked(int filmId, int userId) {
        log.debug("isLiked({}, {})", filmId, userId);
        try {
            jdbcTemplate.queryForObject("SELECT film_id, user_id FROM likes WHERE film_id=? AND user_id=?",
                    new LikeMapper(), filmId, userId);
            log.trace("The movie {} was liked by user {}", filmId, userId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("There is no like for film {} from user {}", filmId, userId);
            return false;
        }
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = "SELECT f.*, COUNT(l.user_id) AS likes_count FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmMapper(), count);
    }
}
