package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<GenreDto> genres;
    private RatingDto mpa;
    private Integer likeCount;
}
