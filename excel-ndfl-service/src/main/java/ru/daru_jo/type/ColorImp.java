package ru.daru_jo.type;

import ru.daru_jo.helper.ColorInterface;

public enum ColorImp implements ColorInterface {
    HEAD (51,51,153),
    CATEGORY(157,195,230),
    TABLE_HEAD(216,230,252),
    REVENUE(205,222,188),
    EXPENDITURE(244,212,190),
    REVENUE_RESULT(205,222,188),
    EXPENDITURE_RESULT(244,212,190),
    COLUMN_HEAD(230,239,221),
    TABLE_RESULT(237,222,242),
    RESULT(218,190,228);

    private final int red;
    private final int green;
    private final int blue;

    ColorImp(int red, int green, int blue) {
        this.red =red;
        this.green =green;
        this.blue =blue;
    }

    @Override
    public int getRed() {
        return red;
    }

    @Override
    public int getGreen() {
        return green;
    }

    @Override
    public int getBlue() {
        return blue;
    }
}
