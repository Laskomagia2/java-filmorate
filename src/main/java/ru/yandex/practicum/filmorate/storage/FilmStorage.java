package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> getFilms(Integer size, Integer from, String sort);

    public Film getFilmById(Integer id);

    public Film addFilm(Film film);

    public Film putFilm(Film newFilm);

    public void removeFilm(Integer id);
}
