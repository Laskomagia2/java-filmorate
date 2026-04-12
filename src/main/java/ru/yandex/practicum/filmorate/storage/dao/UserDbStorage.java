package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Component ("UserDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    UserRelationsDbStorage relationsDbStorage;

    private final String findAllQuery = "SELECT * FROM users";
    private final String findFilmByIdQuery = "SELECT * FROM users WHERE id = ?";
    private final String insertUserQuery = "INSERT INTO users(email, login, user_name, birthday) " +
    "VALUES (?, ?, ?, ?)";
    private final String updateUserQuery = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? " +
    "WHERE id = ?";
    private final String deleteUserQuery = "DELETE FROM users WHERE id = ?";
    private final String findUserFriend = "SELECT u.* " +
    "FROM users u " +
    "JOIN user_relations r " +
    "ON r.addressee_id = u.id " +
    "WHERE r.requester_id = ?";

    private final String findMutualFriends = "SELECT u.* " +
    "FROM users u " +
    "JOIN user_relations r1 ON r1.addressee_id = u.id AND r1.requester_id = ? " +
    "JOIN user_relations r2 ON r2.addressee_id = u.id AND r2.requester_id = ?";


    public UserDbStorage(JdbcTemplate jdbc, @Qualifier ("UserMapper") RowMapper<User> mapper, UserRelationsDbStorage relationsDbStorage) {
        super(jdbc, mapper);
        this.relationsDbStorage = relationsDbStorage;
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(findAllQuery);
    }

    @Override
    public User getUserById(Integer id) {
        if (findOne(findFilmByIdQuery, id).isPresent()) {
            return findOne(findFilmByIdQuery, id).get();
        } else {
            throw new NotFoundException("User " + id + " not found");
        }
    }

    @Override
    public User addUser(User user) {
        validateUser(user);
        int id = insert(
                insertUserQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User putUser(User newUser) {
        update(
                updateUserQuery,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public void removeUser(Integer id) {
        delete(deleteUserQuery, id);
    }

    private void validateUser(User user) {
        if (user.getEmail().isBlank()) {
            log.error("Validation error: Email must not be empty");
            throw new ValidationException("Email must not be empty");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Validation error: Email must contains char '@'");
            throw new ValidationException("Email must contains char '@'");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Validation error: Login must not contains space chars or be blank");
            throw new ValidationException("Login must not contains space chars or be blank");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Validation error: Birthday must not be after actual date");
            throw new ValidationException("Birthday must not be after actual date");
        }
    }

    public Collection<User> getFriends(Integer id) {
        try {
            return findMany(findUserFriend, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Deleting friend error");
        }
    }

    public Collection<User> getMutualFriends(Integer friendId, Integer userId) {
        try {
            return findMany(findMutualFriends, userId, friendId);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Deleting friend error");
        }
    }

    public void addFriend(Integer userId, Integer friendId) {
        relationsDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        relationsDbStorage.removeFriend(userId, friendId);
    }

}
