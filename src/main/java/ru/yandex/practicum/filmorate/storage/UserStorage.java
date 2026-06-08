package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public Collection<User> getUsers();

    public User getUserById(Integer id);

    public User addUser(User user);

    public User putUser(User newUser);

    public void removeUser(Integer id);

    public Collection<User> getFriends(Integer id);

    public Collection<User> getMutualFriends(Integer userId, Integer friendId);

}
