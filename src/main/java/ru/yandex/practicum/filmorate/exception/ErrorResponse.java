package ru.yandex.practicum.filmorate.exception;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ErrorResponse {
    private String message;
    private Map<String, String> fieldErrors;

    public ErrorResponse(String message) {
        this.message = message;
        this.fieldErrors = new HashMap<>();
    }

    public void addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
    }
}
