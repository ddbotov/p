package ru.botov.poker.model;

import java.math.BigDecimal;

public enum Power {

    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("T"),
    JACK("J"),
    QEEAN("Q"),
    KING("K"),
    ACE("A");

    private BigDecimal ordinalBigDecimal;
    private String shortName;

    Power(String shortName) {
        this.shortName = shortName;
    }

    public BigDecimal getOrdinalBigDecimal() {
        if (this.ordinalBigDecimal == null) {
            this.ordinalBigDecimal = new BigDecimal(ordinal());
        }
        return ordinalBigDecimal;
    }

    @Override
    public String toString() {
        return this.shortName;
    }
}
