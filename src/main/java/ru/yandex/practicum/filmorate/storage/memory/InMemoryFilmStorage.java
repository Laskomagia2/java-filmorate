package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genere;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer,Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> filmLikes = new HashMap<>();

    private final AtomicInteger idCounter = new AtomicInteger(0);

    public Collection<Film> getFilms(Integer size, Integer from, String sort) {
        if (films.isEmpty()) {
            throw new NotFoundException("Films not found");
        }
        if (size < 0) {
            throw new ParameterNotValidException("size", "size must not be less than 0");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "from must not be less than 0");
        }
        if (sort.equals("asc")) {
            List<Film> sortedFilms = films.values().stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate)).toList();
            return sortedFilms.stream().skip(from).limit(size).toList();
        } else if (sort.equals("desc")) {
            List<Film> sortedFilms = films.values().stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate).reversed()).toList();
            return sortedFilms.stream().skip(from).limit(size).toList();
        } else if (sort.equals("like")) {
            List<Film> sortedFilms = films.values().stream()
                    .sorted(Comparator.comparing(this::getLikesAmount).reversed()).toList();
            return sortedFilms.stream().limit(size).toList();
        } else {
            return films.values().stream().skip(from).limit(size).toList();
        }
    }

    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film " + id + " not found");
        }
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        log.info("Film {} added", film.getName());
        return prepareFilm(film);
    }

    @Override
    public Film putFilm(Film newFilm) {
        validateFilm(newFilm);
        if (films.containsKey(newFilm.getId())) {
            filmUpdater(films.get(newFilm.getId()), newFilm.getName(),
                    newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration());
        } else {
            log.error("Film not found");
            throw new NotFoundException("Film " + newFilm.getId() + " not found");
        }
        log.info("Film {} inserted", newFilm.getName());
        return films.get(newFilm.getId());
    }

    @Override
    public void removeFilm(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film " + id + " not found");
        }
        films.remove(id);
        filmLikes.remove(id);
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        getFilmById(filmId);
        Set<Integer> likes = filmLikes.computeIfAbsent(filmId, key -> new HashSet<>());
        if (!likes.add(userId)) {
            throw new ValidationException("User " + userId + " is already like film " + filmId);
        }
    }

    @Override
    public Collection<Integer> getUserLikedFilm(Integer filmId) {
        getFilmById(filmId);
        return new HashSet<>(filmLikes.getOrDefault(filmId, Collections.emptySet()));
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        getFilmById(filmId);
        Set<Integer> likes = filmLikes.getOrDefault(filmId, Collections.emptySet());
        likes.remove(userId);
    }

    @Override
    public Collection<GenreDto> getGenres() {
        Collection<GenreDto> result = new ArrayList<>();
        List<String> genres = Arrays.stream(Genere.values()).map(Enum::toString).toList();
        for (int idx = 0; idx < genres.size(); idx++) {
            result.add(new GenreDto(idx + 1, genres.get(idx)));
        }
        return result;
    }

    @Override
    public Collection<RatingDto> getRatings() {
        Collection<RatingDto> result = new ArrayList<>();
        List<String> genres = Arrays.stream(RatingMpa.values()).map(Enum::toString).toList();
        for (int idx = 0; idx < genres.size(); idx++) {
            result.add(new RatingDto(idx + 1, genres.get(idx)));
        }
        return result;
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
        filmLikes.putIfAbsent(film.getId(), new HashSet<>());
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

    private int getLikesAmount(Film film) {
        return filmLikes.getOrDefault(film.getId(), Collections.emptySet()).size();
    }
}
