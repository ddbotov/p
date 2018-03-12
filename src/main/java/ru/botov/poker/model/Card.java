package ru.botov.poker.model;

public enum Card {

    TWO_HEART(Power.TWO, Suit.HEART),
    THREE_HEART(Power.THREE, Suit.HEART),
    FOUR_HEART(Power.FOUR, Suit.HEART),
    FIVE_HEART(Power.FIVE, Suit.HEART),
    SIX_HEART(Power.SIX, Suit.HEART),
    SEVEN_HEART(Power.SEVEN, Suit.HEART),
    EIGHT_HEART(Power.EIGHT, Suit.HEART),
    NINE_HEART(Power.NINE, Suit.HEART),
    TEN_HEART(Power.TEN, Suit.HEART),
    JACK_HEART(Power.JACK, Suit.HEART),
    QEEAN_HEART(Power.QEEAN, Suit.HEART),
    KING_HEART(Power.KING, Suit.HEART),
    ACE_HEART(Power.ACE, Suit.HEART),

    TWO_DIAMOND(Power.TWO, Suit.DIAMOND),
    THREE_DIAMOND(Power.THREE, Suit.DIAMOND),
    FOUR_DIAMOND(Power.FOUR, Suit.DIAMOND),
    FIVE_DIAMOND(Power.FIVE, Suit.DIAMOND),
    SIX_DIAMOND(Power.SIX, Suit.DIAMOND),
    SEVEN_DIAMOND(Power.SEVEN, Suit.DIAMOND),
    EIGHT_DIAMOND(Power.EIGHT, Suit.DIAMOND),
    NINE_DIAMOND(Power.NINE, Suit.DIAMOND),
    TEN_DIAMOND(Power.TEN, Suit.DIAMOND),
    JACK_DIAMOND(Power.JACK, Suit.DIAMOND),
    QEEAN_DIAMOND(Power.QEEAN, Suit.DIAMOND),
    KING_DIAMOND(Power.KING, Suit.DIAMOND),
    ACE_DIAMOND(Power.ACE, Suit.DIAMOND),

    TWO_CLUB(Power.TWO, Suit.CLUB),
    THREE_CLUB(Power.THREE, Suit.CLUB),
    FOUR_CLUB(Power.FOUR, Suit.CLUB),
    FIVE_CLUB(Power.FIVE, Suit.CLUB),
    SIX_CLUB(Power.SIX, Suit.CLUB),
    SEVEN_CLUB(Power.SEVEN, Suit.CLUB),
    EIGHT_CLUB(Power.EIGHT, Suit.CLUB),
    NINE_CLUB(Power.NINE, Suit.CLUB),
    TEN_CLUB(Power.TEN, Suit.CLUB),
    JACK_CLUB(Power.JACK, Suit.CLUB),
    QEEAN_CLUB(Power.QEEAN, Suit.CLUB),
    KING_CLUB(Power.KING, Suit.CLUB),
    ACE_CLUB(Power.ACE, Suit.CLUB),

    TWO_SPADE(Power.TWO, Suit.SPADE),
    THREE_SPADE(Power.THREE, Suit.SPADE),
    FOUR_SPADE(Power.FOUR, Suit.SPADE),
    FIVE_SPADE(Power.FIVE, Suit.SPADE),
    SIX_SPADE(Power.SIX, Suit.SPADE),
    SEVEN_SPADE(Power.SEVEN, Suit.SPADE),
    EIGHT_SPADE(Power.EIGHT, Suit.SPADE),
    NINE_SPADE(Power.NINE, Suit.SPADE),
    TEN_SPADE(Power.TEN, Suit.SPADE),
    JACK_SPADE(Power.JACK, Suit.SPADE),
    QEEAN_SPADE(Power.QEEAN, Suit.SPADE),
    KING_SPADE(Power.KING, Suit.SPADE),
    ACE_SPADE(Power.ACE, Suit.SPADE);

    Card(Power power, Suit suit) {
        this.power = power;
        this.suit = suit;
    }

    private Suit suit;

    private Power power;

    public Suit getSuit() {
        return suit;
    }

    public Power getPower() {
        return power;
    }

}
