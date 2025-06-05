package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public List<Mpa> getAll() {
        log.debug("Вызван метод MpaService.getAll()");
        return mpaDbStorage.getAll();
    }

    public Mpa getMpaById(int id) {
        log.debug("Вызван метод MpaService.getMpaById()");
        return mpaDbStorage.getById(id);
    }
}