package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class FilmAttributeController {

    private final FilmService filmService;

    @GetMapping ("/genres")
    public Collection<GenreDto> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping ("/genres/{id}")
    public GenreDto getGenreById(@PathVariable Integer id) {
        return filmService.getGenreById(id);
    }

    @GetMapping ("/mpa")
    public Collection<RatingDto> getRatings() {
        return filmService.getRatings();
    }

    @GetMapping ("/mpa/{id}")
    public RatingDto getRatingById(@PathVariable Integer id) {
        return filmService.getRatingById(id);
    }

}
