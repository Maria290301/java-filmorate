package ru.yandex.practicum.Film;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.db.genre.GenreDaoImpl;
import ru.yandex.practicum.storage.mapper.GenreMapper;

import java.util.Collections;
import java.util.List;

public class GenreDaoImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private GenreDaoImpl genreDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getGenreByIdShouldReturnGenreWhenExists() {
        Genre expectedGenre = new Genre(1, "Action");
        when(jdbcTemplate.queryForObject(anyString(), any(GenreMapper.class), eq(1))).thenReturn(expectedGenre);

        Genre actualGenre = genreDao.getGenreById(1);

        assertNotNull(actualGenre);
        assertEquals(expectedGenre, actualGenre);
    }

    @Test
    void isContainsShouldReturnTrueWhenGenreExists() {
        when(jdbcTemplate.queryForObject(anyString(), any(GenreMapper.class), eq(1))).thenReturn(new Genre(1, "Action"));

        assertTrue(genreDao.isContains(1));
    }

    @Test
    void isContainsShouldReturnFalseWhenGenreDoesNotExist() {
        when(jdbcTemplate.queryForObject(anyString(), any(GenreMapper.class), eq(1)))
                .thenThrow(new EmptyResultDataAccessException(1));

        assertFalse(genreDao.isContains(1));
    }

    @Test
    void getGenresShouldReturnListOfGenres() {
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(2, "Comedy");
        List<Genre> expectedList = List.of(genre1, genre2);
        when(jdbcTemplate.query(anyString(), any(GenreMapper.class))).thenReturn(expectedList);

        List<Genre> actualList = genreDao.getGenres();

        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList, actualList);
    }

    @Test
    void getGenresForFilmShouldReturnListOfGenresWhenFilmExists() {
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(2, "Comedy");
        List<Genre> expectedList = List.of(genre1, genre2);
        when(jdbcTemplate.query(anyString(), any(GenreMapper.class), eq(1))).thenReturn(expectedList);

        List<Genre> actualList = genreDao.getGenresForFilm(1);

        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList, actualList);
    }

    @Test
    void getGenresForFilmShouldReturnEmptyListWhenFilmHasNoGenres() {
        when(jdbcTemplate.query(anyString(), any(GenreMapper.class), eq(1))).thenReturn(Collections.emptyList());

        List<Genre> actualList = genreDao.getGenresForFilm(1);

        assertNotNull(actualList);
        assertTrue(actualList.isEmpty());
    }
}
