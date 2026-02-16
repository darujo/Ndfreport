package ru.daru_jo.type;

import lombok.Getter;

@Getter
public enum OperationType {
    COMMISSION_SALE("Комиссия за продажу"),
    COMMISSION_BUY("Комиссия за покупку"),
    BUY("Приобретение позиции"),
    SALE("Приобретение позиции");

    private final String Text;

    OperationType(String text) {
        Text = text;
    }

}
