package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

@Getter
public class ParameterNotValidException extends RuntimeException{

    private String error;

    private String description;

    public ParameterNotValidException(String error, String description) {
        this.error = error;
        this.description = description;
    }
}
