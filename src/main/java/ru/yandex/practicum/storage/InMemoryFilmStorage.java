package ru.yandex.practicum.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.FilmNotFoundException;
import ru.yandex.practicum.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @Override
    public Film addFilm(Film film) {
        if (film == null) {
            log.warn("Попытка добавить null фильм");
            throw new IllegalArgumentException("Фильм не может быть null");
        }
        film.setId(currentId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public List<Film> addFilms(List<Film> filmsToAdd) {
        if (filmsToAdd == null || filmsToAdd.isEmpty()) {
            log.warn("Попытка добавить пустой список фильмов");
            throw new IllegalArgumentException("Список фильмов не может быть пустым");
        }
        return filmsToAdd.stream()
                .map(this::addFilm)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Попытка удалить фильм с ID {}, который не найден", filmId);
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        films.remove(filmId);
        log.info("Фильм с ID {} был удален", filmId);
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        if (updatedFilm == null || !films.containsKey(updatedFilm.getId())) {
            log.warn("Попытка обновить фильм, который не найден или является null: {}", updatedFilm);
            throw new FilmNotFoundException("Фильм с ID " + (updatedFilm != null ? updatedFilm.getId() : "null") + " не найден");
        }
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Обновлен фильм: {}", updatedFilm);
        return updatedFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            log.warn("Фильм с ID {} не найден", filmId);
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        log.info("Получен фильм: {}", film);
        return film;
    }
}
