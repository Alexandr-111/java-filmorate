package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component // Регистрируем класс как Spring Bean
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Long, User> storageUsers = new HashMap<>();
    private long generatedId = 0;

    public boolean userNotExists(long id) {
        return !(storageUsers.containsKey(id));
    }

    public User create(User user) {
        log.debug("Вызван метод InMemoryUserStorage.create(). Получен объект класса User {}", user);
        long id = ++generatedId;
        user.setId(id);
        storageUsers.put(id, user);
        return user;
    }

    public User update(User user) {
        log.debug("Вызван метод InMemoryUserStorage.update(). Получен объект класса User {}", user);
        long id = user.getId();
        if (userNotExists(id)) {
            log.debug("Пользователь с таким id не найден, обновление невозможно");
            throw new DataNotFoundException("Пользователь с таким id не найден");
        } else {
            log.debug("Пользователь с таким id найден, начато обновление");
            storageUsers.put(id, user);
            return user;
        }
    }

    public List<User> getAll() {
        log.debug("Вызван метод InMemoryUserStorage.getAll()");
        return new ArrayList<>(storageUsers.values());
    }

    public Optional<User> getUserById(long id) {
        log.debug("Вызван метод InMemoryUserStorage.getUserById()");
        return Optional.ofNullable(storageUsers.get(id));
    }

    public void addToFriends(long id, long friendId) {
        log.debug("Вызван метод InMemoryUserStorage.addToFriends()");
        if (userNotExists(id)) {
            log.debug("Пользователь с таким id не найден, операция невозможна");
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
        if (userNotExists(friendId)) {
            log.debug("Кандидат в друзья с таким id не найден, операция невозможна");
            throw new DataNotFoundException("Кандидат в друзья с таким id не найден");
        }
        log.debug("Пользователи с такими id успешно найдены");
        User user = storageUsers.get(id);
        User friend = storageUsers.get(friendId);
        log.debug("Id пользователя: {}, Id друга {}", id, friendId);
        boolean userAddedFriend = user.getAllFriends().add(friendId); // Добавляем друга в друзья к пользователю
        log.debug("Значение userAddedFriend {}", userAddedFriend);
        log.debug("Содержание allFriends пользователя: {}", user.getAllFriends());
        boolean friendAddedUser = friend.getAllFriends().add(id); // Добавляем пользователя в друзья к другу
        log.debug("Значение friendAddedUser  {}", friendAddedUser);
        log.debug("Содержание allFriends друга: {}", friend.getAllFriends());
    }

    public void removeFromFriends(long id, long friendId) {
        log.debug("Вызван метод InMemoryUserStorage.removeFromFriends()");
        log.debug("Id пользователя: {}, Id друга удаляемого из друзей {}", id, friendId);
        if (userNotExists(id)) {
            log.debug("Пользователь с таким id не найден, операция невозможна");
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
        if (userNotExists(friendId)) {
            log.debug("Друг с таким id не найден, операция невозможна");
            throw new DataNotFoundException("Друг с таким id не найден");
        }
        User user = storageUsers.get(id);
        user.getAllFriends().remove(friendId); // Удаляем друга из друзей пользователя
        User friend = storageUsers.get(friendId);
        friend.getAllFriends().remove(id);    // Удаляем пользователя из друзей друга
    }

    public List<User> displayFriends(long id) {    //Возвращает список всех друзей
        log.debug("Вызван метод InMemoryUserStorage.displayFriends()");
        if (userNotExists(id)) {
            log.debug("Пользователь с таким id не найден, операция невозможна");
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
        User user = storageUsers.get(id);
        return user.getAllFriends().stream()
                .map(storageUsers::get) // Получаем User по id
                .toList();
    }

    //Возвращает список всех, общих с другим пользователем, друзей
    public List<User> displayCommonFriends(long id, long otherId) {
        log.debug("Вызван метод InMemoryUserStorage.displayCommonFriends()");
        if (userNotExists(id)) {
            log.debug("Пользователь с данным id не найден, операция невозможна");
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
        if (userNotExists(otherId)) {
            log.debug("Другой пользователь с данным id не найден, операция невозможна");
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