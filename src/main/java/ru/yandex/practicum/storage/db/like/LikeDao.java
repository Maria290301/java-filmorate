package ru.yandex.practicum.storage.db.like;

import ru.yandex.practicum.model.Film;

import java.util.List;

public interface LikeDao {

    void like(int filmId, int userId);

    void dislike(int filmId, int userId);

    int countLikes(int filmId);

    boolean isLiked(int filmId, int userId);

    List<Film> getTopFilms(int count);
}
