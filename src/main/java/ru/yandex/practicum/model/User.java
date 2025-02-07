package ru.yandex.practicum.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна содержать символ @") // Кастомизированное сообщение
    private String email;

    @NotBlank(message = "Логин не может быть пустым") // Добавлено кастомизированное сообщение
    private String login;

    private String name;
    private LocalDate birthday;

}
