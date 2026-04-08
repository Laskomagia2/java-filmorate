package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ParameterNotValidException;

@RestControllerAdvice ("ru.yandex.practicum.filmorate.controller")
public class ErrorHandler {
    @ResponseStatus (HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponses notFoundExceptionHandler(final NotFoundException ex) {
        return new ErrorResponses(ex.getMessage());
    }

    @ResponseStatus (HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponses validationExceptionHandler(final ValidationException ex) {
        return new ErrorResponses(ex.getMessage());
    }

    @ResponseStatus (HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler
    public ErrorResponses parameterNotValidExceptionHandler(final ParameterNotValidException ex) {
        return new ErrorResponses("Parametr invalid value " + ex.getError() + ": " + ex.getDescription());
    }

    @ResponseStatus (HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponses anyOtherError(final Throwable ex) {
        return new ErrorResponses("Произошла непредвиденная ошибка.");
    }
}

