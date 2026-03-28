package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    @Builder.Default
    private Set<Integer> userLikes = new HashSet<>();

    public Integer getLikesAmount() {
        return userLikes.size();
    }
}
