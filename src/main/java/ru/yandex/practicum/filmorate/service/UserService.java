package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId).getFriendsId().add(friendId);
        userStorage.getUserById(friendId).getFriendsId().add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId).getFriendsId().remove(friendId);
        userStorage.getUserById(friendId).getFriendsId().remove(userId);
    }

    public Collection<User> getFriends(Integer id) {
        User user = userStorage.getUserById(id);
        return userStorage.getUsers().stream().filter(u -> user.getFriendsId().contains(u.getId()))
                .toList();
    }

    public Collection<User> getMutualFriends(Integer friendId, Integer userId) {
        return userStorage.getUserById(userId)
                .getFriendsId()
                .stream().filter(friendsId -> userStorage.getUserById(friendId).getFriendsId().contains(friendsId))
                .map(userStorage::getUserById).toList();
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
        return userStorage.putUser(user);
    }

    public void removeUser(Integer id) {
        userStorage.removeUser(id);
    }

}
