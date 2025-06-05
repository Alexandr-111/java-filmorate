package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAll() {
        String sql = "SELECT rating_id, rating FROM age_ratings ORDER BY rating_id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("rating_id"));
            mpa.setName(rs.getString("rating"));
            return mpa;
        });
    }

    // Проверка существования в БД
    public boolean mpaNotExists(int id) {
        String sql = "SELECT COUNT(*) FROM age_ratings WHERE rating_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count == null || count == 0;
    }

    public Mpa getById(int id) {
        String sql = "SELECT rating_id, rating FROM age_ratings WHERE rating_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new Mpa(rs.getInt("rating_id"),
                            rs.getString("rating")), id);
        } catch (Exception e) {
            throw new DataNotFoundException("Рейтинг с id " + id + " не найден в базе данных");
        }
    }
}