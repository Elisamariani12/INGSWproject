package it.polimi.ingsw.server.model.gamemanager;

import com.google.gson.Gson;
import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.server.exceptions.PlayerCountOutOfBoundsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameSessionSaveToDiskTest
{
    //Used to read JSON files
    private Gson gson;
    private GameSession gameSession;

    @BeforeEach
    public void start()
    {
        gson = new Gson();
        CardRepository.getInstance().loadAllData();
        gameSession = new GameSession(CardRepository.getInstance().getAllActionTokens(), CardRepository.getInstance().getAllLeaderCards());
        try {
            gameSession.addPlayer(new Player("Player"));
        } catch (PlayerCountOutOfBoundsException e) {
            e.printStackTrace();
        }
        gameSession.startSession();

    }

    @Test
    public void serializeGameSession()
    {
        String out = gson.toJson(gameSession, GameSession.class);
        System.out.println(out);
        assertNotNull(out);
    }
}
