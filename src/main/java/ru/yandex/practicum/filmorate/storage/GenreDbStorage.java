package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper = new GenreMapper();

    public List<Genre> getAll() {
        String sql = "SELECT genre_id, name FROM genres ORDER BY genre_id";
        try {
            return jdbcTemplate.query(sql, genreMapper);
        } catch (DataAccessException e) {
            return Collections.emptyList(); // Возвращаем пустой список
        }
    }

    public Genre getGenreById(long id) {
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, genreMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Жанр с ID " + id + " не найден");
        }
    }

    private static class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }
    }
}