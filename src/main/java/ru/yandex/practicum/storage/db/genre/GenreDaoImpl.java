package ru.yandex.practicum.storage.db.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.mapper.GenreMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(Integer id) {
        log.debug("getGenreById({})", id);
        Genre genre = jdbcTemplate.queryForObject("SELECT genre_id, genre_type FROM genre WHERE genre_id=?",
                new GenreMapper(), id);
        log.trace("The genre type with id {} was returned", id);
        return genre;
    }

    @Override
    public List<Genre> getGenres() {
        log.debug("getGenres()");
        List<Genre> genreList = jdbcTemplate.query(
                "SELECT DISTINCT genre_id, genre_type FROM genre ORDER BY genre_id",
                new GenreMapper()
        );
        log.trace("These are all unique genre types: {}", genreList);
        return genreList;
    }

    public List<Genre> getGenresForFilm(Integer filmId) {
        log.debug("getGenresForFilm({})", filmId);
        List<Genre> genreList = jdbcTemplate.query(
                "SELECT DISTINCT g.genre_id, g.genre_type FROM genre g " +
                        "JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                        "WHERE fg.film_id = ?",
                new GenreMapper(),
                filmId
        );
        log.trace("Unique genres for filmId {}: {}", filmId, genreList);
        return genreList;
    }

    @Override
    public boolean isContains(Integer id) {
        log.debug("isContains({})", id);
        try {
            getGenreById(id);
            log.trace("The genre with id {} was found", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("No information for id {} was found", id);
            return false;
        }
    }
}
