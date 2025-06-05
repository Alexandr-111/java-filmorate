package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.Queries.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper = new FilmRowMapper();

    @Override
    public Film create(Film film) {
        validateFilm(film);
        // Сохраняем основной фильм
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        // Получаем сгенерированный ID и создаем  Film с установленным ID
        long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);
        // Сохраняем жанры
        if (film.getGenres() != null) {
            saveFilmGenres(film);
        }
        // Возвращаем фильм с полными данными
        return loadFullFilm(filmId);
    }

    private void saveFilmGenres(Film film) {
        // Удаляем существующие связи
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        // Дедупликация жанров перед сохранением
        Map<Long, Genre> uniqueGenres = new LinkedHashMap<>();
        for (Genre genre : film.getGenres()) {
            uniqueGenres.put(genre.getId(), genre);
        }
        // Пакетная вставка
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Long genreId : uniqueGenres.keySet()) {
            batchArgs.add(new Object[]{film.getId(), genreId});
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private Film loadFullFilm(long filmId) {

        Film film = jdbcTemplate.queryForObject(FILM_WITH_MPA, (rs, rowNum) -> {
            Film f = new Film();
            f.setId(rs.getLong("film_id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            f.setReleaseDate(rs.getDate("release_date").toLocalDate());
            f.setDuration(rs.getInt("duration"));
            f.setLikes(rs.getLong("likes"));

            // Создаем объект Mpa
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("rating_id"));
            mpa.setName(rs.getString("mpa_name"));
            f.setMpa(mpa);
            return f;
        }, filmId);

        // Загрузка жанров
        String genreSql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genreSql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            return genre;
        }, filmId);
        Objects.requireNonNull(film).setGenres(genres);
        return film;
    }

    // Проверка существования фильма в БД
    public boolean filmNotExists(long filmId) {
        String sql = "SELECT NOT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, filmId));
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Фильм не должен быть null");
        }
        // Валидация MPA
        validateMpa(film.getMpa().getId());

        // Валидация жанров
        validateGenres(film.getGenres());
    }

    @Transactional
    @Override
    public Film update(Film film) {
        validateFilm(film);
        long filmId = film.getId();
        if (filmNotExists(filmId)) {
            throw new DataNotFoundException("Фильм с ID " + filmId + " не найден. Обновление невозможно.");
        }
        // Блокировка фильма
        jdbcTemplate.queryForObject(
                "SELECT film_id FROM films WHERE film_id = ? FOR UPDATE",
                Long.class,
                filmId
        );
        // Обновляем основные данные
        updateFilmCoreData(film);
        // Обновляем связанные жанры
        updateGenres(film);
        // Получаем полные данные из БД
        Film updatedFilm = getFilmWithDetails(filmId);
        // Добавляем лайки
        updatedFilm.setLikes(getLikesCount(filmId));
        return updatedFilm;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateGenres(Film film) {
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

    private void updateFilmCoreData(Film film) {
        long filmId = film.getId();
        int rowsAffected = jdbcTemplate.update(UPDATE_CORE,
                film.getName(),
                film.getDescription(),
                (film.getReleaseDate() != null) ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa().getId(),
                filmId);

        if (rowsAffected == 0) {
            throw new DataNotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private Film getFilmWithDetails(long filmId) {
        Film film = getFilmBasicInfo(filmId);
        List<Genre> genres = jdbcTemplate.query(GET_GENRES, (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("genre_id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }, filmId
        );
        film.setGenres(genres);
        return film;
    }

    private Film getFilmBasicInfo(long filmId) {
        return jdbcTemplate.queryForObject(FILM_WITH_GENRES, filmRowMapper, filmId);
    }

    private Long getLikesCount(long filmId) {
        String sql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, filmId);
    }

    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query(ALL_FILMS, filmRowMapper);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        try {
            List<Film> films = jdbcTemplate.query(FILM_BY_ID, rs -> {
                Map<Long, Film> filmMap = new HashMap<>();
                while (rs.next()) {
                    long filmId = rs.getLong("film_id");
                    Film film = filmMap.get(filmId);
                    if (film == null) {
                        film = new Film();
                        film.setId(filmId);
                        film.setName(rs.getString("film_name"));
                        film.setDescription(rs.getString("description"));
                        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                        film.setDuration(rs.getInt("duration"));
                        film.setMpa(new Mpa(
                                rs.getInt("rating_id"),
                                rs.getString("mpa_name")
                        ));
                        film.setGenres(new ArrayList<>());
                        filmMap.put(filmId, film);
                    }
                    rs.getLong("genre_id");
                    long genreId;
                    if (!rs.wasNull()) {
                        genreId = rs.getLong("genre_id");
                        String genreName = rs.getString("genre_name");

                        // Дедупликация жанров
                        if (film.getGenres().stream().noneMatch(g -> g.getId() == genreId)) {
                            film.getGenres().add(new Genre(genreId, genreName));
                        }
                    }
                }
                return new ArrayList<>(filmMap.values());
            }, id);

            return Objects.requireNonNull(films).isEmpty() ? Optional.empty() : Optional.of(films.get(0));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(long id, long userId) {
        // Проверяем существование фильма
        if (filmNotExists(id)) {
            throw new DataNotFoundException("Фильм с id " + id + " не найден");
        }
        // Добавляем лайк (создаем запись в таблице film_likes)
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Transactional
    @Override
    public void deleteLike(long filmId, long userId) {
        // Проверяем существование фильма
        if (filmNotExists(filmId)) {
            throw new DataNotFoundException("Фильм с id " + filmId + " не найден");
        }
        // Проверяем существование пользователя
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        if (count == null || count == 0) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не найден");
        }
        // Удаляем лайк (запись из таблицы film_likes)
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> mostPopularFilms(int count) {
        return jdbcTemplate.query(POPULAR_FILMS, filmRowMapper, count);
    }

    private void validateMpa(int mpaId) {
        String sql = "SELECT COUNT(*) FROM age_ratings WHERE rating_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        if (count == null || count == 0) {
            throw new DataNotFoundException("MPA-рейтинг с id " + mpaId + " не найден в БД");
        }
    }

    private void validateGenres(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        Set<Long> genreIds = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        Set<Long> missingIds = findMissingIds(genreIds);
        if (!missingIds.isEmpty()) {
            throw new DataNotFoundException("Следующие ID жанров не найдены: " + missingIds);
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

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Mpa(rs.getInt("rating_id"), rs.getString("mpa_name")));

            // Обработка жанров с сохранением порядка
            String genreString = rs.getString("genres");
            if (genreString != null && !genreString.isEmpty()) {
                Map<Long, String> genreMap = new LinkedHashMap<>();
                Arrays.stream(genreString.split(","))
                        .forEach(pair -> {
                            String[] parts = pair.split(":");
                            if (parts.length == 2) {
                                long genreId = Long.parseLong(parts[0]);
                                String genreName = parts[1];
                                genreMap.put(genreId, genreName);
                            }
                        });
                List<Genre> genres = new ArrayList<>();
                for (Map.Entry<Long, String> entry : genreMap.entrySet()) {
                    genres.add(new Genre(entry.getKey(), entry.getValue()));
                }
                film.setGenres(genres);
            } else {
                film.setGenres(Collections.emptyList());
            }
            return film;
        }
    }
}