package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.RelationStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final RelationStorage relationStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, @Qualifier("UserRelationsDbStorage") RelationStorage relationStorage) {
        this.userStorage = userStorage;
        this.relationStorage = relationStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        getUserById(userId);
        getUserById(friendId);
        relationStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        getUserById(userId);
        getUserById(friendId);
        relationStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(Integer id) {
       getUserById(id);
       return userStorage.getFriends(id);
    }

    public Collection<User> getMutualFriends(Integer friendId, Integer userId) {
        getUserById(userId);
        getUserById(friendId);
        return userStorage.getMutualFriends(friendId, userId);
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User putUser(User user) {
        getUserById(user.getId());
        return userStorage.putUser(user);
    }

    public void removeUser(Integer id) {
        userStorage.removeUser(id);
    }

}
