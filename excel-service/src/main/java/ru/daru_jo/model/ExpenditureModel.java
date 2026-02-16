package ru.daru_jo.model;

import ru.daru_jo.type.OperationType;

import java.sql.Timestamp;

public class ExpenditureModel extends MovementModel {
    public ExpenditureModel(OperationType operationType, Timestamp timestamp, Double price, Double quantity, String currencyName, String code, String typeOf, Double amountInCur) {
        super(operationType, timestamp, price, quantity, currencyName, code, typeOf, amountInCur);
    }

}
