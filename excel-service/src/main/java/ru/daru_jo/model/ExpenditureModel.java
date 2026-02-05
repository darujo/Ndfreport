package ru.daru_jo.model;

import java.sql.Timestamp;

public class ExpenditureModel extends Movement {
    public ExpenditureModel(Timestamp timestamp, Double price, Double quantity, String currencyName, String code, String typeOf) {
        super(timestamp, price, quantity, currencyName, code, typeOf);
    }

    public ExpenditureModel(Timestamp timestamp, Double quantity,  String currencyName, String code, String typeOf,Double amountInCur) {
        super(timestamp, quantity,  currencyName, code, typeOf,amountInCur);
    }
}
