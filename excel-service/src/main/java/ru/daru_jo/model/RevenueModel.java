package ru.daru_jo.model;

import java.sql.Timestamp;

public class RevenueModel extends MovementModel {
    public RevenueModel(Timestamp timestamp, Double price, Double quantity, String currencyName, String code, String typeOf) {
        super(timestamp, price, quantity, currencyName, code, typeOf, null);
    }
}
