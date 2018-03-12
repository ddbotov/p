package ru.botov.poker.model;

import java.math.BigInteger;

public class Game {

    private Table table;

    private BigInteger blind;

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public BigInteger getBlind() {
        return blind;
    }

    public void setBlind(BigInteger blind) {
        this.blind = blind;
    }
}
