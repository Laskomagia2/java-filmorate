package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.dao.mapper.RatingMapper;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component ("FilmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    public FilmDbStorage(JdbcTemplate jdbc, @Qualifier ("FilmMapper") RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    private final String findAllQuery = "SELECT f.id, f.name AS film_name, f.description, f.realise_date, f.duration, " +
    "fr.id AS rating_id, fr.name AS rating_name, " +
    "COUNT(l.user_id) AS likes_count " +
    "FROM films AS f " +
    "LEFT JOIN film_ratings AS fr ON f.rating_id = fr.id " +
    "LEFT JOIN users_liked_film AS l ON f.id = l.film_id " +
    "GROUP BY f.id, fr.id";
    private final String findByIdQuery = "SELECT f.id, f.name AS film_name, f.description, f.realise_date, f.duration, " +
    "fr.id AS rating_id, fr.name AS rating_name, " +
    "COUNT(l.user_id) AS likes_count " +
    "FROM films AS f " +
    "LEFT JOIN film_ratings AS fr ON f.rating_id = fr.id " +
    "LEFT JOIN users_liked_film AS l ON f.id = l.film_id " +
    "WHERE f.id = ? " +
    "GROUP BY f.id, fr.id";
    private final String insertFilmQuery = "INSERT INTO films(name, description, realise_date, duration, rating_id) " +
    "VALUES (?, ?, ?, ?, ?)";
    private final String updateFilmQuery = "UPDATE films SET name = ?, description = ?, realise_date = ?, duration = ?, " +
    "rating_id = ? WHERE id = ?";
    private final String deleteFilmQuery = "DELETE FROM films WHERE id = ?";
    private final String likeFilmQuery = "INSERT INTO users_liked_film(film_id, user_id) VALUES (?, ?)";
    private final String findUserLikedFilmQuery = "SELECT user_id FROM users_liked_film WHERE film_id = ?";
    private final String deleteLikeQuery = "DELETE FROM users_liked_film WHERE film_id = ? AND user_id = ?";
    private final String findGenresQuery = "SELECT id, name FROM film_genres";
    private final String findRatingQuery = "SELECT id, name FROM film_ratings";
    private static final String existsGenreQuery = "SELECT COUNT(*) FROM film_genres WHERE id = ?";
    private static final String existsRatingQuery = "SELECT COUNT(*) FROM film_ratings WHERE id = ?";
    private static final String findAllLinkedQuery = "SELECT f.id, f.name AS film_name, f.description, f.realise_date, " +
    "f.duration, fr.id AS rating_id, fr.name AS rating_name, " +
    "COALESCE(l.likes_count, 0) AS likes_count " +
    "FROM films f " +
    "LEFT JOIN film_ratings fr ON f.rating_id = fr.id " +
    "LEFT JOIN (" +
    "SELECT film_id, COUNT(user_id) AS likes_count " +
    "FROM users_liked_film " +
    "GROUP BY film_id " +
    ") l ON f.id = l.film_id " +
    "ORDER BY likes_count DESC, f.id ASC";

    @Override
    public Collection<Film> getFilms(Integer size, Integer from, String sort) {
        List<Film> films = findMany(findAllQuery).stream().toList();
        setGenresForFilms(films);
        if (size < 0) {
            throw new ParameterNotValidException("size", "size must not be less than 0");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "from must not be less than 0");
        }
        if (sort.equals("asc")) {
            List<Film> sortedFilms = films.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate)).toList();
            return sortedFilms.stream().skip(from).limit(size).toList();
        } else if (sort.equals("desc")) {
            List<Film> sortedFilms = films.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate).reversed()).toList();
            return sortedFilms.stream().skip(from).limit(size).toList();
        } else if (sort.equals("like")) {
            List<Film> popularFilms = findMany(findAllLinkedQuery)
                    .stream()
                    .skip(from)
                    .limit(size)
                    .toList();
            setGenresForFilms(popularFilms);
            return popularFilms;
        } else {
            return films.stream().skip(from).limit(size).toList();
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = findOne(findByIdQuery, id)
                .orElseThrow(() -> new NotFoundException("Film " + id + " not found"));
        setGenresForFilms(List.of(film));
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        validateGenreAndRating(film);

        int id = insert(
                insertFilmQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );

        film.setId(id);
        updateFilmGenres(film);

        film.setGenres(new ArrayList<>());
        setGenresForFilms(List.of(film));

        return film;
    }

    @Override
    public Film putFilm(Film film) {
        validateFilm(film);
        validateGenreAndRating(film);

        update(
                updateFilmQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        updateFilmGenres(film);

        film.setGenres(new ArrayList<>());
        setGenresForFilms(List.of(film));

        return film;
    }

    @Override
    public void removeFilm(Integer id) {
        delete(deleteFilmQuery, id);
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        try {
            int rowsAffected = jdbc.update(likeFilmQuery, filmId, userId);

            if (rowsAffected == 0) {
                throw new ValidationException("Like error");
            }
            log.info("User {} like film {}", userId, filmId);

        } catch (DataIntegrityViolationException ex) {
            throw new ValidationException("Ошибка при добавлении лайка: " + ex.getMessage());
        }
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        try {
            boolean res = deleteWithTwoKeys(deleteLikeQuery, filmId, userId);
            if (!res) {
                throw new NotFoundException("Like not found");
            }
        } catch (DataIntegrityViolationException ex) {
            throw new ValidationException("User " + userId + " are not liked film " + filmId);
        }
    }

    @Override
    public Collection<Integer> getUserLikedFilm(Integer filmId) {
       return jdbc.queryForList(findUserLikedFilmQuery, Integer.class, filmId);
    }

    @Override
    public Collection<GenreDto> getGenres() {
       return jdbc.query(findGenresQuery, new GenreMapper());
    }

    @Override
    public Collection<RatingDto> getRatings() {
        return jdbc.query(findRatingQuery, new RatingMapper());
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

    private void validateGenreAndRating(Film film) {
        // Проверка жанров
        if (film.getGenres() != null) {
            for (GenreDto genre : film.getGenres()) {
                Integer count = jdbc.queryForObject(
                        existsGenreQuery,
                        Integer.class,
                        genre.getId()
                );
                if (count == null || count == 0) {
                    throw new NotFoundException(
                            "Genre with id=" + genre.getId() + " not found"
                    );
                }
            }
        }

        // Проверка рейтинга MPA
        if (film.getMpa() != null) {
            Integer count = jdbc.queryForObject(
                    existsRatingQuery,
                    Integer.class,
                    film.getMpa().getId()
            );
            if (count == null || count == 0) {
                throw new NotFoundException(
                        "MPA rating with id=" + film.getMpa().getId() + " not found"
                );
            }
        }
    }

    private void updateFilmGenres(Film film) {
        jdbc.update("DELETE FROM film_genre_binding WHERE film_id = ?", film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        List<Integer> genreIds = film.getGenres().stream()
                .map(GenreDto::getId)
                .distinct()
                .toList();

        for (Integer genreId : genreIds) {
            jdbc.update("INSERT INTO film_genre_binding(film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genreId);
        }
    }

    private void setGenresForFilms(Collection<Film> films) {
        if (films.isEmpty()) return;

        List<Integer> ids = films.stream().map(Film::getId).toList();

        String sql = "SELECT b.film_id, g.id AS genre_id, g.name AS genre_name " +
                "FROM film_genre_binding b " +
                "JOIN film_genres g ON b.genre_id = g.id " +
                "WHERE b.film_id IN (" + String.join(",", Collections.nCopies(ids.size(), "?")) + ") " +
                "ORDER BY g.id";

        jdbc.query(sql, (rs) -> {
            int filmId = rs.getInt("film_id");
            GenreDto genre = new GenreDto(rs.getInt("genre_id"), rs.getString("genre_name"));

            films.stream()
                    .filter(f -> f.getId() == filmId)
                    .findFirst()
                    .ifPresent(f -> {
                        if (!f.getGenres().contains(genre)) {
                            f.getGenres().add(genre);
                        }
                    });
        }, ids.toArray());
    }

}
