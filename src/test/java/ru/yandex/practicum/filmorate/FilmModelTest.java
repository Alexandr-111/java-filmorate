package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmModelTest {

    private static ValidatorFactory factory;
    private Validator validator;

    @BeforeAll
    static void setupAll() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeFactory() {
        if (factory != null) {
            factory.close();
        }
    }

    @BeforeEach
    void setUp() {
        validator = factory.getValidator();
    }

    @Test
    public void testValidFilm() {
        Film validFilm = Film.builder()
                .name("Первый фильм")
                .description("Описание этого фильма.")
                .releaseDate(LocalDate.of(2023, 10, 20))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertTrue(violations.isEmpty()); // Если объект Film является валидным, то Set будет пустым
    }

    @Test
    public void testFilmWithBlankName() {

        Film invalidFilm = Film.builder()
                .name("")
                .description("Описание этого фильма.")
                .releaseDate(LocalDate.of(2023, 10, 20))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        assertFalse(violations.isEmpty()); // Если объект Film является невалидным, то Set не будет пустым
    }

    @Test
    public void testFilmWithLongDescription() {

        Film invalidFilm = Film.builder()
                .name("Valid Film")
                .description("Это описание длиннее чем разрешенные двести символов." +
                        "Это описание длиннее чем разрешенные двести символов. " +
                        "Это описание длиннее чем разрешенные двести символов. " +
                        "Это описание длиннее чем разрешенные двести символов.")
                .releaseDate(LocalDate.of(2023, 10, 20))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFilmWithInvalidReleaseDate() {

        Film invalidFilm = Film.builder()
                .name("Первый фильм")
                .description("Описание этого фильма.")
                .releaseDate(LocalDate.of(1894, 10, 26)) // Перед разрешенной датой релиза
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFilmWithNegativeDuration() {

        Film invalidFilm = Film.builder()
                .name("Первый фильм")
                .description("Описание этого фильма.")
                .releaseDate(LocalDate.of(2023, 10, 20))
                .duration(-120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFilmWithNullReleaseDate() {

        Film invalidFilm = Film.builder()
                .name("Первый фильм")
                .description("Описание этого фильма.")
                .releaseDate(null)
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testFilmWithNullName() {

        Film invalidFilm = Film.builder()
                .name(null)
                .description("Описание этого фильма.")
                .releaseDate(LocalDate.of(2023, 10, 20))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        assertFalse(violations.isEmpty());
    }
}