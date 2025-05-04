package ru.yandex.practicum.filmorate.dto;

public class Resp {
    String name;
    String description;

    public Resp(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}