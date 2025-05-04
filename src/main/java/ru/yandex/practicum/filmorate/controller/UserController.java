package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.dto.Resp;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) { // Используем Dependency Injection
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Validated({User.UserCreate.class, Default.class})
                                           @RequestBody User user) {
        log.debug("Вызван UserController.createUser(). Получен объект {}", user);
        User readyUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED)   // код ответа 201 CREATED
                .body(readyUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Validated({User.UserUpdate.class, Default.class})
                                           @RequestBody User user) {
        log.debug("Вызван UserController.updateUser(). Начато обновление. Получен объект {}", user);
        User readyUser = userService.update(user);
        return ResponseEntity.status(HttpStatus.OK)   // код ответа 200 OK
                .body(readyUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("Вызван метод UserController.getAllUsers() для получения списка пользователей");
        List<User> result = userService.getAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/{id}")
    ResponseEntity<User> getUser(@PathVariable long id) {
        log.debug("Вызван метод UserController.getUser(). Получен id {}", id);
        User user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    ResponseEntity<Resp> addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Вызван метод UserController.addFriend(). Id {}, Id друга {}", id, friendId);
        userService.addToFriends(id, friendId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Resp("Выполнено", "Добавление в друзья прошло успешно"));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    ResponseEntity<Resp> removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Вызван метод UserController.removeFriend(). Id {}, Id друга {}", id, friendId);
        userService.removeFromFriends(id, friendId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Resp("Выполнено", "Пользователь c id " + friendId + " удален из друзей"));
    }

    @GetMapping("/{id}/friends")
    ResponseEntity<List<User>> getFriendsUser(@PathVariable long id) {
        log.debug("Вызван метод UserController.getFriendsUser(). Id пользователя {}", id);
        List<User> friends = userService.displayFriends(id);
        return ResponseEntity.ok(friends);  // код ответа 200 OK
    }

    //Возвращает список всех, общих с другим пользователем, друзей
    @GetMapping("/{id}/friends/common/{otherId}")
    ResponseEntity<List<User>> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Вызван UserController.getCommonFriends(). Id пользователя {}. Другого пользователя {}", id, otherId);
        List<User> commonFriends = userService.displayCommonFriends(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }
}