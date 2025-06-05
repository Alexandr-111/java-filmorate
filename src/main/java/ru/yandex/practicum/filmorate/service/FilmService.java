package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(@Valid Film film) {
        // Проверяем дубликаты перед созданием
        log.debug("Вызван метод FilmService.create(). Получен объект класса Film {}", film);
        return filmStorage.create(film);
    }

    public Film update(@Valid Film film) {
        log.debug("Вызван FilmService.update(). Получен объект класса Film {}", film);
        // Проверяем существование фильма
        if (filmStorage.filmNotExists(film.getId())) {
            throw new DataNotFoundException("Фильм с id " + film.getId() + " не найден в БД");
        }
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
            log.debug("Пользователь с id {} не найден", userId);
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
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> mostPopularFilms(int count) {
        log.debug("Вызван метод FilmService.mostPopularFilms().  count = {}", count);
        return filmStorage.mostPopularFilms(count);
    }
}