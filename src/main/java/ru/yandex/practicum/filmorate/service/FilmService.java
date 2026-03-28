package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    //добавление лайка
    public void likeFilm(Integer userId, Integer filmId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId).getUserLikes().add(userId);
    }

    public void deleteLike(Integer userId, Integer filmId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId).getUserLikes().remove(userId);
    }
    //удаление лайка

    public Collection<Film> getMostLikedFilms(Integer count) {
        return filmStorage.getFilms(count, 0, "like");
    }
    //вывод 10 наиболее залайканных фильмов

}
