package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;
import ru.daru_jo.entity.Expenses;

import java.sql.Timestamp;

@Getter
public class ExpensesModel implements CurrencyModel {
    private final String description;
    private final Timestamp Timestamp;
    private final Double amountInCur;
    private final String currencyName;
    private final String typeOfCode;
    @Setter
    private String currencyCode;
    @Setter
    private Double course;
    @Setter
    private Double amount;
    public ExpensesModel(Expenses expenses){
        this(expenses.getCode(),expenses.getDate(),expenses.getAmount(),expenses.getCurrency(),"");
    }
    public ExpensesModel(String code, Timestamp timestamp, Double amountInCur, String currencyName, String typeOfCode) {
        this.description = code;
        Timestamp = timestamp;
        this.amountInCur = amountInCur;
        this.currencyName = currencyName;
        this.typeOfCode = typeOfCode;
    }
}
