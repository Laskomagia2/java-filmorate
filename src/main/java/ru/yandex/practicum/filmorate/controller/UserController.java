package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping ({"/{id}"})
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    //GET /users/{id}/friends
    @GetMapping ("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable(value = "id") Integer id) {
        return userService.getFriends(id);
    }

    //GET /users/{id}/friends/common/{otherId}
    @GetMapping ("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable(value = "id") Integer id,
                          @PathVariable(value = "otherId") Integer otherId) {
        return userService.getMutualFriends(otherId, id);
    }

    @ResponseStatus (HttpStatus.CREATED)
    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    //PUT /users/{id}/friends/{friendId}
    @PutMapping ("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id") Integer id,
                          @PathVariable(value = "friendId") Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        return userService.putUser(user);
    }

    //DELETE /users/{id}/friends/{friendId}
    @DeleteMapping ("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(value = "id") Integer id,
                             @PathVariable(value = "friendId") Integer friendId) {
        userService.removeFriend(id, friendId);
    }

    @DeleteMapping ("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.removeUser(id);
    }

}
