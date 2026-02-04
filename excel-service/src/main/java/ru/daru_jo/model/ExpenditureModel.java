package ru.daru_jo.model;

import java.sql.Timestamp;

public class ExpenditureModel extends Movement {
    public ExpenditureModel(Timestamp timestamp, Double price, Integer quantity, String currencyName, String code, String typeOf) {
        super(timestamp, price, quantity, currencyName, code, typeOf);
    }

    public ExpenditureModel(Timestamp timestamp, Integer quantity, Double amountInCur, String currencyName, String code, String typeOf) {
        super(timestamp, quantity, amountInCur, currencyName, code, typeOf);
    }
}
