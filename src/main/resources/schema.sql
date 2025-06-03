DROP TABLE IF EXISTS FRIENDSHIPS CASCADE;
DROP TABLE IF EXISTS FILM_LIKES CASCADE;
DROP TABLE IF EXISTS FILM_GENRES CASCADE;
DROP TABLE IF EXISTS FILMS CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS GENRES CASCADE;
DROP TABLE IF EXISTS AGE_RATINGS CASCADE;

/* Создание таблицы возрастных рейтингов */
CREATE TABLE IF NOT EXISTS AGE_RATINGS (
    rating_id INT PRIMARY KEY,
    rating VARCHAR(10) NOT NULL UNIQUE
);

/* Создание таблицы жанров */
CREATE TABLE IF NOT EXISTS GENRES (
    genre_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

/* Создание таблицы пользователей */
CREATE TABLE IF NOT EXISTS USERS (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE
);

/* Создание таблицы фильмов */
CREATE TABLE IF NOT EXISTS FILMS (
    film_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration BIGINT NOT NULL,
    rating_id INT NOT NULL,
    FOREIGN KEY (rating_id) REFERENCES AGE_RATINGS(rating_id) ON DELETE CASCADE
);

/* Создание связующей таблицы film_genres */
CREATE TABLE IF NOT EXISTS FILM_GENRES (
    film_id BIGINT NOT NULL,
    genre_id BIGINT  NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE,
    CONSTRAINT uq_film_genre UNIQUE (film_id, genre_id)
);

/* Создание таблицы лайков */
CREATE TABLE IF NOT EXISTS FILM_LIKES (
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES FILMS(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

/* Создание таблицы дружбы */
CREATE TABLE IF NOT EXISTS FRIENDSHIPS (
    user_id BIGINT,
    person_id BIGINT,
    PRIMARY KEY (user_id, person_id),
    FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);