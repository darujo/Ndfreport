package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultRow {
    private Integer row;
    private Double revenueAmount = 0d;
    private Double expenditureAmount = 0d;

    public ResultRow(Integer row) {
        this.row = row;
    }
}
