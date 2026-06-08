package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component("FilmMapper")
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        int ratingId = rs.getInt("rating_id");
        RatingDto mpa = null;
        if (ratingId != 0) {
            mpa = new RatingDto(
                    ratingId,
                    rs.getString("rating_name")
            );
        }

        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("realise_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(new ArrayList<>())
                .likeCount(rs.getInt("likes_count"))
                .build();
    }
}
