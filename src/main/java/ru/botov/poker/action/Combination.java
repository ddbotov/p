package ru.botov.poker.action;

import ru.botov.poker.model.Card;
import ru.botov.poker.model.Power;
import ru.botov.poker.model.StepPower;
import ru.botov.poker.model.Suit;
import ru.botov.poker.utils.SortUtils;

import java.math.BigDecimal;
import java.util.*;

public class Combination {

    public static final BigDecimal COMBINATION_POWER_STEP = new BigDecimal(1_000_000_000_000L);
    private static final BigDecimal NONE_POWER = new BigDecimal(0);
    private static final BigDecimal TOP_POWER_STEP = new BigDecimal(100);

    public static boolean isBetterHand(List<Card> sortedCards, StepPower stepPower, boolean canHaveFlush,
                                       boolean canHaveFourOrFullHouse) {

        BigDecimal result = null;

        ArrayList<Card> sortedCardsCopy = null;
        List<Card> suitGroup = null;

        Power repeatedPower3 = null;
        Card straightCard = null;
        Power repeatedPower4 = null;

        BigDecimal myPower = stepPower.getPower();
        int myStep = stepPower.getStep();
        switch (myStep) {
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
                    if (myStep == 0) {
                        return true;
                    }
                    result = repeatedPower1.getOrdinalBigDecimal();
                    result = result.multiply(COMBINATION_POWER_STEP);
                    result = result.add(getTopPower(3, sortedCardsCopy));
                    if (result.compareTo(myPower) > 0) {
                        return true;
                    }
                    Power repeatedPower2 = getRepeatPowerAndFilterCards(2, sortedCardsCopy);
                    if (repeatedPower2 != null) {//TWO_PAIRS
                        if (myStep == 1) {
                            return true;
                        }
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
                    if (myStep == 2) {
                        return true;
                    }
                    result = getThreePower(repeatedPower3, sortedCardsCopy);
                    if (result.compareTo(myPower) > 0) {
                        return true;
                    }
                }
            case 4:
                straightCard = getStraightCard(sortedCards);
                if (straightCard != null) {//STRAIGHT
                    if (myStep == 3) {
                        return true;
                    }
                    BigDecimal straightPower = straightPowerFrom(straightCard);
                    if (straightPower.compareTo(myPower) > 0) {
                        return true;
                    }
                }
            case 5:
                suitGroup = getFlushGroup(sortedCards, canHaveFlush);
                if (suitGroup != null) {//FLUSH
                    if (myStep == 4) {
                        return true;
                    }
                    result = getFlushPower(suitGroup);
                    if (result.compareTo(myPower) > 0) {
                        return true;
                    }
                }
            case 6:
                if (canHaveFourOrFullHouse) {
                    if (myStep > 3) {
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
                            if (myStep == 5) {
                                return true;
                            }
                            result = getFullHousePower(repeatedPower3, repeatedPower2);
                            if (result.compareTo(myPower) > 0) {
                                return true;
                            }
                        }
                    }
                }
            case 7:
                if (canHaveFourOrFullHouse &&
                        straightCard == null && suitGroup == null //в случае стрита или флеша быть не может каре
                       && ( myStep > 3 || repeatedPower3 != null ) //если нет тройки, то и каре быть не может
                       ) {
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
                        if (myStep == 6) {
                            return true;
                        }
                        result = getFourPower(repeatedPower4, sortedCardsCopy);
                        if (result.compareTo(myPower) > 0) {
                            return true;
                        }
                    }
                }
            case 8:
                if (repeatedPower4 != null) {//в случае каре стритфлеша быть не может
                    if (myStep > 4) {
                        suitGroup = getFlushGroup(sortedCards, canHaveFlush);
                    }
                    if (suitGroup != null) {
                        straightCard = getStraightCard(suitGroup);
                        if (straightCard != null) {//STRAIGHT_FLUSH
                            if (myStep == 7) {
                                return true;
                            }
                            BigDecimal straightPower = straightPowerFrom(straightCard);
                            result = getStraightFlushPower(straightPower);
                            if (result.compareTo(myPower) > 0) {
                                return true;
                            }
                        }
                    }
                }
        }
        return false;
    }

    public static StepPower getPower(EnumSet<Card> cards, boolean canHaveFlush, boolean canHaveFourOrFullHouse) {
        List<Card> sortedCards = SortUtils.getSortedDescCards(cards);

        List<Card> suitGroup = getFlushGroup(sortedCards, canHaveFlush);
        if (suitGroup != null) {
            Card straightCard = getStraightCard(suitGroup);
            if (straightCard != null) {//STRAIGHT_FLUSH
                BigDecimal straightPower = straightPowerFrom(straightCard);
                return new StepPower(getStraightFlushPower(straightPower), 8);
            }
        }

        ArrayList<Card> sortedCardsCopy = new ArrayList<>(sortedCards);
        if (canHaveFourOrFullHouse) {
            Power repeatedPower = getRepeatPowerAndFilterCards(4, sortedCardsCopy);
            if (repeatedPower != null) {//FOUR
                return new StepPower(getFourPower(repeatedPower, sortedCardsCopy), 7);
            }
        }

        Power repeatedPower3 = getRepeatPowerAndFilterCards(3, sortedCardsCopy);
        if (canHaveFourOrFullHouse && repeatedPower3 != null) {
            Power repeatedPower2 = getRepeatPowerAndFilterCards(2, sortedCardsCopy);
            if (repeatedPower2 != null) {//FULL_HOUSE
                return new StepPower(getFullHousePower(repeatedPower3, repeatedPower2), 6);
            }
        }

        if (suitGroup != null) {
            return new StepPower(getFlushPower(suitGroup), 5);//FLUSH
        }

        Card straightCard = getStraightCard(sortedCards);
        if (straightCard != null) {//STRAIGHT
            BigDecimal straightPower = straightPowerFrom(straightCard);
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

    private static Card getStraightCard(List<Card> sortedCards) {
        Card straightTopCard = sortedCards.get(0);
        int straightLenght = 0;
        for (int index=1; index<sortedCards.size(); index++) {
            Card card = sortedCards.get(index);
            if (straightTopCard.getPower().ordinal()-card.getPower().ordinal() == ++straightLenght) {
                if (straightLenght == 5) {
                    return straightTopCard;
                }
                if (straightTopCard.getPower() == Power.FIVE
                        && straightLenght == 4
                        && sortedCards.get(0).getPower() == Power.ACE) {
                    return straightTopCard;
                }
            } else {
                straightTopCard = card;
                straightLenght=0;
            }
        }
        return null;
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

    private static List<Card> getFlushGroup(List<Card> sortedCards, boolean canHaveFlush) {
        if (!canHaveFlush) {
            return null;
        }
        return getFlushGroup(sortedCards);
    }

    public static boolean canHaveFlush(EnumSet<Card> tableCards) {
        int heartGroupSize = 0, diamondGroupSize = 0, clubGroupSize = 0, spadeGroupSize = 0;
        for (Card card : tableCards) {
            Suit suit = card.getSuit();
            int suitGroupSize;
            if (suit == Suit.HEART) {
                suitGroupSize = ++heartGroupSize;
            } else if (suit == Suit.DIAMOND) {
                suitGroupSize = ++diamondGroupSize;
            } else if (suit == Suit.CLUB) {
                suitGroupSize = ++clubGroupSize;
            } else {
                suitGroupSize = ++spadeGroupSize;
            }
            if (suitGroupSize == 3) {
                return true;
            }
        }
        return false;
    }

    private static List<Card> getFlushGroup(List<Card> cards) {
        List<Card> heartGroup = null, diamondGroup = null, clubGroup = null, spadeGroup = null;
        for (Card card : cards) {
            Suit suit = card.getSuit();
            List<Card> suitGroup;
            if (suit == Suit.HEART) {
                suitGroup = heartGroup;
            } else if (suit == Suit.DIAMOND) {
                suitGroup = diamondGroup;
            } else if (suit == Suit.CLUB) {
                suitGroup = clubGroup;
            } else {
                suitGroup = spadeGroup;
            }
            if (suitGroup == null) {
                suitGroup = new ArrayList<>(5);
                suitGroup.add(card);
            } else {
                suitGroup.add(card);
                if (suitGroup.size() == 5) {
                    return suitGroup;
                }
            }
        }
        return null;
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

    public static boolean canHaveFourOrFullHouse(Card[] tableCards) {
        for (int i=0; i<tableCards.length-1; i++) {
            if (tableCards[i]==tableCards[i+1]) {
                return true;
            }
        }
        return false;
    }

    public static boolean canHaveStraight(Card[] tableCards) {
        for (int i=0; i<tableCards.length-1; i++) {
            int diff = tableCards[i].getPower().ordinal()-tableCards[i+1].getPower().ordinal();
            if (diff<2) {
                return true;
            }
        }
        return false;
    }
}
