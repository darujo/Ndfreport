package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;


import java.sql.Timestamp;

@Getter
public class MovementModel implements CurrencyModel{
    private final Timestamp timestamp;
    private final Double price;
    private final Double quantity;
    private final Double amountInCur;
    @Setter
    private String currencyCode = "";
    private final String currencyName;
    @Setter
    private Double course = 0d;
    @Setter
    private Double amount = 0d;
    private final String code;
    private final String typeOf;


    public MovementModel(Timestamp timestamp,
                         Double price,
                         Double quantity,
                         String currencyName,
                         String code,
                         String typeOf,
                         Double amountInCur) {
        this.timestamp = timestamp;
        this.price = price;
        this.quantity = quantity;
        this.amountInCur = price != null ? price *  quantity : amountInCur;
        this.currencyName = currencyName;
        this.code = code;
        this.typeOf = typeOf;
    }

}
