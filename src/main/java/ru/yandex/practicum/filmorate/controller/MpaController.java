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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/mpa")
@Validated
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    // Метод для получения списка всех рейтингов
    @GetMapping
    public ResponseEntity<List<Mpa>> getAllMpa() {
        log.debug("Вызван метод MpaController.getAllMpa() для получения списка всех Мра");
        List<Mpa> allMpas = mpaService.getAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(allMpas);
    }

    // Метод для получения рейтинга по его идентификатору
    @GetMapping("/{id}")
    public ResponseEntity<Mpa> getMpa(@PathVariable @Positive(message = "Id должен быть больше 0") int id) {
        log.debug("Вызван метод MpaController.getMpa(). Id рейтинга {}", id);
        Mpa readyMpa = mpaService.getMpaById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(readyMpa);
    }
}