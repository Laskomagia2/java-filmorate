package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer,Film> films = new HashMap<>();

    private final AtomicInteger idCounter = new AtomicInteger(0);

    @GetMapping
    public List<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        log.info("Film {} added", film.getName());
        return prepareFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            filmUpdater(films.get(film.getId()), film.getName(),
                    film.getDescription(), film.getReleaseDate(), film.getDuration());
        } else {
            log.error("Film not found");
            throw new NotFoundException("Film " + film.getId() + " not found");
        }
        log.info("Film {} inserted", film.getName());
        return films.get(film.getId());
    }

    private void validateFilm(Film film) {
        if (film.getName().isBlank()) {
            log.error("Validation error: Name must not be empty");
            throw new ValidationException("Name must not be empty");
        }
        if (film.getDescription().length() > 200) {
            log.error("Validation error: Description must be less than 200 chars");
            throw new ValidationException("Description must be less than 200 chars");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.error("Validation error: releaseDate must not be before 28.12.1895");
            throw new ValidationException("releaseDate must not be before 28.12.1895");
        }
        if (film.getDuration() == null || film.getDuration() < 1) {
            log.error("Validation error: Duration must not be less than zero");
            throw new ValidationException("Duration must not be less than zero");
        }
    }

    private Film prepareFilm(Film film) {
        if (film.getId() == null) {
            int count = idCounter.incrementAndGet();
            film.setId(count);
        }
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    private void filmUpdater(Film film, String newName,
                    String newDescription, LocalDate newReleaseDate, Integer newDuration) {
        if (newName != null && !newName.isBlank()) {
            film.setName(newName);
        }
        if (newDescription != null && !newDescription.isBlank()) {
            film.setDescription(newDescription);
        }
        if (newReleaseDate != null) {
            film.setReleaseDate(newReleaseDate);
        }
        if (newDuration != null) {
            film.setDuration(newDuration);
        }
    }
}
