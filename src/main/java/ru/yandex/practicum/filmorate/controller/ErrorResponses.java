package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;

@Getter
public class ErrorResponses {

    private final String error;

    public ErrorResponses(String error) {
        this.error = error;
    }
}
