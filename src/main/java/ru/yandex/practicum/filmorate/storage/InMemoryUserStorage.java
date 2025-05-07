package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component // Регистрируем класс как Spring Bean
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storageUsers = new HashMap<>();
    private long id = 0;

    public boolean userNotExists(long id) {
        return !(storageUsers.containsKey(id));
    }

    public User create(User user) {
        long userId = ++id;
        user.setId(userId);
        storageUsers.put(userId, user);
        return user;
    }

    public User update(User user) {
        long id = user.getId();
        if (userNotExists(id)) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        } else {
            User existingUser = storageUsers.get(id);

            // Присваиваем значения полей  user существующему объекту existingUser,
            // если помещать сам user в хранилище утратятся значения хранящиеся в сете allFriends
            existingUser.setLogin(user.getLogin());
            if ((user.getEmail() != null) && (!user.getName().isEmpty())) {
                existingUser.setEmail(user.getEmail());
            }
            if ((user.getName() != null) && (!user.getName().isEmpty())) {
                existingUser.setName(user.getName());
            }
            if (user.getBirthday() != null) {
                existingUser.setBirthday(user.getBirthday());
            }
            return existingUser;
        }
    }

    public List<User> getAll() {
        return new ArrayList<>(storageUsers.values());
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(storageUsers.get(id));
    }

    public void addToFriends(long id, long friendId) {
        if (userNotExists(id)) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
        if (userNotExists(friendId)) {
            throw new DataNotFoundException("Кандидат в друзья с таким id не найден");
        }
        User user = storageUsers.get(id);
        User friend = storageUsers.get(friendId);
        user.getAllFriends().add(friendId); // Добавляем друга в друзья к пользователю
        friend.getAllFriends().add(id); // Добавляем пользователя в друзья к другу
    }

    public void removeFromFriends(long id, long friendId) {
        if (userNotExists(id)) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
        if (userNotExists(friendId)) {
            throw new DataNotFoundException("Друг с таким id не найден");
        }
        User user = storageUsers.get(id);
        user.getAllFriends().remove(friendId); // Удаляем друга из друзей пользователя
        User friend = storageUsers.get(friendId);
        friend.getAllFriends().remove(id);    // Удаляем пользователя из друзей друга
    }

    public List<User> displayFriends(long id) {    //Возвращает список всех друзей
        if (userNotExists(id)) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
        User user = storageUsers.get(id);
        return user.getAllFriends().stream()
                .map(storageUsers::get)
                .toList();
    }

    //Возвращает список всех, общих с другим пользователем, друзей
    public List<User> displayCommonFriends(long id, long otherId) {
        if (userNotExists(id)) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
        if (userNotExists(otherId)) {
            throw new DataNotFoundException("Другой пользователь с таким id не найден");
        }
        User user1 = storageUsers.get(id);
        User user2 = storageUsers.get(otherId);
        Set<Long> friends1 = user1.getAllFriends();
        Set<Long> friends2 = user2.getAllFriends();
        Set<Long> commonElements = new HashSet<>(friends1); //Создаем копию, чтобы не изменять оригинальный сет
        commonElements.retainAll(friends2);  //Получаем только общие элементы в commonElements
        return commonElements.stream()
                .map(storageUsers::get)
                .toList();
    }
}