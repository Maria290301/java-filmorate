package ru.yandex.practicum.Film;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.controller.FilmController;
import ru.yandex.practicum.exception.FilmNotFoundException;
import ru.yandex.practicum.exception.LikeNotFoundException;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FilmControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private FilmController filmController;

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private FilmService filmService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
    }

    @Test
    public void addFilmValidFilmReturnsCreated() throws Exception {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        Set<Integer> likes = new HashSet<>();
        film.setLikes(likes);

        when(filmStorage.addFilm(film)).thenReturn(film);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void addFilmInvalidDurationReturnsBadRequest() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-10);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmEmptyNameReturnsBadRequest() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmNullDescriptionReturnsBadRequest() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Valid Film");
        film.setDescription(null);
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmFutureReleaseDateReturnsBadRequest() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Future Film");
        film.setDescription("This film is set in the future.");
        film.setReleaseDate(LocalDate.now().plusDays(1));
        film.setDuration(120);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addLikeValidFilmAndUserReturnsNoContent() throws Exception {
        int filmId = 1;
        int userId = 2;

        doNothing().when(filmService).addLike(filmId, userId);

        mockMvc.perform(put("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void addLikeFilmNotFoundReturnsNotFound() throws Exception {
        int filmId = 1;
        int userId = 2;

        doThrow(new FilmNotFoundException("Фильм не найден с ID: " + filmId)).when(filmService).addLike(filmId, userId);

        mockMvc.perform(put("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Фильм не найден"));
    }

    @Test
    public void addLikeUserNotFoundReturnsNotFound() throws Exception {
        int filmId = 1;
        int userId = 2;

        doThrow(new UserNotFoundException("Пользователь не найден с ID: " + userId))
                .when(filmService).addLike(filmId, userId);

        mockMvc.perform(put("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    public void removeLikeValidFilmAndUserReturnsNoContent() throws Exception {
        int filmId = 1;
        int userId = 2;

        doNothing().when(filmService).removeLike(filmId, userId);

        mockMvc.perform(delete("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void removeLikeFilmNotFoundReturnsNotFound() throws Exception {
        int filmId = 1;
        int userId = 2;

        doThrow(new FilmNotFoundException("Фильм не найден с ID: " + filmId)).when(filmService).removeLike(filmId, userId);

        mockMvc.perform(delete("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Фильм не найден с ID: " + filmId));
    }

    @Test
    public void removeLikeUserNotFoundReturnsNotFound() throws Exception {
        int filmId = 1;
        int userId = 2;

        doThrow(new UserNotFoundException("Пользователь не найден с ID: " + userId))
                .when(filmService).removeLike(filmId, userId);

        mockMvc.perform(delete("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден с ID: " + userId));
    }

    @Test
    public void removeLikeFilmNotLikedReturnsConflict() throws Exception {
        int filmId = 1;
        int userId = 2;

        doThrow(new LikeNotFoundException("Пользователь не лайкнул фильм с ID: " + filmId))
                .when(filmService).removeLike(filmId, userId);

        mockMvc.perform(delete("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не лайкнул фильм с ID: " + filmId));
    }
}