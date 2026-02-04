package ru.daru_jo.model;

import java.sql.Timestamp;

public class RevenueModel extends Movement {
    public RevenueModel(Timestamp timestamp, Double price, Integer quantity, String currencyName, String code, String typeOf) {
        super(timestamp, price, quantity, currencyName, code, typeOf);
    }
}
