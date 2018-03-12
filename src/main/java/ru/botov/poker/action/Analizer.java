package ru.botov.poker.action;

import ru.botov.poker.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Analizer {

    private List<Set<Card>> hands;

    public Set<Player> whoWins(Table table) {
        Set<Card> topHand = null;
        Set<Player> topPlayers = new HashSet<>();
        for (Player player : table.getPlayers()) {
            Set<Card> currentHand = new HashSet<Card>();
            currentHand.addAll(player.getCards());
            currentHand.addAll(table.getCards());
            BigDecimal currentPower = Combination.getPower(currentHand).subtract(Combination.getPower(topHand));
            if (currentPower.compareTo(new BigDecimal(0)) > 0) {
                topHand = currentHand;
                topPlayers.clear();
                topPlayers.add(player);
            }
            if (currentPower.compareTo(new BigDecimal(0)) == 0) {
                topPlayers.add(player);
            }
        }

        return topPlayers;
    }

    public long howMuchIWin(Table table) {
        Set<Player> winners = whoWins(table);
        boolean iAmWinner = false;
        for (Player winner : winners) {
            if (winner.isMe()) {
                iAmWinner = true;
            }
        }
        if (iAmWinner) {
            return 0L;
        }
        return table.getBank() / winners.size();
    }

    public BigDecimal howMuchRise(Table table) {
        return new BigDecimal(table.getBank()).multiply(getChanceToWin(table));
    }

    public BigDecimal getChanceToWin(Table table) {

        if (table.getStage() == Stage.RIVER) {
            BigDecimal chanceToWin = new BigDecimal(1);
            List<Set<Card>> potentialHands = betterThanMeHands(table.getCards(), getMyCards(table));
            int cardsInDeck = table.getStage().getRemainingSizeOfDeck();
            for (Set<Card> potentialHand : potentialHands) {
                BigDecimal chanceForHand = chanceForHand(cardsInDeck);
                cardsInDeck-=2;
                chanceToWin = chanceToWin.subtract(
                        chanceForHand.multiply(new BigDecimal(table.getPlayers().size() - 1)));//if i am in game
            }
        }
        if (table.getStage() == Stage.TURN) {
            BigDecimal chanceToWin = new BigDecimal(1);
            HashSet<Card> myAndTableCards = new HashSet<>(7);
            myAndTableCards.addAll(table.getCards());
            myAndTableCards.addAll(getMyCards(table));
            List<Set<Card>> potentialHands = getPotentialHands(myAndTableCards);
            int cardsInDeck = table.getStage().getRemainingSizeOfDeck();
            for (Set<Card> potentialHand : potentialHands) {
                BigDecimal chanceForHand = chanceForHand(cardsInDeck);
                cardsInDeck-=2;
                chanceToWin = chanceToWin.subtract(
                        chanceForHand.multiply(new BigDecimal(table.getPlayers().size() - 1)));//if i am in game
            }

            return chanceToWin;
        }
        if (table.getStage() == Stage.FLOP) {
            return new BigDecimal(0);
        }
        if (table.getStage() == Stage.PREFLOP) {
            return new BigDecimal(0);
        }
        return new BigDecimal(0);
    }

    private static final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    //две карты. Одна из них будет получена из коллоды с n карт, 2-я с n-1 карт
    private BigDecimal chanceForHand(int remainingSizeOfDeck) {
        return new BigDecimal(2)
                .divide(new BigDecimal(remainingSizeOfDeck), roundingMode)
                .divide(new BigDecimal(remainingSizeOfDeck-1), roundingMode);
    }

    private Set<Card> getMyCards(Table table) {
        for (Player player : table.getPlayers()) {
            if (player.isMe()) {
                return player.getCards();
            }
        }
        return null;
    }

    private List<Set<Card>> betterThanMeHands(Set<Card> tableCards, Set<Card> myCards) {
        HashSet<Card> myAndTableCards = new HashSet<>(7);
        myAndTableCards.addAll(tableCards);
        myAndTableCards.addAll(myCards);
        BigDecimal myPower = Combination.getPower(myAndTableCards);
        List<Set<Card>> potentialHands = getPotentialHands(myAndTableCards);
        //TODO parallel
        potentialHands = potentialHands.stream().filter(new Predicate<Set<Card>>() {
            @Override
            public boolean test(Set<Card> cards) {
                Set<Card> handAndTableCards = new HashSet<>(7);
                handAndTableCards.addAll(tableCards);
                handAndTableCards.addAll(cards);
                BigDecimal handPower = Combination.getPower(handAndTableCards);
                return handPower.compareTo(myPower) > 0;
            }
        }).collect(Collectors.toList());
        return potentialHands;
    }

    private List<Set<Card>> getPotentialHands(Set<Card> myAndTableCards) {
        List<Card> remainingCards = getRemainingCards(myAndTableCards);
        List<Set<Card>> potentialHands = new LinkedList<>();
        for (int index1=0; index1<remainingCards.size()-1; index1++) {
            Card card1 = remainingCards.get(index1);
            for (int index2=index1+1; index2<remainingCards.size(); index2++) {
                Card card2 = remainingCards.get(index2);
                //potentialHands.add(EnumSet.of(card1, card2));
                HashSet<Card> potentialHand = new HashSet<>(2);
                potentialHand.add(card1);
                potentialHand.add(card2);
                potentialHands.add(potentialHand);
            }
        }
        return potentialHands;
    }

    private static final List<Card> ALL_CARDS = Arrays.asList(Card.values());

    private List<Card> getRemainingCards(Set<Card> myAndTableCards) {
        List<Card> remainingCards = new ArrayList<>(ALL_CARDS);
        remainingCards.removeAll(myAndTableCards);
        return remainingCards;
    }

}
