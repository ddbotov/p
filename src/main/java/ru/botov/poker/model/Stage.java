package ru.botov.poker.model;

public enum Stage {

    NO_GAME(52),
    PREFLOP(50),
    FLOP(47),
    TURN(46),
    RIVER(45);

    private int remainingSizeOfDeck;

    Stage(int remainingSizeOfDeck) {
        this.remainingSizeOfDeck = remainingSizeOfDeck;
    }

    public int getRemainingSizeOfDeck() {
        return remainingSizeOfDeck;
    }
}
