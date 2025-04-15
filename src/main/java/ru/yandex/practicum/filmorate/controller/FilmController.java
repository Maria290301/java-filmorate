package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final String pathLike = "/{id}/like/{user-id}";

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable int id) {
        return filmService.findById(id);
    }

    @PostMapping
    public FilmDto create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping()
    public FilmDto update(@RequestBody Film film) {
        return filmService.update(film);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable int id) {
        filmService.remove(id);
    }

    @PutMapping(pathLike)
    public void addLike(@PathVariable int id,
                        @PathVariable(name = "user-id") int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(pathLike)
    public void removeLike(@PathVariable int id,
                           @PathVariable(name = "user-id") int userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> findPopularFilms(@RequestParam(defaultValue = "10") int count,
                                          @RequestParam(defaultValue = "0") int genreId,
                                          @RequestParam(defaultValue = "0") int year) {
        return filmService.findPopularFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public List<FilmDto> searchFilms(@RequestParam String query,
                                     @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{director-id}")
    public List<FilmDto> findPopularFilms(@PathVariable(name = "director-id") int directorId,
                                          @RequestParam() String sortBy) {
        return filmService.findFilmsOfDirector(directorId, sortBy);
    }

    @GetMapping("/user/{id}")
    public List<FilmDto> getFilmsByUser(@PathVariable int id) {
        return filmService.getFilmsByUserId(id);
    }
}