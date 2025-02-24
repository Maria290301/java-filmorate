package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int counter = 0;

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updatedFilm) {
        if (!films.containsKey(updatedFilm.getId())) {
            throw new UserNotFoundException("Фильм с id " + updatedFilm.getId() + " не найден");
        }
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Обновлен фильм: {}", updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}