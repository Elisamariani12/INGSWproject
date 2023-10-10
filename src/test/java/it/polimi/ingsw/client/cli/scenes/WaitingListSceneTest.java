package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.mocks.CompressedModelMock;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.exceptions.PlayerCountOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class WaitingListSceneTest {
    private WaitingListScene waitingListScene;
    private ArrayList<String> playerArray;
    private CompressedModelMock model;
    private Controller controller;

   @BeforeEach
    void setUp() throws PlayerCountOutOfBoundsException {
        waitingListScene = new WaitingListScene();
        String player="MARIO";
        String player1="LUIGI";
        String player2="DAISY";
        playerArray = new ArrayList<>();
        playerArray.add(player);
        playerArray.add(player1);
        playerArray.add(player2);
        model = new CompressedModelMock();
        model.setPlayerNamesMock(playerArray);
    }

    @Test
    void draw() {
        waitingListScene.draw(model);
    }
}