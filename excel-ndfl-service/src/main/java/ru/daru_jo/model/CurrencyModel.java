package ru.daru_jo.model;

import java.sql.Timestamp;

public interface CurrencyModel {
    String getCurrencyName();

    Timestamp getTimestamp();

    void setCurrencyCode(String numCode);

    void setCourse(Double valUnitRate);

    Double getAmountInCur();

    void setAmount(Double amount);
}
