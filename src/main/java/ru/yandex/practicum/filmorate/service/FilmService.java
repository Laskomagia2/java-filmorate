package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
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

    public Collection<Film> getFilms(Integer size, Integer from, String sort) {
        return filmStorage.getFilms(size,from,sort);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film putFilm(Film film) {
        return filmStorage.putFilm(film);
    }

    public void removeFilm(Integer id) {
        filmStorage.removeFilm(id);
    }

}
