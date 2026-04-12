package ru.yandex.practicum.filmorate.storage;

public interface RelationStorage {
    public void addFriend(Integer userId, Integer friendId);

    public void removeFriend(Integer userId, Integer friendId);

}
