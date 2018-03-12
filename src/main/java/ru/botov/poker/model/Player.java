package ru.botov.poker.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Player {

    private BigDecimal bank;

    private Set<Card> cards = new HashSet<>();

    private boolean me;

    public BigDecimal getBank() {
        return bank;
    }

    public void setBank(BigDecimal bank) {
        this.bank = bank;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }


    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }
}
