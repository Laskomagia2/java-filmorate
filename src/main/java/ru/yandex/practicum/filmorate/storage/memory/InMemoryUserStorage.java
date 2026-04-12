package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    private final AtomicInteger idCounter = new AtomicInteger(0);
    private final InMemoryUserRelationsStorage relationsStorage;

    @Autowired
    public InMemoryUserStorage (InMemoryUserRelationsStorage relationsStorage) {
        this.relationsStorage = relationsStorage;
    }

    @Override
    public Collection<User> getUsers() {
        if (users.isEmpty()) {
            throw new NotFoundException("Users not found");
        }
        return users.values().stream().sorted(Comparator.comparing(User::getId)).toList();
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User " + id + " not found");
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        validateUser(user);
        return prepareUser(user);
    }

    @Override
    public User putUser(User newUser) {
        validateUser(newUser);
        if (users.containsKey(newUser.getId())) {
            userUpdater(users.get(newUser.getId()), newUser.getName(),
                    newUser.getLogin(), newUser.getBirthday());
            log.info("User {} inserted", newUser.getLogin());
        } else {
            log.error("User not found");
            throw new NotFoundException("User " + newUser.getId() + " not found");
        }
        return newUser;
    }

    @Override
    public void removeUser(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User " + id + " not found");
        }
        users.remove(id);
    }

    @Override
    public Collection<User> getFriends(Integer id) {
        getUserById(id);
        return relationsStorage.getFriendIdsForUser(id).stream()
                .map(this::getUserById)
                .toList();
    }

    @Override
    public Collection<User> getMutualFriends(Integer friendId, Integer userId) {
        getUserById(friendId);
        getUserById(userId);
        Set<Integer> friendsOfUser = new HashSet<>(relationsStorage.getFriendIdsForUser(userId));
        return relationsStorage.getFriendIdsForUser(friendId).stream()
                .filter(friendsOfUser::contains)
                .map(this::getUserById)
                .toList();
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

    private User prepareUser(User user) {
        if (user.getId() == null) {
            int count = idCounter.incrementAndGet();
            user.setId(count);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User id:{}, login: {} created",user.getId(), user.getLogin());
        return users.get(user.getId());
    }

    private void userUpdater(User user, String newName,
                             String newLogin, LocalDate newBirthday) {
        if (newName != null && !newName.isBlank()) {
            user.setName(newName);
        }
        if (newLogin != null && !newLogin.isBlank()) {
            user.setLogin(newLogin);
        }
        if (newBirthday != null) {
            user.setBirthday(newBirthday);
        }
    }

}
