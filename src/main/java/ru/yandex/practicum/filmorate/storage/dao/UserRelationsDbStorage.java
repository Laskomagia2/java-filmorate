package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserRelations;
import ru.yandex.practicum.filmorate.storage.RelationStorage;


@Qualifier
@Component ("UserRelationsDbStorage")
public class UserRelationsDbStorage extends BaseDbStorage<UserRelations> implements RelationStorage {

    private final String INSERT_FRIENDSHIP = "INSERT INTO user_relations (requester_id, addressee_id) " +
            "VALUES (?, ?)";
    private final String REMOVE_FRIENDSHIP = "DELETE FROM user_relations " +
            "WHERE requester_id = ? AND addressee_id = ?";
    private final String CONFIRM_FRIENDSHIP = "UPDATE user_relations SET status = 2 " +
            "WHERE requester_id = ? AND addressee_id = ?";

    public UserRelationsDbStorage(JdbcTemplate jdbc, @Qualifier ("RelationsMapper") RowMapper<UserRelations> mapper) {
        super(jdbc, mapper);
    }

    public void addFriend(Integer userId, Integer friendId) {
        jdbc.update(
                INSERT_FRIENDSHIP,
                userId,
                friendId
        );
    }

    public void removeFriend(Integer userId, Integer friendId) {
        jdbc.update(
                REMOVE_FRIENDSHIP,
                userId, friendId
        );
    }

}
