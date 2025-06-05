package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public List<Genre> getAll() {
        log.debug("Вызван метод GenreService.getAll()");
        return genreDbStorage.getAll();
    }

    public Genre getGenreById(long id) {
        log.debug("Вызван метод GenreService.getGenreById()");
        return genreDbStorage.getGenreById(id);
    }
}