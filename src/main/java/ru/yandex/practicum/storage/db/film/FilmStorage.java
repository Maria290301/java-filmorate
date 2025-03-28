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

    Set<Genre> getGenres(int filmId);

    boolean isContains(int id);
}
