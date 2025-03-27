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
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;

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

        when(filmService.addFilm(film)).thenReturn(film);

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
        Integer filmId = 1;
        Integer userId = 1;

        doNothing().when(filmService).addLike(filmId, userId);

        mockMvc.perform(put("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void addLikeFilmNotFoundReturnsNotFound() throws Exception {
        int filmId = 1;
        int userId = 2;

        doThrow(new NotFoundException("Film not found")).when(filmService).addLike(filmId, userId);

        mockMvc.perform(put("/{filmId}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addLikeUserNotFoundReturnsNotFound() throws Exception {
        int filmId = 1;
        int userId = 2;

        doThrow(new NotFoundException("User not found")).when(filmService).addLike(filmId, userId);

        mockMvc.perform(put("/{filmId}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void removeLikeValidFilmAndUserReturnsNoContent() throws Exception {
        int filmId = 1;
        int userId = 2;

        doNothing().when(filmService).removeLike(filmId, userId);

        mockMvc.perform(delete("/films/{filmId}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        verify(filmService, times(1)).removeLike(filmId, userId);
    }

    @Test
    public void removeLikeFilmNotFoundReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Фильм с ID 1 не найден"))
                .when(filmService).removeLike(1, 1);

        mockMvc.perform(delete("/1/like/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void removeLikeUserNotFoundReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь с ID 1 не найден"))
                .when(filmService).removeLike(1, 1);

        mockMvc.perform(delete("/1/like/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFilmsReturnsFilmList() throws Exception {
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Film 1");
        film1.setDescription("Description 1");

        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Film 2");
        film2.setDescription("Description 2");

        List<Film> films = Arrays.asList(film1, film2);
        when(filmService.getAllFilms()).thenReturn(films);

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Film 1"))
                .andExpect(jsonPath("$[1].name").value("Film 2"));
    }

    @Test
    public void getFilmByIdValidIdReturnsFilm() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Film 1");
        film.setDescription("Description 1");

        when(filmService.getFilmById(1)).thenReturn(film);

        mockMvc.perform(get("/films/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film 1"));
    }

    @Test
    public void getPopularFilmsReturnsPopularFilms() throws Exception {
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Popular Film 1");
        film1.setDescription("Description 1");

        List<Film> films = Arrays.asList(film1);
        when(filmService.getTopFilms(1)).thenReturn(films);

        mockMvc.perform(get("/films/popular?count=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Popular Film 1"));
    }

  /*  @Test
    public void getTopFilmsNoFilmsReturnsEmptyList() {
        when(filmService.getAllFilms()).thenReturn(Collections.emptyList());

        List<Film> result = filmController.getTopFilms(5);

        assertTrue(result.isEmpty());
    }*/
}