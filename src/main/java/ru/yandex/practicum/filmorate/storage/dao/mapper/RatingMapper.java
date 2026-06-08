package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.RatingDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingMapper implements RowMapper<RatingDto> {

    @Override
    public RatingDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RatingDto(rs.getInt("id"), rs.getString("name"));
    }
}
