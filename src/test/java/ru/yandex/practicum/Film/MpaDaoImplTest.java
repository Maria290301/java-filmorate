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
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.db.mpa.MpaDaoImpl;
import ru.yandex.practicum.storage.mapper.MpaMapper;

import java.util.List;

public class MpaDaoImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private MpaDaoImpl mpaDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMpaByIdShouldReturnMpaWhenExists() {
        Mpa expectedMpa = new Mpa(1, "G");
        when(jdbcTemplate.queryForObject(anyString(), any(MpaMapper.class), eq(1))).thenReturn(expectedMpa);

        Mpa actualMpa = mpaDao.getMpaById(1);

        assertNotNull(actualMpa);
        assertEquals(expectedMpa, actualMpa);
    }

    @Test
    void getMpaByIdShouldThrowNotFoundExceptionWhenNotExists() {
        when(jdbcTemplate.queryForObject(anyString(), any(MpaMapper.class), eq(1)))
                .thenThrow(new EmptyResultDataAccessException(1));

        assertThrows(NotFoundException.class, () -> mpaDao.getMpaById(1));
    }

    @Test
    void isContainsShouldReturnTrueWhenMpaExists() {
        when(jdbcTemplate.queryForObject(anyString(), any(MpaMapper.class), eq(1))).thenReturn(new Mpa(1, "G"));

        assertTrue(mpaDao.isContains(1));
    }

    @Test
    void isContainsShouldReturnFalseWhenMpaDoesNotExist() {
        when(jdbcTemplate.queryForObject(anyString(), any(MpaMapper.class), eq(1)))
                .thenThrow(new EmptyResultDataAccessException(1));

        assertFalse(mpaDao.isContains(1));
    }

    @Test
    void getMpaListShouldReturnListOfMpas() {
        Mpa mpa1 = new Mpa(1, "G");
        Mpa mpa2 = new Mpa(2, "PG");
        List<Mpa> expectedList = List.of(mpa1, mpa2);
        when(jdbcTemplate.query(anyString(), any(MpaMapper.class))).thenReturn(expectedList);

        List<Mpa> actualList = mpaDao.getMpaList();

        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList, actualList);
    }
}
