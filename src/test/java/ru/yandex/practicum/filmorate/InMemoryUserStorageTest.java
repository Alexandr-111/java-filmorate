package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exeption.DataNotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void testCreateUser() {

        User user = User.builder()
                .id(0L)
                .email("test@example.com")
                .login("testlogin")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 6, 1))
                .allFriends(new HashSet<>())
                .build();

        User createdUser = userStorage.create(user);

        assertNotNull(createdUser, "Созданный пользователь не должен быть null");
        assertNotEquals(0L, createdUser.getId(), "Id пользователя должен быть сгенерирован");
        assertTrue(createdUser.getId() > 0, "Id должен быть больше 0");
        assertEquals(user.getEmail(), createdUser.getEmail(), "Email должен совпадать");
        assertEquals(user.getLogin(), createdUser.getLogin(), "Логин должен совпадать");
        assertEquals(user.getName(), createdUser.getName(), "Имя должно совпадать");
        assertEquals(user.getBirthday(), createdUser.getBirthday(), "Дата рождения должна совпадать");

        // Проверим помещен ли созданный объект в хранилище, получим его по id
        Optional<User> optionalUser = userStorage.getUserById(1L);
        User retrievedUser = optionalUser.get();
        assertNotNull(retrievedUser, "Объект должен быть помещен в хранилище после создания");
        assertEquals(createdUser, retrievedUser, "Объект в хранилище должен совпадать с созданным объектом");
        assertEquals(createdUser.getName(), retrievedUser.getName(), "Имя должно совпадать");
        assertEquals(createdUser.getEmail(), retrievedUser.getEmail(), "Email должен совпадать");
    }

    @Test
    void testUpdateExistingUser() {

        User initialUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .login("testlogin")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 5, 1))
                .build();
        userStorage.create(initialUser);
        User updatedUser = User.builder()
                .id(1L)
                .email("updated@example.com")
                .login("updatedlogin")
                .name("Иван Новиков")
                .birthday(LocalDate.of(1991, 6, 2))
                .build();

        User resultUser = userStorage.update(updatedUser);

        assertNotNull(resultUser, "Пользователь после обновления не должен быть null");
        assertEquals(updatedUser.getId(), resultUser.getId(), "Id пользователей должны совпадать");
        assertEquals(updatedUser.getEmail(), resultUser.getEmail(), "Email должен совпадать");
        assertEquals(updatedUser.getLogin(), resultUser.getLogin(), "Login должен совпадать");
        assertEquals(updatedUser.getName(), resultUser.getName(), "Имя должно совпадать");
        assertEquals(updatedUser.getBirthday(), resultUser.getBirthday(), "День рождения должен совпадать");

        // Убедимся, что пользователь обновился в хранилище
        Optional<User> optionalUser = userStorage.getUserById(1L);
        User retrievedUser = optionalUser.get();
        assertEquals("Иван Новиков", retrievedUser.getName(), "Имя должно совпадать");
        assertEquals("updated@example.com", retrievedUser.getEmail(), "Email должен совпадать");
    }

    @Test
    public void testUpdateNonExistingUser() {

        User nonExistingUser = User.builder()
                .id(987) //  Используем id, которого нет
                .email("test@example.com")
                .login("testlogin")
                .name("Несуществующий пользователь")
                .birthday(LocalDate.of(1985, 1, 1))
                .build();

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> {
            userStorage.update(nonExistingUser);
        });

        assertNotNull(exception, "Метод update должен выбросить исключение DataNotFoundException");
        assertEquals("Пользователь с таким id не найден", exception.getMessage(), "Сообщения разные");
    }
}