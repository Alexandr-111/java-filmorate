package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserModelTest {

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
    public void testValidUser() {

        User user1 = User.builder()
                .email("user1@example.com")
                .login("MyLogin")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertTrue(violations.isEmpty());
    }


    @Test
    public void testUserWithBlankEmail() {

        User user2 = User.builder()
                .email("")
                .login("validLogin")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user2);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testUserWithInvalidEmailFormat() {

        User user2 = User.builder()
                .email("invalid.email")
                .login("validLogin")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user2);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testUserWithBlankLogin() {

        User user2 = User.builder()
                .email("user2@example.com")
                .login("")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user2);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testUserWithLoginAndSpaces() {

        User user2 = User.builder()
                .email("user2email@example.com")
                .login("login with spaces")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user2);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testUserWithFutureBirthday() {

        User user2 = User.builder()
                .email("user2email@example.com")
                .login("validLogin")
                .name("Иван Иванов")
                .birthday(LocalDate.of(2035, 11, 29))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user2);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testUserWithNullEmail() {

        User user2 = User.builder()
                .email(null)
                .login("validLogin")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user2);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testUserWithNullLogin() {

        User user2 = User.builder()
                .email("user2email@example.com")
                .login(null)
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user2);
        assertFalse(violations.isEmpty());
    }
}