package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service  // Регистрируем класс как Spring Bean
public class UserService {
    private final UserStorage userStorage; //  Зависимость от интерфейса

    @Autowired     // Используем Dependency Injection
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        log.debug("Вызван метод UserService.create(). Получен объект класса User {}", user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Поле name пустое. В качестве имени для отображения используем login: {}", user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        log.debug("Вызван метод UserService.update(). Получен объект класса User {}", user);
        return userStorage.update(user);
    }

    public List<User> getAll() {
        log.debug("Вызван метод UserService.getAll()");
        return userStorage.getAll();
    }

    public User getUserById(long id) {
        log.debug("Вызван метод UserService.getUserById()");
        Optional<User> userOptional = userStorage.getUserById(id);
        return userOptional.orElseThrow(() -> new DataNotFoundException("Пользователь не найден."));
    }

    public void addToFriends(long id, long friendId) {
        log.debug("Вызван метод UserService.addFriend(). Id {}, Id друга {}", id, friendId);
        userStorage.addToFriends(id, friendId);
    }

    public void removeFromFriends(long id, long friendId) {
        log.debug("Вызван метод UserService.removeFromFriends(). Id {}, Id друга {}", id, friendId);
        userStorage.removeFromFriends(id, friendId);
    }

    public List<User> displayFriends(long id) {    //Возвращает список всех друзей
        log.debug("Вызван метод UserService.displayFriends(). Id {}", id);
        return userStorage.displayFriends(id);
    }

    //Возвращает список всех общих друзей
    public List<User> displayCommonFriends(long id, long otherId) {
        log.debug("Вызван метод UserService.displayCommonFriends(). Id {}, другой id {}", id, otherId);
        return userStorage.displayCommonFriends(id, otherId);
    }
}