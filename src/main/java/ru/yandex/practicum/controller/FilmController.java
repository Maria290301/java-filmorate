package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final List<Film> films = new ArrayList<>();
    private int filmIdCounter = 1; // Счетчик для уникальных ID

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        film.setId(filmIdCounter++); // Устанавливаем уникальный ID
        films.add(film);
        log.info("Добавлен фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable int id, @Valid @RequestBody Film updatedFilm) {
        Optional<Film> existingFilm = films.stream().filter(film -> film.getId() == id).findFirst();
        if (existingFilm.isPresent()) {
            Film film = existingFilm.get();
            film.setName(updatedFilm.getName());
            film.setDescription(updatedFilm.getDescription());
            film.setReleaseDate(updatedFilm.getReleaseDate());
            film.setDuration(updatedFilm.getDuration());
            log.info("Обновлен фильм: {}", film);
            return ResponseEntity.ok(film);
        }
        log.warn("Фильм с id {} не найден", id);
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Film> getFilms() {
        return films;
    }
}

