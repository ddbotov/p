package ru.botov.poker.action;

import ru.botov.poker.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Analizer {

    public Set<Player> whoWins(Table table) {
        EnumSet<Card> topHand = null;
        Set<Player> topPlayers = new HashSet<>();
        for (Player player : table.getPlayers()) {
            EnumSet<Card> currentHand = EnumSet.copyOf(player.getCards());
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
        this.cache = new HashMap<>();
        BigDecimal chanceToWin = new BigDecimal(1);

        BigDecimal otherPlayersInGame = new BigDecimal(table.getPlayers().size() - 1);//if i am in game
        EnumSet<Card> myCards = getMyCards(table);

        BigDecimal chanceToLose = getChanceToLose(table.getCards().isEmpty() ? EnumSet.noneOf(Card.class) : EnumSet.copyOf(table.getCards()), myCards, otherPlayersInGame);
        chanceToWin = chanceToWin.subtract(chanceToLose);
        return chanceToWin;
    }

    private BigDecimal getChanceToLose(EnumSet<Card> tableCards, EnumSet<Card> myCards, BigDecimal otherPlayersInGame) {
        BigDecimal chanceToLose = new BigDecimal(0);
        if (tableCards.size() == 5) {
            long betterThanMeHandsCount = betterThanMeHandsCount(tableCards, myCards);
            BigDecimal chanceForHand = chanceForHand(Stage.RIVER.getRemainingSizeOfDeck());
            chanceToLose = chanceToLose.add(chanceForHand.multiply(otherPlayersInGame).multiply(new BigDecimal(betterThanMeHandsCount)));
        } else {
            EnumSet<Card> myAndTableCards = EnumSet.copyOf(tableCards);
            myAndTableCards.addAll(myCards);
            List<Card> remainingCards = getRemainingCards(myAndTableCards);

            BigDecimal oneMoreCardChance = getRemaningCardChance(remainingCards.size());
            for (Card remainingCard : remainingCards) {
                EnumSet<Card> potentialTableCards = EnumSet.copyOf(tableCards);
                potentialTableCards.add(remainingCard);
                chanceToLose.add(getChanceToLose(potentialTableCards, myCards, otherPlayersInGame).multiply(oneMoreCardChance));
            }
        }
        return chanceToLose;
    }

    private static final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    private BigDecimal getRemaningCardChance(int remainingSizeOfDeck) {
        //TODO cache result for perfomance
        return new BigDecimal(1)
                .divide(new BigDecimal(remainingSizeOfDeck), roundingMode);
    }

    //две карты. Одна из них будет получена из коллоды с n карт, 2-я с n-1 карт
    private BigDecimal chanceForHand(int remainingSizeOfDeck) {
        return getRemaningCardChance(remainingSizeOfDeck).add(getRemaningCardChance(remainingSizeOfDeck-1));
    }

    private EnumSet<Card> getMyCards(Table table) {
        for (Player player : table.getPlayers()) {
            if (player.isMe()) {
                return EnumSet.copyOf(player.getCards());
            }
        }
        return null;
    }

    Map<EnumSet<Card>, Long> cache = new HashMap<>();

    private long betterThanMeHandsCount(EnumSet<Card> tableCards, EnumSet<Card> myCards) {
        EnumSet<Card> myAndTableCards = EnumSet.copyOf(tableCards);
        myAndTableCards.addAll(myCards);
        BigDecimal myPower = Combination.getPower(myAndTableCards);

        Long valueFromCache = cache.get(myAndTableCards);
        if (valueFromCache != null) {
            return valueFromCache;
        }

        List<Card> remainingCards = getRemainingCards(myAndTableCards);
        long betterThanMeHandsCount = 0L;
        for (int index1=0; index1<remainingCards.size()-1; index1++) {
            Card card1 = remainingCards.get(index1);
            for (int index2=index1+1; index2<remainingCards.size(); index2++) {
                Card card2 = remainingCards.get(index2);
                EnumSet<Card> potentialHand = EnumSet.copyOf(tableCards);
                potentialHand.add(card1);
                potentialHand.add(card2);
                BigDecimal handPower = Combination.getPower(potentialHand);
                if (handPower.compareTo(myPower) > 0) {
                    betterThanMeHandsCount++;
                }
            }
        }
        cache.put(myAndTableCards, betterThanMeHandsCount);
        return betterThanMeHandsCount;
    }

    private static final List<Card> ALL_CARDS = Arrays.asList(Card.values());

    private List<Card> getRemainingCards(EnumSet<Card> myAndTableCards) {
        List<Card> remainingCards = new ArrayList<>(ALL_CARDS);
        remainingCards.removeAll(myAndTableCards);
        return remainingCards;
    }

}
