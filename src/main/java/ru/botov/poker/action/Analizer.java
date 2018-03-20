package ru.botov.poker.action;

import ru.botov.poker.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Analizer {

    public BigDecimal getChanceToWin(Table table) {
        this.cache = new HashMap<>();
        BigDecimal chanceToWin = new BigDecimal(1);

        BigDecimal otherPlayersInGame = new BigDecimal(table.getPlayers().size() - 1);//if i am in game
        EnumSet<Card> myCards = getMyCards(table);

        BigDecimal chanceToLose = getChanceToLose(table.getCards().isEmpty() ? EnumSet.noneOf(Card.class) : EnumSet.copyOf(table.getCards()), myCards, otherPlayersInGame);
        chanceToWin = chanceToWin.subtract(chanceToLose);
        return chanceToWin;
    }

    private BigDecimal chanceForHandOnRiver = chanceForHand(Stage.RIVER.getRemainingSizeOfDeck());
    private BigDecimal threeMoreCardChanceOnPreFlop = getRemaningCardChance(Stage.PREFLOP.getRemainingSizeOfDeck())
            .multiply(getRemaningCardChance(Stage.PREFLOP.getRemainingSizeOfDeck()-1))
            .multiply(getRemaningCardChance(Stage.PREFLOP.getRemainingSizeOfDeck()-2));
    private BigDecimal oneMoreCardChanceOnFlop = getRemaningCardChance(Stage.FLOP.getRemainingSizeOfDeck());
    private BigDecimal oneMoreCardChanceOnTurn = getRemaningCardChance(Stage.TURN.getRemainingSizeOfDeck());

    private static final BigDecimal ZERO = new BigDecimal(0);

    private BigDecimal getChanceToLose(EnumSet<Card> tableCards, EnumSet<Card> myCards, BigDecimal otherPlayersInGame) {
        BigDecimal chanceToLose = ZERO;
        if (tableCards.size() == 5) {
            long betterThanMeHandsCount = betterThanMeHandsCount(tableCards, myCards);
            chanceToLose = chanceToLose.add(new BigDecimal(betterThanMeHandsCount).multiply(chanceForHandOnRiver).multiply(otherPlayersInGame));
        } else {
            if (tableCards.size() == 0) {//preflop
                ArrayList<Card> remainingCards = new ArrayList<>(ALL_CARDS);
                remainingCards.removeAll(myCards);
                for (int index1=0; index1<remainingCards.size()-1; index1++) {
                    Card card1 = remainingCards.get(index1);
                    for (int index2 = index1 + 1; index2 < remainingCards.size(); index2++) {
                        Card card2 = remainingCards.get(index2);
                        for (int index3 = index2 + 1; index3 < remainingCards.size(); index3++) {
                            Card card3 = remainingCards.get(index3);
                            EnumSet<Card> potentialTableCards = EnumSet.of(card1, card2, card3);
                            chanceToLose = chanceToLose.add(getChanceToLose(potentialTableCards, myCards, otherPlayersInGame).multiply(threeMoreCardChanceOnPreFlop));
                        }
                    }
                }
            } else {
                EnumSet<Card> remainingCards = EnumSet.copyOf(ALL_CARDS);
                remainingCards.removeAll(tableCards);
                remainingCards.removeAll(myCards);
                BigDecimal oneMoreCardChance = tableCards.size() == 3 ? oneMoreCardChanceOnFlop : oneMoreCardChanceOnTurn;
                for (Card remainingCard : remainingCards) {
                    EnumSet<Card> potentialTableCards = EnumSet.copyOf(tableCards);
                    potentialTableCards.add(remainingCard);
                    chanceToLose = chanceToLose.add(getChanceToLose(potentialTableCards, myCards, otherPlayersInGame).multiply(oneMoreCardChance));
                }
            }
        }
        return chanceToLose;
    }

    private static final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    private BigDecimal getRemaningCardChance(int remainingSizeOfDeck) {
        //TODO cache result for perfomance
        return new BigDecimal("1.00000000000000000000000")
                .divide(new BigDecimal(remainingSizeOfDeck), roundingMode);
    }

    //две карты. Одна из них будет получена из коллоды с n карт, 2-я с n-1 карт
    private BigDecimal chanceForHand(int remainingSizeOfDeck) {
        return getRemaningCardChance(remainingSizeOfDeck).multiply(getRemaningCardChance(remainingSizeOfDeck-1));
    }

    private EnumSet<Card> getMyCards(Table table) {
        for (Player player : table.getPlayers()) {
            if (player.isMe()) {
                return EnumSet.copyOf(player.getCards());
            }
        }
        return null;
    }

    private Map<EnumSet<Card>, Long> cache = new HashMap<>();

    private long betterThanMeHandsCount(EnumSet<Card> tableCards, EnumSet<Card> myCards) {
        EnumSet<Card> myAndTableCards = EnumSet.copyOf(tableCards);
        myAndTableCards.addAll(myCards);

        Long valueFromCache = cache.get(myAndTableCards);
        if (valueFromCache != null) {
            return valueFromCache;
        }

        StepPower myStepPower = Combination.getPower(myAndTableCards);
        ArrayList<Card> remainingCards = new ArrayList<>(ALL_CARDS);
        remainingCards.removeAll(myAndTableCards);
        long betterThanMeHandsCount = 0L;
        for (int index1=0; index1<remainingCards.size()-1; index1++) {
            Card card1 = remainingCards.get(index1);
            for (int index2=index1+1; index2<remainingCards.size(); index2++) {
                Card card2 = remainingCards.get(index2);
                EnumSet<Card> potentialHand = EnumSet.copyOf(tableCards);
                potentialHand.add(card1);
                potentialHand.add(card2);
                if (Combination.isBetterHand(potentialHand, myStepPower)) {
                    betterThanMeHandsCount++;
                }
            }
        }
        cache.put(myAndTableCards, betterThanMeHandsCount);
        return betterThanMeHandsCount;
    }

    private static final EnumSet<Card> ALL_CARDS = EnumSet.copyOf(Arrays.asList(Card.values()));

}
