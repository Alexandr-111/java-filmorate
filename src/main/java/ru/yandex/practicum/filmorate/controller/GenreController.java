package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    // Метод для получения списка всех жанров
    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        log.debug("Вызван метод GenreController.getAllGenres() для получения списка жанров");
        List<Genre> allGenres = genreService.getAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(allGenres);
    }

    // Метод для получения жанра по ID
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable @Positive(message = "Id должен быть больше 0") long id) {
        log.debug("Вызван метод GenreController.getGenre(). Id жанра {}", id);
        Genre readyGenre = genreService.getGenreById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(readyGenre);
    }
}