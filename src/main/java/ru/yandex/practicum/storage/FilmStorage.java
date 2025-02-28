package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    List<Film> addFilms(List<Film> filmsToAdd);

    void deleteFilm(int filmId);

    Film updateFilm(Film updatedFilm);

    List<Film> getAllFilms();

    Film getFilmById(int filmId);
}