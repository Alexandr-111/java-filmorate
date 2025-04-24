package ru.yandex.practicum.filmorate.handler;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service  // Регистрируем класс как Spring Bean
public class UserHandler {
    private final Map<Integer, User> storageUsers = new HashMap<>();
    private int generatedId = 0;

    public User create(User user) {
        log.debug("Получен объект класса User {}", user);
        int id = ++generatedId;
        user.setId(id);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Поле name пустое. В качестве имени для отображения используем login: {}", user.getLogin());
        }
        storageUsers.put(id, user);
        return user;
    }

    public User update(User user) {
        int id = user.getId();
        if (!storageUsers.containsKey(id)) {
            log.debug("Id не найден в storageUsers, обновление невозможно");
            throw new DataNotFoundException("Пользователь с таким Id не найден");
        } else {
            log.debug("Id найден в storageUsers,  начато обновление");
            storageUsers.put(id, user);
            return user;
        }
    }

    public List<User> getAll() {
        log.debug("Вызван метод UserHandler.getAll()");
        return new ArrayList<>(storageUsers.values());
    }
}