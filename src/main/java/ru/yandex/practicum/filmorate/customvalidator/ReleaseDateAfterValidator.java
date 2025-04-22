package ru.yandex.practicum.filmorate.customvalidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateAfterValidator implements ConstraintValidator<ReleaseDateAfter, LocalDate> {
    private LocalDate date;

    @Override
    public void initialize(ReleaseDateAfter customAnnotation) {
        this.date = LocalDate.of(1895, 12, 28);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null && value.isBefore(date)) {
            return false; // Возвращаем false, валидация не прошла
        }
        return true;
    }
}