package ru.daru_jo.type;

import lombok.Getter;

@Getter
public enum OperationType {
    COMMISSION_SALE("Комиссия за продажу"),
    COMMISSION_BUY("Комиссия за покупку"),
    BUY("Приобретение позиции"),
    SALE("Приобретение позиции"),
    COUPON_PAYMENT("Купоный платеж"),
    COUPON_NKD("НКД за продажу"),
    COUPON_NKD_MINUS("Отрицательный НКД")
    ;


    private final String Text;

    OperationType(String text) {
        Text = text;
    }

}
