package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
public class DividendRevenueModel implements CurrencyModel{
    private final String code;
    private final Timestamp timestamp;
    private Double amountInCur;
    private final String currencyName;
    private final String country;
    private final String typeOf;
    @Setter
    private String currencyCode;
    @Setter
    private Double course;
    @Setter
    private Double amount;

    public DividendRevenueModel(String code, Timestamp timestamp, Double amountInCur, String currencyName, String country, String typeOf) {
        this.code = code;
        this.timestamp = timestamp;
        this.amountInCur = amountInCur;
        this.currencyName = currencyName;
        this.country = country;
        this.typeOf = typeOf;
    }

    public void addAmountInCur(Double amountInCur) {
        this.amountInCur = this.amountInCur + amountInCur;
        this.amount = this.amountInCur * course;
    }
}
