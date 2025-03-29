package ru.yandex.practicum.storage.db.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.mapper.MpaMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(Integer id) {
        log.debug("getMpaById({})", id);
        try {
            Mpa mpa = jdbcTemplate.queryForObject(
                    "SELECT mpa_id, mpa_rating FROM film_mpa WHERE mpa_id=?",
                    new MpaMapper(), id);
            log.trace("The MPA rating {} was returned", mpa);
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            log.error("MPA with id {} not found", id);
            throw new NotFoundException("MPA with ID " + id + " not found");
        }
    }

    @Override
    public List<Mpa> getMpaList() {
        log.debug("getMpaList()");
        List<Mpa> mpaList = jdbcTemplate.query(
                "SELECT mpa_id, mpa_rating FROM film_mpa ORDER BY mpa_id",
                new MpaMapper());
        log.trace("These are all the mpa rating: {}", mpaList);
        return mpaList;
    }

    @Override
    public boolean isContains(Integer id) {
        log.debug("isContains({})", id);
        try {
            getMpaById(id);
            log.trace("The MPA with id {} was found", id);
            return true;
        } catch (NotFoundException e) {
            log.trace("The MPA with id {} was not found", id);
            return false;
        }
    }
}
