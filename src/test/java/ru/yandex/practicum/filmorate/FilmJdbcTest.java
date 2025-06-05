package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/testschema.sql", "/testdata.sql"})
@Import({FilmDbStorage.class, UserDbStorage.class, MpaDbStorage.class, GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmJdbcTest {
   private final FilmDbStorage filmDbStorage;

    @Test
     void getFilmByIdTest() {
        Optional<Film> filmOpt = filmDbStorage.getFilmById(1);

        assertThat(filmOpt)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(1L);
                    assertThat(film.getName()).isEqualTo("Невероятные приключения");
                    assertThat(film.getDescription()).isEqualTo("Приключенческий фильм о путешествии");
                    assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2010, 7, 12));
                    assertThat(film.getDuration()).isEqualTo(125);

                    // Проверка Mpa
                    assertThat(film.getMpa())
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("id", 2);

                    // Проверка жанров
                    if (film.getGenres() != null) {
                        assertThat(film.getGenres())
                                .hasSize(2)
                                .extracting(Genre::getId)
                                .containsExactlyInAnyOrder(6L, 2L);
                    }
                });
    }

    @Test
     void getFilmByIdNotFound() {
        Optional<Film> filmOptional = filmDbStorage.getFilmById(9999L);
        assertThat(filmOptional).isEmpty();
    }

    @Test
    void createFilmBasicDataTest() {
        // Основные данные фильма
        Film newFilm = new Film();
        newFilm.setName("Основной фильм");
        newFilm.setDescription("Описание данного фильма");
        newFilm.setReleaseDate(LocalDate.now().minusYears(5));
        newFilm.setDuration(90L);
        newFilm.setMpa(new Mpa(1, "G"));

        // Вызов метода
        Film createdFilm = filmDbStorage.create(newFilm);

        // Проверки
        assertThat(createdFilm)
                .satisfies(film -> {
                    assertThat(film.getId()).isPositive();
                    assertThat(film.getName()).isEqualTo("Основной фильм");
                    assertThat(film.getDescription()).isEqualTo("Описание данного фильма");
                    assertThat(film.getDuration()).isEqualTo(90L);
                    assertThat(film.getMpa().getId()).isEqualTo(1);
                });
    }


}