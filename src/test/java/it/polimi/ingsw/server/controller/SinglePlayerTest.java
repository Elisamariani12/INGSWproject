package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.server.model.gamemanager.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SinglePlayerTest {
    private SinglePlayer gameMode;
    @BeforeEach
    void setUp() {
        CardRepository.getInstance().loadAllData();
        gameMode = new SinglePlayer();
        gameMode.setupGame();
        Player player = new Player("user");
        gameMode.addPlayerToSession(player);
    }


    @Test
    void tryAdvance() {
        gameMode.tryAdvance(gameMode.getModel().getGameSession().getPlayerByIndex(0), 18 );
        assertTrue(gameMode.getModel().getGameSession().getPlayerByIndex(0).getBoard().getFaithTrack().getPopeFavour(1));
        assertTrue(gameMode.getModel().getGameSession().getPlayerByIndex(0).getBoard().getFaithTrack().getPopeFavour(2));

        for(int i=0; i<50; i++){
            gameMode.pickActionToken();
        }
        if(gameMode.getModel().getGameSession().getLorenzoDeMedici().getFaithTrackPosition()>=24){
            gameMode.tryAdvance(gameMode.getModel().getGameSession().getPlayerByIndex(0), 18 );
            assertFalse(gameMode.getModel().getGameSession().getPlayerByIndex(0).getBoard().getFaithTrack().getPopeFavour(3));
        }
    }


    @Test
    void pickActionToken() {
    }

    @Test
    void calculateScores() {
        Player p=gameMode.getModel().getGameSession().getPlayerByIndex(0);
        gameMode.tryAdvance(p,26);
        gameMode.calculateScores();

        //System.out.println("player has reached"+gameMode.getModel().getGameSession().getOrderedScoreBoard());
        assertEquals(gameMode.getModel().getGameSession().getOrderedScoreBoard().size(), 1);
        assertEquals(p, gameMode.getModel().getGameSession().getOrderedScoreBoard().keySet().iterator().next());
    }
}