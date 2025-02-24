package ru.yandex.practicum.filmorate.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void userWithEmptyEmailShouldFailValidation() {
        User user = new User();
        user.setEmail("");
        user.setLogin("validLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    public void userWithInvalidEmailShouldFailValidation() {
        User user = new User();
        user.setEmail("invalidEmail");
        user.setLogin("validLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Электронная почта должна содержать символ @", violations.iterator().next().getMessage());
    }

    @Test
    public void userWithEmptyLoginShouldFailValidation() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin(""); // Пустой логин
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("Количество нарушений: " + violations.size());
        assertEquals(2, violations.size(), "Ожидалось 2 нарушения валидации");
        boolean hasEmptyLoginViolation = violations.stream()
                .anyMatch(v -> "Логин не может быть пустым".equals(v.getMessage()));
        boolean hasSpaceViolation = violations.stream()
                .anyMatch(v -> "Логин не должен содержать пробелы".equals(v.getMessage()));
        assertTrue(hasEmptyLoginViolation, "Ожидалось нарушение: Логин не может быть пустым");
        assertTrue(hasSpaceViolation, "Ожидалось нарушение: Логин не должен содержать пробелы");
    }
}