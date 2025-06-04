package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.storage.Queries.COMMON_FRIENDS;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
                    preparedStmt.setString(1, user.getEmail());
                    preparedStmt.setString(2, user.getLogin());
                    preparedStmt.setString(3, user.getName());
                    preparedStmt.setDate(4, Date.valueOf(user.getBirthday()));
                    return preparedStmt;
                },
                keyHolder);

        // Получаем сгенерированный ID и устанавливаем его в User
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        String sql = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            // Если пользователь с таким ID не найден
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT user_id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sqlQuery, userRowMapper);
    }

    @Override
    public User update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User не должен быть null");
        }
        long userId = user.getId();
        String sqlQuery = "UPDATE users SET " +
                "email = ?, " +
                "login = ?, " +
                "name = ?, " +
                "birthday = ? " +
                "WHERE user_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    (user.getBirthday() != null) ? Date.valueOf(user.getBirthday()) : null,
                    userId);

            if (rowsAffected == 0) {
                throw new DataNotFoundException("Пользователь с id " + userId + " не найден.");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Ошибка при обновлении пользователя с id " + userId, e) {
            };
        }
        return user;
    }

    // Добавление в друзья
    @Override
    public void addToFriends(long userId, long personId) {
        String sql = "INSERT INTO friendships (user_id, person_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, personId);
    }

    // Удаление из друзей
    @Override
    public void removeFromFriends(long userId, long personId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND person_id = ?";
        jdbcTemplate.update(sql, userId, personId);
    }

    // Получение списка друзей
    @Override
    public List<User> displayFriends(long userId) {
        String sql = """
                    SELECT u.*
                    FROM users u
                    JOIN friendships s ON u.user_id = s.person_id
                    WHERE s.user_id = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    // Поиск общих друзей
    @Override
    public List<User> displayCommonFriends(long userId, long otherUserId) {
        return jdbcTemplate.query(COMMON_FRIENDS, userRowMapper, userId, otherUserId);
    }

    // Проверка существования пользователя
    @Override
    public boolean userNotExists(long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count == null || count == 0;
    }

    private static class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            Date birthdayDate = rs.getDate("birthday");
            if (birthdayDate != null) {
                user.setBirthday(birthdayDate.toLocalDate());
            }
            return user;
        }
    }
}