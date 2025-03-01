package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        return filmService.addFilm(film);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Film> addFilms(@Valid @RequestBody List<Film> films) {
        log.info("Получен запрос на создание нескольких фильмов");
        return filmService.addFilms(films);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable int filmId) {
        log.info("Получен запрос на удаление фильма с ID: {}", filmId);
        filmService.deleteFilm(filmId);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info("Получен запрос на обновление фильма: {}", updatedFilm);
        return filmService.updateFilm(updatedFilm);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на получение списка фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<Film> getFilmById(@PathVariable("filmId") int filmId) {
        log.info("Получен запрос на получение фильма с ID: {}", filmId);
        Film film = filmService.getFilmById(filmId);
        return ResponseEntity.ok(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable("filmId") Integer filmId,
                        @PathVariable("userId") Integer userId) {
        log.info("Получен запрос на добавление лайка фильму с ID: {} от пользователя с ID: {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable("filmId") Integer filmId,
                           @PathVariable("userId") Integer userId) {
        log.info("Получен запрос на удаление лайка у фильма с ID: {} от пользователя с ID: {}", filmId, userId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count) {
        log.info("Получен запрос на получение популярных фильмов, количество: {}", count);
        return filmService.getTopFilms(count);
    }
}