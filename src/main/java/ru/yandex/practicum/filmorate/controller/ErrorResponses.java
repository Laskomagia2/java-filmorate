package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;

@Getter
public class ErrorResponses {

    String error;

    public ErrorResponses(String error) {
        this.error = error;
    }
}
