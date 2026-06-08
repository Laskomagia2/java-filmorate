package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements RowMapper<GenreDto> {

    @Override
    public GenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new GenreDto(rs.getInt("id"), rs.getString("name"));
    }
}
