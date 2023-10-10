package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.server.model.gamemanager.Player;
import it.polimi.ingsw.server.model.gamemanager.MVCModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MultiPlayerTest {

    private MVCModel model;
    private MultiPlayer gameModeMultiPlayer;

    @BeforeEach
    void setUp() {
        CardRepository.getInstance().loadAllData();
        gameModeMultiPlayer = new MultiPlayer();
    }

    @Test
    void setupGame() {
        Player p = new Player("A");
        Player p2 = new Player("B");
        gameModeMultiPlayer.addPlayerToSession(p);
        gameModeMultiPlayer.addPlayerToSession(p2);
        gameModeMultiPlayer.setupGame();

        assertTrue(gameModeMultiPlayer.getModel().getGameSession().getPlayerCount()==2);
        assertTrue(gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getFaithTrack().getPosition()==0);

    }

    @Test
    void distributeInitialResources() {
        Player p = new Player("A");
        Player p2 = new Player("B");
        Player p3 = new Player("C");
        Player p4 = new Player("D");
        gameModeMultiPlayer.addPlayerToSession(p);
        gameModeMultiPlayer.addPlayerToSession(p2);
        gameModeMultiPlayer.addPlayerToSession(p3);
        gameModeMultiPlayer.addPlayerToSession(p4);
        gameModeMultiPlayer.setupGame();
        ArrayList<Resource> list= new ArrayList<Resource>();list.add(Resource.STONE);
        ArrayList<Resource> list2= new ArrayList<Resource>();list2.add(Resource.STONE);list2.add(Resource.COIN);
        ArrayList<Resource> list0= new ArrayList<>();
        gameModeMultiPlayer.distributeInitialResources(0,list0);
        gameModeMultiPlayer.distributeInitialResources(1,list0);
        gameModeMultiPlayer.distributeInitialResources(2,list);
        gameModeMultiPlayer.distributeInitialResources(3,list2);

        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(0).getBoard().getFaithTrack().getPosition(), 0);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(1).getBoard().getFaithTrack().getPosition(), 0);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(2).getBoard().getFaithTrack().getPosition(), 1);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(3).getBoard().getFaithTrack().getPosition(), 1);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(0).getBoard().getTotalStoredAmount(Resource.COIN), 0);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(1).getBoard().getTotalStoredAmount(Resource.COIN), 0);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(2).getBoard().getTotalStoredAmount(Resource.COIN), 0);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(3).getBoard().getTotalStoredAmount(Resource.COIN), 1);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(0).getBoard().getTotalStoredAmount(Resource.STONE), 0);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(1).getBoard().getTotalStoredAmount(Resource.STONE), 0);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(2).getBoard().getTotalStoredAmount(Resource.STONE), 1);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(3).getBoard().getTotalStoredAmount(Resource.STONE), 1);

    }

    @Test
    void tryAdvanceANDvaticanReport() {
        Player p = new Player("A");
        Player p2 = new Player("B");
        gameModeMultiPlayer.addPlayerToSession(p);
        gameModeMultiPlayer.addPlayerToSession(p2);
        ArrayList<Resource> list0= new ArrayList<>();
        gameModeMultiPlayer.distributeInitialResources(0,list0);
        gameModeMultiPlayer.distributeInitialResources(1,list0);

        gameModeMultiPlayer.tryAdvance(p,7);
        gameModeMultiPlayer.tryAdvance(p2,8);
        assertTrue(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(0).getBoard().getFaithTrack().getPopeFavour(1));
        assertTrue(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(1).getBoard().getFaithTrack().getPopeFavour(1));
        gameModeMultiPlayer.tryAdvance(p,1);
        gameModeMultiPlayer.tryAdvance(p,8);
        assertTrue(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(0).getBoard().getFaithTrack().getPopeFavour(2));
        assertFalse(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(1).getBoard().getFaithTrack().getPopeFavour(2));
        gameModeMultiPlayer.tryAdvance(p,20);
        assertTrue(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(0).getBoard().getFaithTrack().getPopeFavour(3));
        assertFalse(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(1).getBoard().getFaithTrack().getPopeFavour(3));
    }

    @Test
    void calculateScores() {
        Player p = new Player("A");
        Player p2 = new Player("B");
        gameModeMultiPlayer.addPlayerToSession(p);
        gameModeMultiPlayer.addPlayerToSession(p2);
        ArrayList<Resource> list0= new ArrayList<>();
        gameModeMultiPlayer.distributeInitialResources(0,list0);
        gameModeMultiPlayer.distributeInitialResources(1,list0);

        gameModeMultiPlayer.tryAdvance(p,10);
        gameModeMultiPlayer.tryAdvance(p2,26);

        gameModeMultiPlayer.calculateScores();

        //System.out.println("player has reached"+gameModeMultiPlayer.getModel().getGameSession().getOrderedScoreBoard());

        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getOrderedScoreBoard().size(), 2);
        if(gameModeMultiPlayer.getModel().getGameSession().getOrderedScoreBoard().keySet().stream().findFirst().isPresent()){
            assertSame(gameModeMultiPlayer.getModel().getGameSession().getOrderedScoreBoard().keySet().iterator().next(), p2);
        }
    }
}