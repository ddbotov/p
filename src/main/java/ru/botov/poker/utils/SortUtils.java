package ru.botov.poker.utils;

import ru.botov.poker.model.Card;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.List;

public class SortUtils {

    //Возвращает отсортированнй по убыванию список
    public static List<Card> getSortedDescCards(AbstractCollection<Card> cards) {
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
