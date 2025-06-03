package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private Long userId;    // Идентификатор пользователя
    private Long personId;  // Идентификатор другого человека

    public Friendship(Long userId, Long personId) {
        this.userId = userId;
        this.personId = personId;
    }
}