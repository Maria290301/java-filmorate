package ru.yandex.practicum.filmorate.Film;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.controller.FilmController;
import ru.yandex.practicum.model.Film;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FilmControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FilmController()).build();
    }

    @Test
    public void addFilmValidFilmReturnsCreated() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
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
}