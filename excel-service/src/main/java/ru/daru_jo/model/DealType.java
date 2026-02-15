package ru.daru_jo.model;

import lombok.Getter;

@Getter
public enum DealType {
    FOND(""),
    CURRENCY(""),
    INDEX(""),
    PIF("");
    private final String name;

    DealType(String name) {
        this.name = name;
    }
}
