package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class User {
    private int id;  // идентификатор

    @NotBlank(message = "Email не должен быть пустым", groups = UserCreate.class)
    @Email(message = "Некорректный формат email", groups = {UserCreate.class, Default.class, UserUpdate.class})
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;  // имя для отображения

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    // Определяем группы валидации
    public interface UserCreate {
    }

    public interface UserUpdate {
    }
}