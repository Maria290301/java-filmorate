package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.FilmNotFoundException;
import ru.yandex.practicum.exception.LikeNotFoundException;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Фильм не найден с ID: " + filmId);
        }
        if (!userService.exists(userId)) {
            throw new UserNotFoundException("Пользователь не найден с ID: " + userId);
        }
        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        if (!userService.exists(userId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        if (!film.getLikes().remove((Integer) userId)) {
            log.warn("Лайк пользователя {} не найден для фильма с ID {}", userId, filmId);
            throw new LikeNotFoundException("Лайк не найден для пользователя " + userId + " на фильме " + filmId);
        }
        log.info("Лайк пользователя {} удалён для фильма с ID {}", userId, filmId);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> allFilms = filmStorage.getAllFilms();
        if (allFilms == null || allFilms.isEmpty()) {
            return Collections.emptyList();
        }
        return allFilms.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
