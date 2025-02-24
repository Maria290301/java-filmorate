package ru.yandex.practicum.filmorate.film;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import jakarta.validation.ConstraintViolation;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void filmWithEmptyName_ShouldFailValidation() {
        Film film = new Film();
        film.setName("");
        film.setDescription("A valid description.");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    public void filmWithNegativeDuration_ShouldFailValidation() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A valid description.");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-10);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    public void filmWithFutureReleaseDate_ShouldFailValidation() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A valid description.");
        film.setReleaseDate(LocalDate.now().plusDays(1));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("Количество нарушений: " + violations.size());
        for (ConstraintViolation<Film> violation : violations) {
            System.out.println("Нарушение: " + violation.getMessage());
        }
        assertEquals(1, violations.size());
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", violations.iterator().next().getMessage());
    }
}