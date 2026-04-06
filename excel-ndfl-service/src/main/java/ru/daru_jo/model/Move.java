package ru.daru_jo.model;

import lombok.Getter;

import java.util.Map;

@Getter
public class Move {
    Map<String, Map<String, Map<String, Deal>>> mapDeal;
    double amount;

    public Move(Map<String, Map<String, Map<String, Deal>>> mapDeal, double amount) {
        this.mapDeal = mapDeal;
        this.amount = amount;
    }
}
