package ru.daru_jo.entity;

import ru.daru_jo.type.OperationType;

import java.sql.Timestamp;


public class CouponNKD extends Bond{
    public CouponNKD(Long id, String code, Timestamp date, Double amount, String currency, OrderAccount orderAccount) {
        super(id, code, date, amount, currency, orderAccount, OperationType.COUPON_NKD.toString());
    }
}
