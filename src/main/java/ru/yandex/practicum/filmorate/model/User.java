package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class User {
    int id;  // идентификатор

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    String login;

    String name;  // имя для отображения

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}