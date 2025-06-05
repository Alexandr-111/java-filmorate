package ru.yandex.practicum.filmorate.model;

import jakarta.validation.groups.Default;
import ru.yandex.practicum.filmorate.customvalidator.ReleaseDateAfter;
import ru.yandex.practicum.filmorate.model.validation.CreateFilmValidation;
import ru.yandex.practicum.filmorate.model.validation.UpdateFilmValidation;
import lombok.AllArgsConstructor;
import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @Positive(message = "Id должен быть положительным числом", groups = UpdateFilmValidation.class)
    private long id;

    @NotBlank(message = "Название фильма не должно быть пустым", groups = CreateFilmValidation.class)
    private String name;

    @NotBlank(message = "Описание фильма не должно быть пустым", groups = CreateFilmValidation.class)
    @Size(max = 200, message = "Описание не должно превышать 200 символов", groups = Default.class)
    private String description;

    @ReleaseDateAfter(fieldName = "releaseDate")  // релиз должен быть не ранее установленной даты
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private long duration;

    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();

    private long likes;
}