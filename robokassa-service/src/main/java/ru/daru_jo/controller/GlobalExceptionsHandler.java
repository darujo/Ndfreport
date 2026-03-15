package ru.daru_jo.controller;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.daru_jo.exceptions.AppError;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;

@ControllerAdvice
public class GlobalExceptionsHandler {
    @ExceptionHandler
    public ResponseEntity<@NonNull AppError> catchResourceNotFoundRunTime(ResourceNotFoundRunTime e){
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value() ,e.getMessage()),HttpStatus.NOT_FOUND);
    }

}
