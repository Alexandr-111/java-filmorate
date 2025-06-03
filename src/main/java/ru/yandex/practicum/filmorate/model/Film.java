package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.customvalidator.ReleaseDateAfter;
import lombok.AllArgsConstructor;
import lombok.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.groups.Default;


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
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @ReleaseDateAfter(fieldName = "releaseDate")  // релиз должен быть не ранее установленной даты
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private long duration;

    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();

    private long likes;  // счетчик лайков

    @JsonIgnore
    @Builder.Default
    private Set<Long> likesFilm = new HashSet<>();

    public interface CreateFilmValidation extends Default {
    }

    public interface UpdateFilmValidation extends Default {
    }
}