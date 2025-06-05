package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/testschema.sql", "/testdata.sql"})
@Import({MpaDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaJdbcTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getByIdMpaTest() {
        assertThat(mpaDbStorage.getById(1))
                .as("Рейтинг с ID=1")
                .extracting(Mpa::getId, Mpa::getName)
                .containsExactly(1, "G");

        assertThat(mpaDbStorage.getById(3))
                .as("Рейтинг с ID=3")
                .extracting(Mpa::getId, Mpa::getName)
                .containsExactly(3, "PG-13");
    }

    @Test
    void getByIdForNonExistingId() {
        int[] invalidIds = {-1, 0, 9, 9999};
        for (int id : invalidIds) {
            assertThatThrownBy(() -> mpaDbStorage.getById(id))
                    .as("Проверка несуществующего ID: " + id)
                    .isInstanceOf(DataNotFoundException.class)
                    .hasMessageContaining("не найден");
        }
    }
}