package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service // Регистрируем класс как Spring Bean
public class FilmService {
    private final FilmStorage filmStorage; //  Зависимость от интерфейса
    private final UserStorage userStorage; //  Зависимость от интерфейса

    @Autowired     // Используем Dependency Injection
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        log.debug("Вызван метод FilmService.create(). Получен объект класса Film {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.debug("Вызван FilmService.update(). Получен объект класса Film {}", film);
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        log.debug("Вызван метод FilmService.getAll()");
        return filmStorage.getAll();
    }

    public Film getFilmById(long id) {
        log.debug("Вызван метод FilmService.getFilmById()");
        Optional<Film> filmOptional = filmStorage.getFilmById(id);
        return filmOptional.orElseThrow(() -> new DataNotFoundException("Фильм с таким id не найден."));
    }

    public void addLike(long id, long userId) {
        log.debug("Вызван метод FilmService.addLike()");
        if (userStorage.userNotExists(userId)) {
            log.debug("Пользователь с id {}  не найден", userId);
            throw new DataNotFoundException("Пользователь c таким id не найден.");
        }
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(long id, long userId) {
        log.debug("Вызван метод FilmService. deleteLike()");
        if (userStorage.userNotExists(userId)) {
            log.debug("Пользователь с id {}  не найден", userId);
            throw new DataNotFoundException("Пользователь c таким id не найден.");
        }
        filmStorage.deleteLike(id);
    }

    public List<Film> mostPopularFilms(int count) {
        log.debug("Вызван метод FilmService.mostPopularFilms().  count = {}", count);
        return filmStorage.mostPopularFilms(count);
    }
}