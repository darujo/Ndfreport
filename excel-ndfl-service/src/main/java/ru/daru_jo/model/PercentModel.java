package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;
import ru.daru_jo.entity.Percent;

import java.sql.Timestamp;

@Getter
public class PercentModel implements CurrencyModel {
    private final String code;
    private final Timestamp Timestamp;
    private final Double amountInCur;
    private final String currencyName;
    private final String country;
    private final String typeOfCode;
    @Setter
    private String currencyCode;
    @Setter
    private Double course;
    @Setter
    private Double amount;
    public PercentModel(Percent percent){
        this(percent.getCode(),percent.getDate(),percent.getAmount(),percent.getCurrency(),"USA","6013");
    }
    public PercentModel(String code, Timestamp timestamp, Double amountInCur, String currencyName, String country, String typeOfCode) {
        this.code = code;
        Timestamp = timestamp;
        this.amountInCur = amountInCur;
        this.currencyName = currencyName;
        this.country = country;
        this.typeOfCode = typeOfCode;
    }
}
