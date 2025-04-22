package ru.yandex.practicum.filmorate.exeption;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}