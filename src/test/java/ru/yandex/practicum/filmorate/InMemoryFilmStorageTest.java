package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage(); // Создаем новый экземпляр перед каждым тестом
    }

    @Test
    void testCreateAndGetFilm() {

        Film film = Film.builder()
                .id(0L)
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2023, 10, 20))
                .duration(120L)
                .likesFilm(new HashSet<>())
                .build();

        Film createdFilm = filmStorage.create(film);

        assertNotNull(createdFilm, "Созданный фильм не должен быть null");
        assertNotEquals(0L, createdFilm.getId(), "Id  фильма должен быть сгенерирован при создании");
        assertTrue(createdFilm.getId() > 0, "Id фильма должен быть больше 0");
        assertEquals(film.getName(), createdFilm.getName(), "Название фильма должно совпадать");
        assertEquals(film.getDescription(), createdFilm.getDescription(), "Описание фильма должно совпадать");
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate(), "Дата релиза должна совпадать");
        assertEquals(film.getDuration(), createdFilm.getDuration(), "Продолжительность должна совпадать");

        // Проверим помещен ли созданный объект в хранилище, получим его по id
        Optional<Film> optionalFilm = filmStorage.getFilmById(createdFilm.getId());
        Film retrievedFilm = optionalFilm.get();
        assertNotNull(retrievedFilm, "Объект должен быть помещен в хранилище после создания");
        assertEquals(createdFilm, retrievedFilm, "Объект в хранилище должен совпадать с созданным объектом");
        assertEquals("Тестовый фильм", retrievedFilm.getName(), "Названия фильмов должны совпадать");
        assertEquals(120L, retrievedFilm.getDuration(), "Продолжительность должна совпадать");
    }

    @Test
    void testUpdateExistingFilm() {

        Film film = Film.builder()
                .id(1L)
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 2, 20))
                .duration(180L)
                .build();
        filmStorage.create(film);
        Film updatedFilm = Film.builder()
                .id(1L)
                .name("Обновленный фильм")
                .description("Обновленное описание фильма")
                .releaseDate(LocalDate.of(2001, 3, 21))
                .duration(150L)
                .build();

        Film resultFilm = filmStorage.update(updatedFilm);

        assertNotNull(resultFilm, "Фильм после обновления не должен быть null");
        assertEquals(updatedFilm.getId(), resultFilm.getId(), "Id фильмов должны совпадать");
        assertEquals(updatedFilm.getName(), resultFilm.getName(), "Имена фильмов должны совпадать");
        assertEquals(updatedFilm.getDescription(), resultFilm.getDescription(), "Описания должны совпадать");
        assertEquals(updatedFilm.getReleaseDate(), resultFilm.getReleaseDate(), "Даты релиза должны совпадать");
        assertEquals(updatedFilm.getDuration(), resultFilm.getDuration(), "Продолжительность  должна совпадать");

        // Убедимся, что фильм обновился в хранилище
        Optional<Film> optionalFilm = filmStorage.getFilmById(1L);
        Film retrievedFilm = optionalFilm.get();
        assertEquals("Обновленный фильм", retrievedFilm.getName(), "Названия фильмов должны совпадать");
        assertEquals(150L, retrievedFilm.getDuration(), "Продолжительность должна совпадать");

    }

    @Test
    void testUpdateNonExistingFilm() {

        Film nonExistingFilm = Film.builder()
                .id(987) //  Используем id, которого нет
                .name("Несуществующий фильм")
                .description("Описание этого фильма")
                .releaseDate(LocalDate.of(2002, 5, 1))
                .duration(120)
                .build();

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> {
            filmStorage.update(nonExistingFilm);
        });
        assertNotNull(exception, "Метод update должен выбросить исключение DataNotFoundException");
        assertEquals("Фильм с таким id не найден", exception.getMessage(), "Сообщения не совпадают");
    }
}