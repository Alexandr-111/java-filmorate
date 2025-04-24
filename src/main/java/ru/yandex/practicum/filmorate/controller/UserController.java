package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.handler.UserHandler;
import ru.yandex.practicum.filmorate.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserHandler userHandler;

    // Используем Dependency Injection
    @Autowired
    public UserController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @PostMapping
    public User createUser(@Validated({User.UserCreate.class, Default.class}) @RequestBody User user) {
        log.debug("Начато создание профиля пользователя. Получен объект {}", user);
        return userHandler.create(user);
    }

    @PutMapping
    public User updateUser(@Validated({User.UserUpdate.class, Default.class}) @RequestBody User user) {
        log.debug("Начато обновление профиля пользователя. Получен объект {}", user);
        return userHandler.update(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("Вызван метод getAllUsers() для получения списка пользователей");
        return userHandler.getAll();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}