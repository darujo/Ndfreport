package ru.daru_jo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DividendModel {
    private String currency;
    private String code;
    private DividendRevenueModel dividendRevenueModel;
    private DividendTaxModel dividendTaxModel;
}
