package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.FilmNotFoundException;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public List<Film> addFilms(List<Film> films) {
        return filmStorage.addFilms(films);
    }

    public void deleteFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public Film updateFilm(Film updatedFilm) {
        return filmStorage.updateFilm(updatedFilm);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            throw new IllegalArgumentException("Фильм ID и пользователь ID не могут быть null");
        }

        Film film = getFilmById(filmId);

        if (!userStorage.userExists(userId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }

        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
            log.info("Пользователь с ID {} добавил лайк к фильму с ID {}", userId, filmId);
        } else {
            log.info("Пользователь с ID {} уже лайкал фильм с ID {}", userId, filmId);
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            throw new IllegalArgumentException("Фильм ID и пользователь ID не могут быть null");
        }

        Film film = getFilmById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }

        if (!userStorage.userExists(userId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }

        log.info("Текущие лайки для фильма {}: {}", filmId, film.getLikes());

        if (!film.getLikes().remove(userId)) {
            throw new IllegalArgumentException("Пользователь не имеет лайка к фильму с ID " + filmId);
        }

        log.info("Пользователь с ID {} убрал лайк к фильму с ID {}", userId, filmId);
    }

    public List<Film> getTopFilms(int count) {
        if (count <= 0) {
            log.warn("Запрашиваемое количество фильмов должно быть больше нуля");
            throw new IllegalArgumentException("Количество фильмов должно быть больше нуля");
        }

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
