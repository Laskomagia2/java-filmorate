package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
public class FilmAttributeController {
    private final FilmService filmService;

    public FilmAttributeController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping ("/genres")
    public Collection<GenreDto> getGenres() {
        return filmService.getGenres(0);
    }

    @GetMapping ("/genres/{id}")
    public GenreDto getGenreById(@PathVariable Integer id) {
        return filmService.getGenres(id).stream().toList().get(0);
    }

    @GetMapping ("/mpa")
    public Collection<RatingDto> getRatings() {
        return filmService.getRating(0);
    }

    @GetMapping ("/mpa/{id}")
    public RatingDto getRatingById(@PathVariable Integer id) {
        return filmService.getRating(id).stream().toList().get(0);
    }

}
