package ru.botov.poker.model;

import java.math.BigDecimal;

public enum Power {

    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QEEAN,
    KING,
    ACE;

    private BigDecimal ordinalBigDecimal;

    public BigDecimal getOrdinalBigDecimal() {
        if (this.ordinalBigDecimal == null) {
            this.ordinalBigDecimal = new BigDecimal(ordinal());
        }
        return ordinalBigDecimal;
    }

}
