package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    private final AtomicInteger idCounter = new AtomicInteger(0);

    @GetMapping
    public List<User> getUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateUser(user);
        log.info("User {} created", user.getLogin());
        return prepareUser(user);
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        validateUser(user);
        if (users.containsKey(user.getId())) {
            userUpdater(users.get(user.getId()), user.getName(),
                    user.getLogin(), user.getBirthday());
            log.info("User {} inserted", user.getLogin());
        } else {
            log.error("User not found");
            throw new NotFoundException("User "+ user.getId() + " not found");
        }
        return user;
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
