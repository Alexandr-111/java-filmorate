package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;  // идентификатор

    @NotBlank(message = "Email не должен быть пустым", groups = UserCreate.class)
    @Email(message = "Некорректный формат email", groups = {UserCreate.class, Default.class, UserUpdate.class})
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @Builder.Default
    private Set<Long> allFriends = new HashSet<>();

    // Определяем группы валидации
    public interface UserCreate {
    }

    public interface UserUpdate {
    }
}