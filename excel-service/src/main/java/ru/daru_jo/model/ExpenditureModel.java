package ru.daru_jo.model;

import java.sql.Timestamp;

public class ExpenditureModel extends MovementModel {
    public ExpenditureModel(Timestamp timestamp, Double price, Double quantity, String currencyName, String code, String typeOf, Double amountInCur) {
        super(timestamp, price, quantity, currencyName, code, typeOf, amountInCur);
    }

}
