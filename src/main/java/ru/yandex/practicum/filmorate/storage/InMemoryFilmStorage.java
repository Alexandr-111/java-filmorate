package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storageFilms = new HashMap<>();
    private long id = 0;

    public Film create(Film film) {
        long filmId = ++id;
        film.setId(filmId);
        storageFilms.put(filmId, film);
        return film;
    }

    public Film update(Film film) {
        long id = film.getId();
        if (!storageFilms.containsKey(id)) {
            throw new DataNotFoundException("Фильм с таким id не найден");
        } else {
            Film existingFilm = storageFilms.get(id);

            // Присваиваем значения полей  film существующему объекту existingFilm,
            // если помещать сам film в хранилище утратятся значения хранящиеся в сете likesFilm
            existingFilm.setName(film.getName());
            if ((film.getDescription() != null) && (!film.getDescription().isEmpty())) {
                existingFilm.setDescription(film.getDescription());
            }
            if (film.getReleaseDate() != null) {
                existingFilm.setReleaseDate(film.getReleaseDate());
            }
            existingFilm.setDuration(film.getDuration());
            return existingFilm;
        }
    }

    public List<Film> getAll() {
        return new ArrayList<>(storageFilms.values());
    }

    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(storageFilms.get(id));
    }

    public void addLike(long id, long userId) {
        if (!storageFilms.containsKey(id)) {
            throw new DataNotFoundException("Фильм с id " + id + " не найден");
        }
        Film film = storageFilms.get(id);
        film.getLikesFilm().add(userId);
    }

    public void deleteLike(long id) {
        if (!storageFilms.containsKey(id)) {
            throw new DataNotFoundException("Фильм с id " + id + " не найден");
        }
        Film film = storageFilms.get(id);
        film.getLikesFilm().remove(id);
    }

    public List<Film> mostPopularFilms(int count) {
        // Используем Comparator.comparingInt, так как Set.size() возвращает тип int
        Comparator<Film> comparator = Comparator.comparingInt(film -> film.getLikesFilm().size());
        return storageFilms.values().stream()
                .sorted(comparator.reversed())  // Сортируем по убыванию количества лайков
                .limit(count)
                .toList();
    }
}