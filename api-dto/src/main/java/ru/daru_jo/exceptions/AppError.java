package ru.daru_jo.exceptions;

import java.io.Serializable;

public class AppError implements Serializable {
    @SuppressWarnings("unused")
    public AppError() {
    }

    private Integer statusCode;
    private String message;

    public AppError(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @SuppressWarnings("unused")
    public Integer getStatusCode() {
        return statusCode;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }
}
