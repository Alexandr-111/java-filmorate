package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.customvalidator.ReleaseDateAfter;
import lombok.AllArgsConstructor;
import lombok.*;
import jakarta.validation.constraints.*;


import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor

public class Film {
    int id;  // идентификатор

    @NotBlank(message = "Название фильма не должно быть пустым")
    String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    String description;

    @ReleaseDateAfter(fieldName = "releaseDate")  // релиз должен быть не ранее установленной даты
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    long duration;
}