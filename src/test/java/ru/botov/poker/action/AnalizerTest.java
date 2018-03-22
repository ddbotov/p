package ru.botov.poker.action;

import org.junit.Test;
import org.testng.Assert;
import ru.botov.poker.model.Card;
import ru.botov.poker.model.Player;
import ru.botov.poker.model.Stage;
import ru.botov.poker.model.Table;
import ru.botov.poker.utils.SortUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;

public class AnalizerTest {

    @Test
    public void testGetChanceToWinOnRiver() {
        Analizer analizer = new Analizer();
        Table table = new Table();
        table.setStage(Stage.RIVER);
        table.getCards().add(Card.ACE_HEART);
        table.getCards().add(Card.ACE_SPADE);
        table.getCards().add(Card.EIGHT_SPADE);
        table.getCards().add(Card.JACK_HEART);
        table.getCards().add(Card.THREE_DIAMOND);

        Player me = new Player();
        me.setMe(true);
        me.getCards().add(Card.ACE_CLUB);
        me.getCards().add(Card.ACE_DIAMOND);
        table.getPlayers().add(me);

        Player player1 = new Player();
        table.getPlayers().add(player1);

        BigDecimal chanceToWin = analizer.getChanceToWin(table);
        Assert.assertTrue(new BigDecimal(1).compareTo(chanceToWin) == 0);
    }

    @Test
    public void testGetChanceToWinOnTurn() {
        Analizer analizer = new Analizer();
        Table table = new Table();
        table.setStage(Stage.TURN);
        table.getCards().add(Card.ACE_HEART);
        table.getCards().add(Card.ACE_SPADE);
        table.getCards().add(Card.EIGHT_SPADE);
        table.getCards().add(Card.JACK_HEART);

        Player me = new Player();
        me.setMe(true);
        me.getCards().add(Card.ACE_CLUB);
        me.getCards().add(Card.ACE_DIAMOND);
        table.getPlayers().add(me);

        Player player1 = new Player();
        table.getPlayers().add(player1);

        BigDecimal chanceToWin = analizer.getChanceToWin(table);
        Assert.assertTrue(new BigDecimal(1).compareTo(chanceToWin) == 0);
    }

    @Test
    public void testGetChanceToWinOnFlop() {
        Analizer analizer = new Analizer();
        Table table = new Table();
        table.setStage(Stage.FLOP);
/*        table.getCards().add(Card.ACE_HEART);
        table.getCards().add(Card.ACE_SPADE);
        table.getCards().add(Card.EIGHT_SPADE);*/
        table.getCards().add(Card.KING_CLUB);
        table.getCards().add(Card.KING_SPADE);
        table.getCards().add(Card.EIGHT_SPADE);

        Player me = new Player();
        me.setMe(true);
        me.getCards().add(Card.ACE_CLUB);
        me.getCards().add(Card.ACE_DIAMOND);
        table.getPlayers().add(me);

        Player player1 = new Player();
        table.getPlayers().add(player1);

        BigDecimal chanceToWin = analizer.getChanceToWin(table);
        Assert.assertEquals(new BigDecimal("0.9523183"), chanceToWin.round(MathContext.DECIMAL32));
    }

/*    @Test
    public void testGetChanceToWinOnFlopV2ToDelete() {
        Analizer analizer = new Analizer();
        Table table = new Table();
        table.setStage(Stage.FLOP);
        table.getCards().add(Card.ACE_HEART);
        //table.getCards().add(Card.ACE_SPADE);
        //table.getCards().add(Card.EIGHT_SPADE);

        Player me = new Player();
        me.setMe(true);
        me.getCards().add(Card.ACE_CLUB);
        me.getCards().add(Card.ACE_DIAMOND);
        table.getPlayers().add(me);

        Player player1 = new Player();
        table.getPlayers().add(player1);

        BigDecimal chanceToWin = analizer.getChanceToWin(table);
        Assert.assertEquals(new BigDecimal(1), chanceToWin);
    }*/

    @Test
    public void testGetChanceToWinOnPreFlop() {
        Analizer analizer = new Analizer();
        Table table = new Table();
        table.setStage(Stage.PREFLOP);

        Player me = new Player();
        me.setMe(true);
        me.getCards().add(Card.ACE_CLUB);
        me.getCards().add(Card.ACE_DIAMOND);
        table.getPlayers().add(me);

        Player player1 = new Player();
        table.getPlayers().add(player1);

        BigDecimal chanceToWin = analizer.getChanceToWin(table);
        Assert.assertEquals(new BigDecimal("85.3"), chanceToWin);
    }

    @Test
    public void testGetChanceToWinOnAllPreFlop() {
        Analizer analizer = new Analizer();
        Table table = new Table();
        table.setStage(Stage.PREFLOP);

        Player me = new Player();
        me.setMe(true);

        int counter = 9*52*51/2;
        try {
            List<Card> allCards = Arrays.asList(Card.values());
            for (int index1=0; index1<allCards.size()-1; index1++) {
                Card card1 = allCards.get(index1);
                for (int index2=index1+1; index2<allCards.size(); index2++) {
                    Card card2 = allCards.get(index2);
                    me.getCards().clear();
                    me.getCards().add(card1);
                    me.getCards().add(card2);

                    table.getPlayers().clear();
                    table.getPlayers().add(me);
                    for (int playersCounter = 1; playersCounter < 10; playersCounter++) {
                        Player player = new Player();
                        table.getPlayers().add(player);
                        BigDecimal chanceToWin = analizer.getChanceToWin(table);
                        Assert.assertNotNull(chanceToWin);
                        counter--;
                    }
                }
            }
        } catch (Throwable e) {
            System.out.println(me.getCards());
            throw e;
        }
        Assert.assertEquals(0, counter);
    }

}
