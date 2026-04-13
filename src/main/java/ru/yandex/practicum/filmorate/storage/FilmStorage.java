package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    public Collection<Film> getFilms(Integer size, Integer from, String sort);

    public Film getFilmById(Integer id);

    public Film addFilm(Film film);

    public Film putFilm(Film newFilm);

    public void removeFilm(Integer id);

    public void likeFilm(Integer filmId, Integer userId);

    public Collection<Integer> getUserLikedFilm(Integer filmId);

    public void deleteLike(Integer filmId, Integer userId);

    public Collection<GenreDto> getGenres();

    public Optional<GenreDto> getGenreById(Integer id);

    public Collection<RatingDto> getRatings();

    public Optional<RatingDto> getRatingById(Integer id);
}
