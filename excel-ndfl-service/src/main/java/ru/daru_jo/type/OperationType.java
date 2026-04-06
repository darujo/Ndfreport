package ru.daru_jo.type;

import lombok.Getter;

@Getter
public enum OperationType {
    COMMISSION_SALE("Комиссия за продажу"),
    COMMISSION_BUY("Комиссия за покупку"),
    BUY("Приобретение позиции"),
    SALE("Приобретение позиции"),
    COUPON_PAYMENT("Купонный платеж"),
    COUPON_NKD("НКД за продажу"),
    COUPON_NKD_MINUS("Отрицательный НКД"),
    BOND_FULL_CALL ("Досрочное погашение"),
    BOND_CLOSED_LOT("Приобретение позиции"),
    INTEREST("Проценты"),
    INTEREST_FEES("Проценты комиссии с +"),
    EXPENSES("Общие расходы"),
    EXPENSES_FEES("Общие расходы комиссия с -"),
    EXPENSES_INTEREST_PAID("Ставка брокера: уплачено"),
    EXPENSES_TRANSACTION_FEES("Комиссия за перевод")
    ;


    private final String Text;

    OperationType(String text) {
        Text = text;
    }

}
