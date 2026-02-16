package ru.daru_jo.model;

import ru.daru_jo.type.OperationType;

import java.sql.Timestamp;

public class RevenueModel extends MovementModel {
    public RevenueModel(OperationType operationType, Timestamp timestamp, Double price, Double quantity, String currencyName, String code, String typeOf) {
        super(operationType, timestamp, price, quantity, currencyName, code, typeOf, null);
    }
}
