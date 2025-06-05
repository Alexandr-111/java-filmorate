package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/testschema.sql", "/testdata.sql"})
@Import({GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreJdbcTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getAllTest() {

        List<Genre> genres = genreDbStorage.getAll();
        assertThat(genres)
                .hasSize(6)
                .extracting(Genre::getName)
                .containsExactly(
                        "Комедия",
                        "Драма",
                        "Мультфильм",
                        "Триллер",
                        "Документальный",
                        "Боевик"
                );
    }

    @Test
    void getGenreByIdTest() {

        Genre genre = genreDbStorage.getGenreById(1);
        assertThat(genre)
                .extracting(Genre::getId, Genre::getName)
                .containsExactly(1L, "Комедия");
    }

    @Test
    void getGenreByIdFailTest() {
        assertThatThrownBy(() -> genreDbStorage.getGenreById(9999))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найден");
    }
}