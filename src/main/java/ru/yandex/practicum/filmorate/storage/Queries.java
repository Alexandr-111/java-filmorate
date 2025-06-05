package ru.yandex.practicum.filmorate.storage;

public final class Queries {

    private Queries() {
    }

    public static final String GET_GENRES = """
                SELECT g.genre_id, g.name
                FROM film_genres fg
                JOIN genres g ON fg.genre_id = g.genre_id
                WHERE fg.film_id = ?
                ORDER BY g.genre_id
            """;

    public static final String FILM_WITH_GENRES = """
            SELECT
                f.film_id,
                f.name AS film_name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                ar.rating AS mpa_name,
                GROUP_CONCAT(CONCAT(CONCAT(g.genre_id, ':'), g.name)) AS genres
            FROM films f
            JOIN age_ratings ar ON f.rating_id = ar.rating_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            WHERE f.film_id = ?
            GROUP BY
                f.film_id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                ar.rating
            """;

    public static final String FILM_WITH_MPA = """
                SELECT
                    f.film_id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    f.rating_id,
                    m.rating AS mpa_name,
                    (SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.film_id) AS likes
                FROM films f
                JOIN age_ratings m ON f.rating_id = m.rating_id
                WHERE f.film_id = ?
            """;

    public static final String ALL_FILMS = """
            SELECT
                f.film_id,
                f.name AS film_name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                m.rating AS mpa_name,
                COALESCE(fl.likes_count, 0) AS likes,
                GROUP_CONCAT(CONCAT(g.genre_id, ':', g.name)) AS genres
            FROM films f
            JOIN age_ratings m ON f.rating_id = m.rating_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            LEFT JOIN (
                SELECT film_id, COUNT(*) AS likes_count
                FROM film_likes
                GROUP BY film_id
            ) fl ON f.film_id = fl.film_id
            GROUP BY
                f.film_id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                m.rating,
                fl.likes_count
            """;

    public static final String FILM_BY_ID = """
            SELECT
                f.film_id,
                f.name AS film_name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                ar.rating AS mpa_name,
                g.genre_id AS genre_id,
                g.name AS genre_name
            FROM films f
            JOIN age_ratings ar ON f.rating_id = ar.rating_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            WHERE f.film_id = ?
            ORDER BY g.genre_id
            """;

    public static final String POPULAR_FILMS = """
            SELECT
                f.film_id,
                f.name AS film_name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                ar.rating AS mpa_name,
                (SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.film_id) AS likes,
                GROUP_CONCAT(g.genre_id || ':' || g.name) AS genres
            FROM films f
            JOIN age_ratings ar ON f.rating_id = ar.rating_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            GROUP BY
                f.film_id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                ar.rating
            ORDER BY likes DESC
            LIMIT ?
            """;

    public static final String UPDATE_CORE = """
                UPDATE films SET
                    name = ?,
                    description = ?,
                    release_date = ?,
                    duration = ?,
                    rating_id = ?
                WHERE film_id = ?
            """;

    public static final String COMMON_FRIENDS = """
                SELECT u.*
                FROM users u
                JOIN friendships s1 ON s1.person_id = u.user_id
                JOIN friendships s2 ON s2.person_id = u.user_id
                WHERE s1.user_id = ?
                  AND s2.user_id = ?
            """;
}