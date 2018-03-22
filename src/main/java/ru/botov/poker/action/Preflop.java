package ru.botov.poker.action;

import org.apache.commons.lang3.StringUtils;
import ru.botov.poker.model.Card;
import ru.botov.poker.utils.SortUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class Preflop {

    private static final HashMap<String, BigDecimal[]> PREFLOP_HANDS = new HashMap();

    static {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Preflop.class.getClassLoader().getResourceAsStream("preflop.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s");
                String key = values[0];
                BigDecimal[] chances = new BigDecimal[9];
                int playersCounter = 0;
                for (int i = 1; i<values.length; i++) {
                    String value = values[i];
                    if (StringUtils.isNotBlank(value)) {
                        chances[playersCounter++] = new BigDecimal(value);
                    }

                }
                PREFLOP_HANDS.put(key, chances);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BigDecimal getChanceToWin(EnumSet<Card> myCards, int otherPlayersInGame) {
        String key = getKey(myCards);
        BigDecimal[] preflopHand = PREFLOP_HANDS.get(key);
        return preflopHand[otherPlayersInGame-1];
    }

    private static String getKey(EnumSet<Card> myCards) {
        List<Card> sortedCards = SortUtils.getSortedDescCards(myCards);
        String result = "" + sortedCards.get(0).getPower() + sortedCards.get(1).getPower();
        if (sortedCards.get(0).getSuit() == sortedCards.get(1).getSuit()) {
            result += "s";
        }
        return result;
    }
}
