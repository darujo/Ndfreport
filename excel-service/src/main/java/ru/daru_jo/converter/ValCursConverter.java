package ru.daru_jo.converter;

import ru.daru_jo.dto.Valute;
import ru.daru_jo.entity.CursVal;

import java.sql.Timestamp;

public class ValCursConverter {
    public static CursVal getCursVal(Timestamp timestamp, Valute valute) {
        return new CursVal(null, valute.getNumCode(), valute.getCharCode(), valute.getNominal(), valute.getName(), valute.getValue(), valute.getValUnitRate(), valute.getId(), timestamp);
    }
}
