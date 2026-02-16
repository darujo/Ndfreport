package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;
import ru.daru_jo.type.OperationType;


import java.sql.Timestamp;

@Getter
public class MovementModel implements CurrencyModel {
    private final OperationType operationType;
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


    public MovementModel(OperationType operationType,
                         Timestamp timestamp,
                         Double price,
                         Double quantity,
                         String currencyName,
                         String code,
                         String typeOf,
                         Double amountInCur) {
        if (price != null && amountInCur != null) {
            throw new RuntimeException(operationType.toString() + " цена и стоимость не  может быть одновременно");
        }
        this.operationType = operationType;
        this.timestamp = timestamp;
        this.price = price;
        this.quantity = quantity;
        this.amountInCur = price == null ? amountInCur : price * quantity;
        this.currencyName = currencyName;
        this.code = code;
        this.typeOf = typeOf;
    }

}
