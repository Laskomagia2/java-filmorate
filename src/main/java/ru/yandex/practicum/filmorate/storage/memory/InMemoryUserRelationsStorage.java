package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.UserRelations;
import ru.yandex.practicum.filmorate.storage.RelationStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryUserRelationsStorage implements RelationStorage {

    private final Set<UserRelations> relations = new HashSet<>();

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("User cannot add themselves as a friend");
        }
        Integer firstId = Math.min(userId, friendId);
        Integer secondId = Math.max(userId, friendId);
        validateRelation(firstId, secondId);
        UserRelations relation = new UserRelations(firstId, secondId, userId);
        relation.setRelationStatus(FriendshipStatus.UNCONFIRMED);
        relations.add(relation);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        Integer firstId = Math.min(userId, friendId);
        Integer secondId = Math.max(userId, friendId);
        Optional<UserRelations> result = relations.stream()
                .filter(relation -> Objects.equals(relation.getUserId(), firstId)
                        && Objects.equals(relation.getAnotherUserId(), secondId))
                .findFirst();
        if (result.isPresent()) {
            relations.remove(result.get());
        } else {
            throw new NotFoundException("Relation between user " + userId + " and user " + friendId + " not found");
        }
    }

    public List<Integer> getFriendIdsForUser(Integer userId) {
        return relations.stream()
                .filter(r -> Objects.equals(r.getUserId(), userId)
                        || Objects.equals(r.getAnotherUserId(), userId))
                .map(r -> Objects.equals(r.getUserId(), userId) ? r.getAnotherUserId() : r.getUserId())
                .collect(Collectors.toList());
    }

    private void validateRelation(Integer firstId, Integer secondId) {
        if (relations.stream().anyMatch(relation -> Objects.equals(relation.getUserId(), firstId)
                && Objects.equals(relation.getAnotherUserId(), secondId))) {
            throw new ValidationException("relation is already exist");
        }
    }

}
