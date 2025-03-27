package ru.yandex.practicum.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaMapper implements RowMapper<Mpa> {

    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_rating"));
        return mpa;
    }
}