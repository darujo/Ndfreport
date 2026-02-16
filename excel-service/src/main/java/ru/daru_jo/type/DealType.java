package ru.daru_jo.type;

import lombok.Getter;

@Getter
public enum DealType {
    FOND("Фондовый рынок: ценные бумаги, обращающиеся на рынке (Interactive Brokers #acct#)"),
    CURRENCY("Валюта, металлы, товары (Interactive Brokers #acct#)"),
    INDEX("Производные финансовые инструменты, обращающиеся на рынке (ПФИ на акции и индексы) (Interactive Brokers #acct#)"),
    PIF("ПФИ, не обращающиеся на рынке (Interactive Brokers #acct#)");
    private final String name;

    DealType(String name) {
        this.name = name;
    }
}
