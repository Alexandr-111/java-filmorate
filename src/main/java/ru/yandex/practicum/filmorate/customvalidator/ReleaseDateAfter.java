package ru.yandex.practicum.filmorate.customvalidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD}) // Применяется к полям
@Retention(RetentionPolicy.RUNTIME) //  Доступна во время выполнения программы
@Constraint(validatedBy = ReleaseDateAfterValidator.class) //  Валидация выполняется классом ReleaseDateAfterValidator
public @interface ReleaseDateAfter {

    String message() default "Дата релиза должна быть не раньше 28 декабря 1895 года";

    String fieldName();

    Class[] groups() default {}; // Обязательный параметр для @Constraint

    Class<? extends Payload>[] payload() default {}; // Обязательный параметр для @Constraint
}