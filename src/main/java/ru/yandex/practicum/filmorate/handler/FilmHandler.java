package ru.yandex.practicum.filmorate.handler;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FilmHandler {
    private final Map<Integer, Film> storageFilms = new HashMap<>();
    private int generatedId = 0;

    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен объект класса Film {}", film);
        int id = ++generatedId;
        film.setId(id);
        storageFilms.put(id, film);
        return film;
    }

    public Film update(@Valid @RequestBody Film film) {
        int id = film.getId();
        if (!storageFilms.containsKey(id)) {
            log.debug("Фильм с этим Id не найден в storageFilms, обновление невозможно");
            throw new DataNotFoundException("Фильм с таким Id не найден");
        } else {
            log.debug("Id найден в storageFilms, начато обновление");
            storageFilms.put(id, film);
            return film;
        }
    }

    public List<Film> getAll() {
        log.debug("Вызван метод FilmHandler.getAll()");
        return new ArrayList<>(storageFilms.values());
    }
}