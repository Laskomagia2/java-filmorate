package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/films")
public class FilmController {

    private final FilmService filmService;


    @GetMapping
    public Collection<Film> getFilms(@RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "default") String sort) {
        return filmService.getFilms(size,from,sort);
    }

    //GET /films/popular?count={count}
    @GetMapping ("/popular")
    public Collection<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getMostLikedFilms(count);
    }

    @GetMapping ("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @ResponseStatus (HttpStatus.CREATED)
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    //PUT /films/{id}/like/{userId}
    @PutMapping ("/{id}/like/{userId}")
    public void likeFilm(@PathVariable(value = "id") Integer id,
                         @PathVariable(value = "userId") Integer userId) {
        filmService.likeFilm(id, userId);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        return filmService.putFilm(film);
    }

    //DELETE /films/{id}/like/{userId}
    @DeleteMapping ("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Integer id,
                           @PathVariable(value = "userId") Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping ("/{id}")
    public void deleteFilm(@PathVariable Integer id) {
        filmService.removeFilm(id);
    }

}
