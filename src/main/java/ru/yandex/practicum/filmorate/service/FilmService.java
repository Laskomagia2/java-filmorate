package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier ("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void likeFilm(Integer filmId, Integer userId) {
        getFilmById(filmId);
        filmStorage.likeFilm(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getMostLikedFilms(Integer count) {
        return filmStorage.getFilms(count, 0, "like");
    }

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

    public Collection<GenreDto> getGenres() {
        return filmStorage.getGenres();
    }

    public GenreDto getGenreById(Integer id) {
        return filmStorage.getGenreById(id).orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found"));
    }

    public Collection<RatingDto> getRatings() {
        return filmStorage.getRatings();
    }

    public RatingDto getRatingById(Integer id) {
        return filmStorage.getRatingById(id).orElseThrow(() -> new NotFoundException("Rating with id " + id + " not found"));
    }

}
