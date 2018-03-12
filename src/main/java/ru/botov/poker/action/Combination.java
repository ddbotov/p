package ru.botov.poker.action;

import ru.botov.poker.model.Card;
import ru.botov.poker.model.Power;
import ru.botov.poker.model.Suit;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public enum Combination {

    STRAIGHT_FLUSH(new Function<Set<Card>, BigDecimal>() {
        @Override
        public BigDecimal apply(Set<Card> cards) {
            Set<Card> suitGroup = getFlushGroup(cards);
            if (suitGroup != null) {
                BigDecimal straightPower = STRAIGHT.getPowerInternal.apply(suitGroup);
                if (straightPower != NONE_POWER) {
                    BigDecimal result = straightPower;
                    result = result.multiply(COMBINATION_POWER_STEP);
                    result = result.multiply(COMBINATION_POWER_STEP);
                    result = result.multiply(COMBINATION_POWER_STEP);
                    result = result.multiply(COMBINATION_POWER_STEP);
                    return result;
                }
            }
            return NONE_POWER;
        }
    }),
    FOUR(new Function<Set<Card>, BigDecimal>() {
        @Override
        public BigDecimal apply(Set<Card> cards) {
            List<Card> sortedCards = getSortedDescCards(cards);
            Power repeatedPower = getRepeatPowerAndFilterCards(4, sortedCards);
            if (repeatedPower != null) {
                BigDecimal result = new BigDecimal(repeatedPower.ordinal());
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.add(getTopPower(1, sortedCards));
                return result;
            }
            return NONE_POWER;
        }
    }),
    FULL_HOUSE(new Function<Set<Card>, BigDecimal>() {
        @Override
        public BigDecimal apply(Set<Card> cards) {
            List<Card> sortedCards = getSortedDescCards(cards);
            Power repeatedPower1 = getRepeatPowerAndFilterCards(3, sortedCards);
            Power repeatedPower2 = getRepeatPowerAndFilterCards(2, sortedCards);
            if (repeatedPower1 != null && repeatedPower2 != null) {
                BigDecimal result = new BigDecimal(repeatedPower1.ordinal());
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.add(new BigDecimal(repeatedPower2.ordinal()));
                return result;
            }
            return NONE_POWER;
        }
    }),
    FLUSH(new Function<Set<Card>, BigDecimal>() {
        @Override
        public BigDecimal apply(Set<Card> cards) {
            Set<Card> suitGroup = getFlushGroup(cards);
            if (suitGroup != null) {
                return getFlushPower(suitGroup);
            }
            return NONE_POWER;
        }
    }),
    STRAIGHT(new Function<Set<Card>, BigDecimal>() {
        @Override
        public BigDecimal apply(Set<Card> cards) {
            List<Card> sortedCards = getSortedDescCards(cards);
            Card straightTopCard = sortedCards.get(0);
            int straightLenght = 0;
            for (int index=1; index<sortedCards.size(); index++) {
                Card card = sortedCards.get(index);
                if (straightTopCard.getPower().ordinal()-card.getPower().ordinal() == ++straightLenght) {
                    if (straightLenght == 5) {
                        return straightPowerFrom(straightTopCard);
                    }
                    if (straightTopCard.getPower() == Power.FIVE
                            && straightLenght == 4
                            && sortedCards.get(0).getPower() == Power.ACE) {
                        return straightPowerFrom(straightTopCard);
                    }
                } else {
                    straightTopCard = card;
                    straightLenght=0;
                }
            }
            return NONE_POWER;
        }
    }),
    THREE(new Function<Set<Card>, BigDecimal>() {
        @Override
        public BigDecimal apply(Set<Card> cards) {
            List<Card> sortedCards = getSortedDescCards(cards);
            Power repeatedPower = getRepeatPowerAndFilterCards(3, sortedCards);
            if (repeatedPower != null) {
                BigDecimal result = new BigDecimal(repeatedPower.ordinal());
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.add(getTopPower(2, sortedCards));
                return result;
            }
            return NONE_POWER;
        }
    }),
    TWO_PAIR(new Function<Set<Card>, BigDecimal>() {
        @Override
        public BigDecimal apply(Set<Card> cards) {
            List<Card> sortedCards = getSortedDescCards(cards);
            Power repeatedPower1 = getRepeatPowerAndFilterCards(2, sortedCards);
            Power repeatedPower2 = getRepeatPowerAndFilterCards(2, sortedCards);
            if (repeatedPower1 != null && repeatedPower2 != null) {
                BigDecimal result = new BigDecimal(repeatedPower1.ordinal());
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.multiply(COMBINATION_POWER_STEP);
                BigDecimal repeatedPower2Result = new BigDecimal(repeatedPower2.ordinal());
                repeatedPower2Result = repeatedPower2Result.multiply(COMBINATION_POWER_STEP);
                result = result.add(repeatedPower2Result);
                result = result.add(getTopPower(1, sortedCards));
                return result;
            }
            return NONE_POWER;
        }
    }),
    PAIR(new Function<Set<Card>, BigDecimal>() {
        @Override
        public BigDecimal apply(Set<Card> cards) {
            List<Card> sortedCards = getSortedDescCards(cards);
            Power pairPower = getRepeatPowerAndFilterCards(2, sortedCards);
            if (pairPower != null) {
                BigDecimal result = new BigDecimal(pairPower.ordinal());
                result = result.multiply(COMBINATION_POWER_STEP);
                result = result.add(getTopPower(3, sortedCards));
                return result;
            }
            return NONE_POWER;
        }
    }),
    TOP(new Function<Set<Card>, BigDecimal>() {

        @Override
        public BigDecimal apply(Set<Card> cards) {
            return getTopPower(5, getSortedDescCards(cards));
        }
    });

    private Function<Set<Card>, BigDecimal> getPowerInternal;
    private static final BigDecimal COMBINATION_POWER_STEP = new BigDecimal(1_000_000_000_000L);
    private static final BigDecimal NONE_POWER = new BigDecimal(0);
    private static final BigDecimal TOP_POWER_STEP = new BigDecimal(100);

    Combination(Function<Set<Card>, BigDecimal> getPowerInternal) {
        this.getPowerInternal = getPowerInternal;
    }

    public static BigDecimal getPower(Set<Card> cards) {
        return NONE_POWER;//TODO remove
/*        if (cards == null) {
            return NONE_POWER;
        }
        for (Combination hand : values()) {
            BigDecimal value = hand.getPowerInternal.apply(cards);
            if (value.compareTo(NONE_POWER) > 0) {
                return value;
            }
        }
        return NONE_POWER;*/
    }

    private static BigDecimal getFlushPower(Set<Card> suitGroup) {
        List<Card> sortedSuitGroup = getSortedDescCards(suitGroup);
        BigDecimal result = new BigDecimal(sortedSuitGroup.get(0).getPower().ordinal());
        result = result.multiply(COMBINATION_POWER_STEP);
        result = result.multiply(COMBINATION_POWER_STEP);
        result = result.multiply(COMBINATION_POWER_STEP);
        result = result.multiply(COMBINATION_POWER_STEP);
        result = result.multiply(COMBINATION_POWER_STEP);
        return result;
    }

    private static Set<Card> getFlushGroup(Set<Card> cards) {
        Map<Suit, Set<Card>> suitGroups = new HashMap<>();
        for (Card card : cards) {
            Set<Card> suitGroup = suitGroups.get(card.getSuit());
            if (suitGroup == null) {
                suitGroup = new HashSet<>();
                suitGroups.put(card.getSuit(), suitGroup);
            }
            suitGroup.add(card);
            if (suitGroup.size() >= 5) {
                return suitGroup;
            }
        }
        return null;
    }

    private static BigDecimal straightPowerFrom(Card straightTopCard) {
        BigDecimal result = new BigDecimal(straightTopCard.getPower().ordinal());
        result = result.multiply(COMBINATION_POWER_STEP);
        result = result.multiply(COMBINATION_POWER_STEP);
        result = result.multiply(COMBINATION_POWER_STEP);
        result = result.multiply(COMBINATION_POWER_STEP);
        return result;
    }

    private static Power getRepeatPowerAndFilterCards(int repeatCardCount, List<Card> sortedCards) {
        Card lastCard = sortedCards.get(0);
        Set<Card> cardsToFilter = new HashSet<>();
        cardsToFilter.add(lastCard);
        for (int index=1; index<sortedCards.size(); index++) {
            Card card = sortedCards.get(index);
            cardsToFilter.add(card);
            if (lastCard.getPower().ordinal() == card.getPower().ordinal()) {
                if (cardsToFilter.size() == repeatCardCount) {
                    sortedCards.removeAll(cardsToFilter);
                    return lastCard.getPower();
                }
            } else {
                cardsToFilter = new HashSet<>();
                lastCard = card;
            }
        }
        return null;
    }

    private static BigDecimal getTopPower(int topCardsCount, List<Card> sortedCards) {
        sortedCards = sortedCards.subList(0, topCardsCount);
        BigDecimal power = new BigDecimal(0);
        int cardPowerIndex = topCardsCount-1;
        for (Card card : sortedCards) {
            BigDecimal cardValue = new BigDecimal(card.getPower().ordinal());
            for (int index=0; index<cardPowerIndex; index++) {
                cardValue = cardValue.multiply(TOP_POWER_STEP);
            }
            power = power.add(cardValue);
            cardPowerIndex--;
        }
        return power;
    }

    private static Comparator<? super Card> sortedDescComparator = new Comparator<Card>() {
        @Override
        public int compare(Card o1, Card o2) {
            return o2.getPower().ordinal() - o1.getPower().ordinal();
        }
    };

    //Возвращает отсортированнй по убыванию список
    private static ArrayList<Card> getSortedDescCards(Set<Card> cards) {
        ArrayList<Card> sortedCards = new ArrayList<Card>(cards);
        Collections.sort(sortedCards, sortedDescComparator);
        return sortedCards;
    }

}
