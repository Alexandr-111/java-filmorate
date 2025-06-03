package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/testschema.sql", "/testdata.sql"})
@Import({UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserJdbcTest {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void getUserByIdTest() {
        Optional<User> userOptional = userDbStorage.getUserById(5);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(5L);
                    assertThat(user.getEmail()).isEqualTo("user5@domain.com");
                    assertThat(user.getLogin()).isEqualTo("user5");
                    assertThat(user.getName()).isEqualTo("Дмитрий");
                    assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1998, 12, 30));
                });
    }

    @Test
    void getUserByIdNotFound() {
        Optional<User> userOptional = userDbStorage.getUserById(9999L);
        assertThat(userOptional).isEmpty();
    }

    @Test
    void createUserTest() {
        User newUser = User.builder()
                .email("newuser@mail.ru")
                .login("new_user")
                .name("Новый Пользователь")
                .birthday(LocalDate.of(2000, 6, 1))
                .build();
        // Сохраняем пользователя в БД
        User createdUser = userDbStorage.create(newUser);


        assertThat(createdUser.getId())
                .isPositive();

        // Проверка через обращение к БД
        User dbUser = jdbcTemplate.queryForObject(
                "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?",
                (rs, rowNum) -> User.builder()
                        .id(rs.getLong("user_id"))
                        .email(rs.getString("email"))
                        .login(rs.getString("login"))
                        .name(rs.getString("name"))
                        .birthday(rs.getDate("birthday").toLocalDate())
                        .build(),
                createdUser.getId()
        );

        // Проверка полей
        assertThat(dbUser)
                .isNotNull()
                .extracting(
                        User::getEmail,
                        User::getLogin,
                        User::getName,
                        User::getBirthday)
                .containsExactly(
                        "newuser@mail.ru",
                        "new_user",
                        "Новый Пользователь",
                        LocalDate.of(2000, 6, 1));
    }

    @Test
    void createUserWithExistingEmailTest() {
        // Пользователь с email, который уже существует в базе
        User duplicateEmailUser = User.builder()
                .email("user1@mail.ru") // Этот email уже есть в testdata.sql
                .login("unique_login")
                .birthday(LocalDate.of(2003, 12, 1))
                .build();

        // Проверяем исключение при создании
        assertThatThrownBy(() -> userDbStorage.create(duplicateEmailUser))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("users");
    }
}