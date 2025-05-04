package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    boolean userNotExists(long id);

    User create(User user);

    User update(User user);

    List<User> getAll();

    Optional<User> getUserById(long id);

    void addToFriends(long id, long friendId);

    void removeFromFriends(long id, long friendId);

    List<User> displayFriends(long id);

    List<User> displayCommonFriends(long id, long otherId);
}