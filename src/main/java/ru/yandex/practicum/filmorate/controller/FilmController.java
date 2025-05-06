package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.Response;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    // Используем Dependency Injection
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.debug("Вызван FilmController.createFilm(). Начато создание фильма. Получен объект {}", film);
        Film readyFilm = filmService.create(film);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readyFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.debug("Вызван FilmController.updateFilm(). Начато обновление. Получен объект {}", film);
        Film readyFilm = filmService.update(film);
        return ResponseEntity.status(HttpStatus.OK)
                .body(readyFilm);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.debug("Вызван метод FilmController.getAllFilms() для получения списка фильмов");
        List<Film> allFilms = filmService.getAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(allFilms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable @Positive(message = "Id должен быть больше 0") long id) {
        log.debug("Вызван метод FilmController.getFilm(). Id фильма {}", id);
        Film readyFilm = filmService.getFilmById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(readyFilm);
    }

    // Пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Response> like(@PathVariable @Positive(message = "Id должен быть больше 0") long id,
                                         @PathVariable @Positive(message = "Id должен быть больше 0") long userId) {
        log.debug("Вызван метод FilmController.like(). Id фильма {}, id пользователя {}", id, userId);
        filmService.addLike(id, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response("Выполнено", "Фильму c id " + id + " добавлен один лайк"));
    }

    // Пользователь удаляет лайк
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Response> removeLike(@PathVariable @Positive(message = "Id должен быть больше 0") long id,
                                               @PathVariable @Positive(message = "Id должен быть больше 0") long userId) {
        log.debug("Вызван метод FilmController.removeLike(). Id фильма {}, id пользователя {}", id, userId);
        filmService.deleteLike(id, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response("Выполнено", "Ваш лайк фильму c id " + id + " удален"));
    }

    // Возвращает count самых популярных фильмов (по количеству лайков).
    // Если параметр запроса count не установлен, выводит 10 фильмов.
    @GetMapping("/popular")
    public ResponseEntity<List<Film>> showMostPopularFilms(@RequestParam(defaultValue = "10")
                                                           @Positive(message = "Должен быть count > 0") int count) {
        log.debug("Вызван UserController.showMostPopularFilms(). count = {}", count);
        List<Film> popularFilms = filmService.mostPopularFilms(count);
        return ResponseEntity.status(HttpStatus.OK)
                .body(popularFilms);
    }
}