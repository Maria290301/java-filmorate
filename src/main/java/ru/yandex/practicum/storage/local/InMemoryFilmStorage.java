package ru.yandex.practicum.storage.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.db.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public Film addFilm(Film film) {
        validate(film);
        films.put(film.getId(), film);
        log.info("'{}' movie was added to a library, the identifier is '{}'", film.getName(), film.getId());
        return film;
    }

    @Override
    public void deleteFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Попытка удалить фильм с ID {}, который не найден", filmId);
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        films.remove(filmId);
        log.info("Фильм с ID {} был удален", filmId);
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        if (films.containsKey(updatedFilm.getId())) {
            validate(updatedFilm);
            films.put(updatedFilm.getId(), updatedFilm);
            log.info("'{}' movie was updated in a library, the identifier is '{}'", updatedFilm.getName(), updatedFilm.getId());
            return updatedFilm;
        } else {
            throw new NotFoundException("Attempt to update non-existing movie");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("There are '{}' movies in a library now", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int filmId) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Attempt to reach non-existing movie with id '" + id + "'");
        }
        return films.get(id);
    }

    @Override
    public void addGenres(int filmId, Set<Genre> genres) {

    }

    @Override
    public void updateGenres(int filmId, Set<Genre> genres) {

    }

    @Override
    public Set<Genre> getGenres(int filmId) {
        return null;
    }

    @Override
    public void deleteGenres(int filmId) {

    }

    @Override
    public boolean isContains(int id) {
        return false;
    }

    private void validate(Film film) {
        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Incorrect release date");
        }
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("Attempt to set an empty movie name");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Attempt to set duration less than zero");
        }
        if (film.getDescription().length() > 200 || film.getDescription().length() == 0) {
            throw new ValidationException("Description increases 200 symbols or empty");
        }
        if (film.getId() == null || film.getId() <= 0) {
            film.setId(++id);
            log.info("Movie identifier was set as '{}", film.getId());
        }
    }
}
