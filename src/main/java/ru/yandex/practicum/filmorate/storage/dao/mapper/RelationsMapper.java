package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.UserRelations;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("RelationsMapper")
public class RelationsMapper implements RowMapper<UserRelations> {

    @Override
    public UserRelations mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserRelations result = new UserRelations(rs.getInt("user_id"),
                rs.getInt("another_user_id"), rs.getInt("initiator_id"));
        if (rs.getInt("relation_status_id") > 1) {
            result.setRelationStatus(FriendshipStatus.CONFIRMED);
        } else {
            result.setRelationStatus(FriendshipStatus.UNCONFIRMED);
        }
        return result;
    }
}
