package ru.botov.poker.action;

import ru.botov.poker.model.Card;
import ru.botov.poker.model.Power;
import ru.botov.poker.model.StepPower;
import ru.botov.poker.model.Suit;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public class Combination {

    public static final BigDecimal COMBINATION_POWER_STEP = new BigDecimal(1_000_000_000_000L);
    private static final BigDecimal NONE_POWER = new BigDecimal(0);
    private static final BigDecimal TOP_POWER_STEP = new BigDecimal(100);

    public static boolean isBetterHand(EnumSet<Card> cards, StepPower myStepPower) {
        List<Card> sortedCards = getSortedDescCards(cards);

        BigDecimal result = null;

        ArrayList<Card> sortedCardsCopy = null;
        List<Card> suitGroup = null;

        Power repeatedPower3 = null;

        BigDecimal myPower = myStepPower.getPower();
        switch (myStepPower.getStepPower()) {
            case 0:
                result = getTopPower(5, sortedCards);//TOP
                if (result.compareTo(myPower) > 0) {
                    return true;
                }
            case 1:
            case 2:
                sortedCardsCopy = new ArrayList<>(sortedCards);
                Power repeatedPower1 = getRepeatPowerAndFilterCards(2, sortedCardsCopy);
                if (repeatedPower1 != null) {//TWO
                    result = repeatedPower1.getOrdinalBigDecimal();
                    result = result.multiply(COMBINATION_POWER_STEP);
                    result = result.add(getTopPower(3, sortedCardsCopy));
                    if (result.compareTo(myPower) > 0) {
                        return true;
                    }
                    Power repeatedPower2 = getRepeatPowerAndFilterCards(2, sortedCardsCopy);
                    if (repeatedPower2 != null) {//TWO_PAIRS
                        result = getTwoPairPower(repeatedPower1, repeatedPower2, sortedCardsCopy);
                        if (result.compareTo(myPower) > 0) {
                            return true;
                        }
                    }
                }
            case 3:
                if (sortedCardsCopy != null) {
                    if (sortedCardsCopy.size()<7) {
                        sortedCardsCopy = new ArrayList<>(sortedCards);
                        repeatedPower3 = getRepeatPowerAndFilterCards(3, sortedCardsCopy);
                    }
                } else {
                    sortedCardsCopy = new ArrayList<>(sortedCards);
                    repeatedPower3 = getRepeatPowerAndFilterCards(3, sortedCardsCopy);
                }
                if (repeatedPower3 != null) {//THREE
                    result = getThreePower(repeatedPower3, sortedCardsCopy);
                    if (result.compareTo(myPower) > 0) {
                        return true;
                    }
                }
            case 4:
                BigDecimal straightPower = getStraightPower(sortedCards);
                if (straightPower != NONE_POWER) {//STRAIGHT
                    if (straightPower.compareTo(myPower) > 0) {
                        return true;
                    }
                }
            case 5:
                suitGroup = getFlushGroup(sortedCards);
                if (!suitGroup.isEmpty()) {//FLUSH
                    result = getFlushPower(suitGroup);
                    if (result.compareTo(myPower) > 0) {
                        return true;
                    }
                }
            case 6:
                if (repeatedPower3 == null) {
                    if (sortedCardsCopy != null) {
                        if (sortedCardsCopy.size()<7) {
                            sortedCardsCopy = new ArrayList<>(sortedCards);
                            repeatedPower3 = getRepeatPowerAndFilterCards(3, sortedCardsCopy);
                        }
                    } else {
                        sortedCardsCopy = new ArrayList<>(sortedCards);
                        repeatedPower3 = getRepeatPowerAndFilterCards(3, sortedCardsCopy);
                    }
                }
                if (repeatedPower3 != null) {
                    Power repeatedPower2 = getRepeatPowerAndFilterCards(2, sortedCardsCopy);
                    if (repeatedPower2 != null) {//FULL_HOUSE
                        result = getFullHousePower(repeatedPower3, repeatedPower2);
                        if (result.compareTo(myPower) > 0) {
                            return true;
                        }
                    }
                }
            case 7:
                Power repeatedPower4 = null;
                if (sortedCardsCopy != null) {
                    if (sortedCardsCopy.size()<7) {
                        sortedCardsCopy = new ArrayList<>(sortedCards);
                        repeatedPower4 = getRepeatPowerAndFilterCards(4, sortedCardsCopy);
                    }
                } else {
                    sortedCardsCopy = new ArrayList<>(sortedCards);
                    repeatedPower4 = getRepeatPowerAndFilterCards(4, sortedCardsCopy);
                }
                if (repeatedPower4 != null) {//FOUR
                    result = getFourPower(repeatedPower4, sortedCardsCopy);
                    if (result.compareTo(myPower) > 0) {
                        return true;
                    }
                }
            case 8:
                if (suitGroup == null) {
                    suitGroup = getFlushGroup(sortedCards);
                }
                if (!suitGroup.isEmpty()) {
                    straightPower = getStraightPower(suitGroup);
                    if (straightPower != NONE_POWER) {//STRAIGHT_FLUSH
                        result = getStraightFlushPower(straightPower);
                        if (result.compareTo(myPower) > 0) {
                            return true;
                        }
                    }
                }
        }
        return false;
    }

    public static StepPower getPower(EnumSet<Card> cards) {
        List<Card> sortedCards = getSortedDescCards(cards);

        List<Card> suitGroup = getFlushGroup(sortedCards);
        if (!suitGroup.isEmpty()) {
            BigDecimal straightPower = getStraightPower(suitGroup);
            if (straightPower != NONE_POWER) {//STRAIGHT_FLUSH
                return new StepPower(getStraightFlushPower(straightPower), 8);
            }
        }

        ArrayList<Card> sortedCardsCopy = new ArrayList<>(sortedCards);
        Power repeatedPower = getRepeatPowerAndFilterCards(4, sortedCardsCopy);
        if (repeatedPower != null) {//FOUR
            return new StepPower(getFourPower(repeatedPower, sortedCardsCopy), 7);
        }

        Power repeatedPower3 = getRepeatPowerAndFilterCards(3, sortedCardsCopy);
        if (repeatedPower3 != null) {
            Power repeatedPower2 = getRepeatPowerAndFilterCards(2, sortedCardsCopy);
            if (repeatedPower2 != null) {//FULL_HOUSE
                return new StepPower(getFullHousePower(repeatedPower3, repeatedPower2), 6);
            }
        }

        if (!suitGroup.isEmpty()) {
            return new StepPower(getFlushPower(suitGroup), 5);//FLUSH
        }

        BigDecimal straightPower = getStraightPower(sortedCards);
        if (straightPower != NONE_POWER) {//STRAIGHT
            return new StepPower(straightPower, 4);
        }

        if (repeatedPower3 != null) {//THREE
            return new StepPower(getThreePower(repeatedPower3, sortedCardsCopy), 3);
        } else {
            Power repeatedPower1 = getRepeatPowerAndFilterCards(2, sortedCardsCopy);
            if (repeatedPower1 != null) {
                Power repeatedPower2 = getRepeatPowerAndFilterCards(2, sortedCardsCopy);
                if (repeatedPower2 != null) {//TWO_PAIRS
                    return new StepPower(getTwoPairPower(repeatedPower1, repeatedPower2, sortedCardsCopy), 2);
                } else {//TWO
                    BigDecimal result = repeatedPower1.getOrdinalBigDecimal();
                    result = result.multiply(COMBINATION_POWER_STEP);
                    result = result.add(getTopPower(3, sortedCardsCopy));
                    return new StepPower(result, 1);
                }
            }
        }
        return new StepPower(getTopPower(5, sortedCards), 0);//TOP
    }

    private static BigDecimal twoPairPowerMultiplyer = new BigDecimal(1)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP);

    private static BigDecimal getTwoPairPower(Power repeatedPower1, Power repeatedPower2, List<Card> sortedCardsCopy) {
        BigDecimal result = repeatedPower1.getOrdinalBigDecimal();
        result = result.multiply(twoPairPowerMultiplyer);
        BigDecimal repeatedPower2Result = repeatedPower2.getOrdinalBigDecimal();
        repeatedPower2Result = repeatedPower2Result.multiply(COMBINATION_POWER_STEP);
        result = result.add(repeatedPower2Result);
        result = result.add(getTopPower(1, sortedCardsCopy));
        return result;
    }

    private static BigDecimal threePowerMultiplyer = new BigDecimal(1)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP);

    private static BigDecimal getThreePower(Power repeatedPower3, List<Card> sortedCards) {
        BigDecimal result = repeatedPower3.getOrdinalBigDecimal();
        result = result.multiply(threePowerMultiplyer);
        result = result.add(getTopPower(2, sortedCards));
        return result;
    }

    private static BigDecimal getStraightPower(List<Card> sortedCards) {
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

    private static BigDecimal fullHousePowerMultiplyer = new BigDecimal(1)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP);

    private static BigDecimal getFullHousePower(Power repeatedPower3, Power repeatedPower2) {
        BigDecimal result = repeatedPower3.getOrdinalBigDecimal();
        result = result.multiply(fullHousePowerMultiplyer);
        result = result.add(repeatedPower2.getOrdinalBigDecimal());
        return result;
    }

    private static BigDecimal fourPowerMultiplyer = new BigDecimal(1)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP);

    private static BigDecimal getFourPower(Power repeatedPower, List<Card> sortedCardsCopy) {
        BigDecimal result = repeatedPower.getOrdinalBigDecimal();
        result = result.multiply(fourPowerMultiplyer);
        result = result.add(getTopPower(1, sortedCardsCopy));
        return result;
    }

    private static BigDecimal getStraightFlushPower(BigDecimal straightPower) {
        BigDecimal result = straightPower;
        result = result.multiply(straightPowerMultiplyer);
        return result;
    }

    private static BigDecimal flushPowerMultiplyer = new BigDecimal(1)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP);

    private static BigDecimal getFlushPower(List<Card> sortedSuitGroup) {
        BigDecimal result = sortedSuitGroup.get(0).getPower().getOrdinalBigDecimal();
        result = result.multiply(flushPowerMultiplyer);
        return result;
    }

    private static List<Card> getFlushGroup(List<Card> cards) {
        List<Card> heartGroup = null, diamondGroup = null, clubGroup = null, spadeGroup = null;
        for (Card card : cards) {
            Suit suit = card.getSuit();
            List<Card> suitGroup = null;
            if (suit == Suit.HEART) {
                suitGroup = heartGroup;
            } else if (suit == Suit.DIAMOND) {
                suitGroup = diamondGroup;
            } else if (suit == Suit.CLUB) {
                suitGroup = clubGroup;
            } else if (suit == Suit.SPADE) {
                suitGroup = spadeGroup;
            }
            if (suitGroup == null) {
                suitGroup = new ArrayList<>(5);
                suitGroup.add(card);
            } else {
                suitGroup.add(card);
                if (suitGroup.size() >= 5) {
                    return suitGroup;
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    private static BigDecimal straightPowerMultiplyer = new BigDecimal(1)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP)
            .multiply(COMBINATION_POWER_STEP);

    private static BigDecimal straightPowerFrom(Card straightTopCard) {
        BigDecimal result = straightTopCard.getPower().getOrdinalBigDecimal();
        result = result.multiply(straightPowerMultiplyer);
        return result;
    }

    private static Power getRepeatPowerAndFilterCards(int repeatCardCount, List<Card> sortedCards) {
        Card lastCard = sortedCards.get(0);
        int count = 1;
        for (int index=1; index<sortedCards.size(); index++) {
            Card card = sortedCards.get(index);
            if (lastCard.getPower() == card.getPower()) {
                count++;
                if (count == repeatCardCount) {
                    for (int indexToRemove = index; indexToRemove>index-repeatCardCount; indexToRemove--) {
                        sortedCards.remove(indexToRemove);
                    }
                    return lastCard.getPower();
                }
            } else {
                count = 1;
                lastCard = card;
            }
        }
        return null;
    }

    private static BigDecimal[] bdCache = new BigDecimal[5];
    static {
        bdCache[0] = TOP_POWER_STEP.pow(0);
        bdCache[1] = TOP_POWER_STEP.pow(1);
        bdCache[2] = TOP_POWER_STEP.pow(2);
        bdCache[3] = TOP_POWER_STEP.pow(3);
        bdCache[4] = TOP_POWER_STEP.pow(4);
    }

    private static BigDecimal getTopPower(int topCardsCount, List<Card> sortedCards) {
        BigDecimal power = NONE_POWER;
        for (int index=0; index < topCardsCount; index++) {
            Card card = sortedCards.get(index);
            BigDecimal cardValue = card.getPower().getOrdinalBigDecimal();
            cardValue = cardValue.multiply(bdCache[topCardsCount-index-1]);
            power = power.add(cardValue);
        }
        return power;
    }

    //Возвращает отсортированнй по убыванию список
    private static List<Card> getSortedDescCards(EnumSet<Card> cards) {
        Object[] elementData = cards.toArray();
        bubbleSort(elementData);
        List<Object> sortedCards = Arrays.asList(elementData);
        return (List<Card>) (List) sortedCards;
    }

    public static void bubbleSort(Object[] numArray) {
        int n = numArray.length;
        Object temp = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (((Card) numArray[j - 1]).getPower().ordinal() > ( (Card) numArray[j]).getPower().ordinal()) {
                    temp = numArray[j - 1];
                    numArray[j - 1] = numArray[j];
                    numArray[j] = temp;
                }
            }
        }
    }

}
