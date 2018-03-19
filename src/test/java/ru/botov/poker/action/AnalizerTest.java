package ru.botov.poker.action;

import org.junit.Test;
import org.testng.Assert;
import ru.botov.poker.model.Card;
import ru.botov.poker.model.Player;
import ru.botov.poker.model.Stage;
import ru.botov.poker.model.Table;

import java.math.BigDecimal;

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
        Assert.assertEquals(new BigDecimal(1), chanceToWin);
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
        Assert.assertEquals(new BigDecimal(1), chanceToWin);
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
        Assert.assertEquals(new BigDecimal(1), chanceToWin);
    }

    @Test
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
    }

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
        Assert.assertEquals(new BigDecimal(1), chanceToWin);
    }

}
