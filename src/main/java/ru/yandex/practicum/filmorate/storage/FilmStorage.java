package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> getAll();

    Optional<Film> getFilmById(long id);

    void addLike(long id, long userId);

    void deleteLike(long id);

    List<Film> mostPopularFilms(int count);
}