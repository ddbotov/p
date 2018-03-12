package ru.botov.poker.model;

public enum Stage {

    NO_GAME(52, null),
    RIVER(45, NO_GAME),
    TURN(46, RIVER),
    FLOP(47, TURN),
    PREFLOP(50, FLOP);

    private final Stage nextStage;
    private final int remainingSizeOfDeck;

    Stage(int remainingSizeOfDeck, Stage nextStage) {
        this.remainingSizeOfDeck = remainingSizeOfDeck;
        this.nextStage = nextStage;
    }

    public int getRemainingSizeOfDeck() {
        return remainingSizeOfDeck;
    }
}
