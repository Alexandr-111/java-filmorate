package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.model.validation.UserCreateValidation;
import ru.yandex.practicum.filmorate.model.validation.UserUpdateValidation;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Positive(message = "Id должен быть положительным числом", groups = UserUpdateValidation.class)
    private long id;

    @NotBlank(message = "Email не должен быть пустым", groups = UserCreateValidation.class)
    @Email(message = "Некорректный формат email", groups = {UserCreateValidation.class,
            Default.class, UserUpdateValidation.class})
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Необходимо указать дату рождения", groups = UserCreateValidation.class)
    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = Default.class)
    private LocalDate birthday;
}