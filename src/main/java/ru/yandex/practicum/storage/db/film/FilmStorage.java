package ru.yandex.practicum.storage.db.film;

import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    void deleteFilm(int filmId);

    Film updateFilm(Film updatedFilm);

    List<Film> getAllFilms();

    Film getFilmById(int filmId);

    void addGenres(int filmId, Set<Genre> genres);

    void updateGenres(int filmId, Set<Genre> genres);

    Set<Genre> getGenres(int filmId);

    void deleteGenres(int filmId);

    boolean isContains(int id);
}
