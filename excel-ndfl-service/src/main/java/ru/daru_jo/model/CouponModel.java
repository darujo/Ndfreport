package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;
import ru.daru_jo.entity.Coupon;

import java.sql.Timestamp;
@Getter
public class CouponModel implements CurrencyModel{
    private final String code;
    private final Timestamp timestamp;
    private final Double amountInCur;
    private final String currencyName;
    @Setter
    private String currencyCode;
    @Setter
    private Double course;
    @Setter
    private Double amount;

    public CouponModel(Coupon coupon){
        this(coupon.getCode(),coupon.getDate(),coupon.getAmount(),coupon.getCurrency());
    }

    public CouponModel(String code, Timestamp timestamp, Double amountInCur, String currencyName) {
        this.code = code;
        this.timestamp = timestamp;
        this.amountInCur = amountInCur;
        this.currencyName = currencyName;
    }


}
