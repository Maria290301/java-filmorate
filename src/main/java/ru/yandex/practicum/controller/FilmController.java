package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();


    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            log.warn("Фильм с id {} уже существует", film.getId());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updatedFilm) {
        if (!films.containsKey(updatedFilm.getId())) {
            log.warn("Фильм с id {} не найден", updatedFilm.getId());
            return ResponseEntity.notFound().build();
        }
        for (Film film : films.values()) {
            if (film.getId() != updatedFilm.getId() && film.getName().equals(updatedFilm.getName())) {
                log.warn("Название фильма {} уже используется", updatedFilm.getName());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
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
