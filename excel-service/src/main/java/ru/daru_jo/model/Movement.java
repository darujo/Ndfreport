package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;


import java.sql.Timestamp;

@Getter
public class Movement {
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

    public Movement(Timestamp timestamp,
                    Double price,
                    Double quantity,
                    String currencyName,
                    String code,
                    String typeOf) {
        this.timestamp = timestamp;
        this.price = price;
        this.quantity = quantity;
        this.amountInCur = price *  quantity;
        this.currencyName = currencyName;
        this.code = code;
        this.typeOf = typeOf;
    }
    public Movement(Timestamp timestamp,
                    Double quantity,
                    String currencyName,
                    String code,
                    String typeOf,
                    Double amountInCur) {
        this.timestamp = timestamp;
        this.price = null;
        this.quantity = quantity;
        this.amountInCur = amountInCur;
        this.currencyName = currencyName;
        this.code = code;
        this.typeOf = typeOf;
    }

}
