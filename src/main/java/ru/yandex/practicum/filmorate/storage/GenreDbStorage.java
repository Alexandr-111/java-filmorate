package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    GenreMapper genreMapper = new GenreMapper();

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

    public Set<Long> findMissingIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = "SELECT genre_id FROM genres WHERE genre_id IN (" + placeholders + ")";
        // Получаем существующие ID
        Set<Long> existingIds = new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, ids.toArray()));

        // Возвращаем разницу между запрошенными и существующими ID
        Set<Long> missingIds = new HashSet<>(ids);
        missingIds.removeAll(existingIds);
        return missingIds;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateFilmGenres(Film film) {
        long filmId = film.getId();

        // Удаление текущих связей
        int deleted = jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Long> uniqueIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            //  Пакетная вставка
            List<Object[]> batchArgs = uniqueIds.stream()
                    .map(genreId -> new Object[]{filmId, genreId})
                    .collect(Collectors.toList());

            jdbcTemplate.batchUpdate(
                    "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                    batchArgs
            );
        }
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        String sql = """
                    SELECT g.genre_id, g.name
                    FROM film_genres fg
                    JOIN genres g ON fg.genre_id = g.genre_id
                    WHERE fg.film_id = ?
                    ORDER BY g.genre_id
                """;
        return jdbcTemplate.query(sql, new GenreMapper(), filmId);
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