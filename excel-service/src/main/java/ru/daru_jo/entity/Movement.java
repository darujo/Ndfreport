package ru.daru_jo.entity;

import java.sql.Timestamp;

public interface Movement {
    String getCategory();
    String getType();
    String getCompanyName();

    Timestamp getTimestamp();

    Double getPrice();

    Double getQuantity();

    String getCurrencyCode();

    Double getCommission();
}
