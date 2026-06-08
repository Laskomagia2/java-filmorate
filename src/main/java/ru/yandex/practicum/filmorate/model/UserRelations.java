package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRelations {
    @EqualsAndHashCode.Include
    private final Integer userId;
    @EqualsAndHashCode.Include
    private final Integer anotherUserId;
    private final Integer initiatorId;
    private FriendshipStatus relationStatus;
}
