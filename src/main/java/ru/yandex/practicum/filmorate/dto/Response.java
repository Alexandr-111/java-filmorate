package ru.yandex.practicum.filmorate.dto;

public class Response {
    private final String name;
    private final String description;

    public Response(String name, String description) {
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