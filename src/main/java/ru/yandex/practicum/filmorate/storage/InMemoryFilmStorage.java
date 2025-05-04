package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storageFilms = new HashMap<>();
    private long generatedId = 0;

    public Film create(Film film) {
        log.debug("Вызван метод InMemoryFilmStorage.create(). Получен объект класса Film {}", film);
        long id = ++generatedId;
        film.setId(id);
        storageFilms.put(id, film);
        return film;
    }

    public Film update(Film film) {
        long id = film.getId();
        if (!storageFilms.containsKey(id)) {
            log.debug("Вызван InMemoryFilmStorage.update(). Фильм с таким id не найден, обновление невозможно");
            throw new DataNotFoundException("Фильм с таким id не найден");
        } else {
            log.debug("Вызван InMemoryFilmStorage.update(). Id найден в storageFilms, начато обновление");
            storageFilms.put(id, film);
            return film;
        }
    }

    public List<Film> getAll() {
        log.debug("Вызван метод InMemoryFilmStorage.getAll()");
        return new ArrayList<>(storageFilms.values());
    }

    public Optional<Film> getFilmById(long id) {
        log.debug("Вызван метод InMemoryFilmStorage.getFilmById()");
        return Optional.ofNullable(storageFilms.get(id));
    }

    public void addLike(long id, long userId) {
        log.debug("Вызван метод InMemoryFilmStorage.addLike()");
        if (!storageFilms.containsKey(id)) {
            log.debug("Фильм с id {}  не найден", id);
            throw new DataNotFoundException("Фильм с id " + id + " не найден");
        }
        Film film = storageFilms.get(id);
        film.getLikesFilm().add(userId);
    }

    public void deleteLike(long id) {
        log.debug("Вызван метод InMemoryFilmStorage.deleteLike()");
        if (!storageFilms.containsKey(id)) {
            log.debug("Фильм с id {}  не найден", id);
            throw new DataNotFoundException("Фильм с id " + id + " не найден");
        }
        Film film = storageFilms.get(id);
        film.getLikesFilm().remove(id);
    }

    public List<Film> mostPopularFilms(int count) {
        log.debug("Вызван метод InMemoryFilmStorage.mostPopularFilms().  count = {}", count);
        // Используем Comparator.comparingInt, так как Set.size() возвращает тип int
        Comparator<Film> comparator = Comparator.comparingInt(film -> film.getLikesFilm().size());
        return storageFilms.values().stream()
                .sorted(comparator.reversed())  // Сортируем по убыванию количества лайков
                .limit(count)                   // Ограничиваем количество
                .toList();                      // Собираем в список
    }
}