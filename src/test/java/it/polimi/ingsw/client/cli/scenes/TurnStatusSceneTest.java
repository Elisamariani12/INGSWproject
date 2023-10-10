package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.mocks.CompressedModelMock;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class TurnStatusSceneTest {
    private CompressedModelMock compressedModel;
    private List<ResourceStack[]> resourcePlayer;
    private TurnStatusScene turnStatusScene;

    @BeforeEach
    void setUp() throws FullResourceStackException {
        CardRepository.getInstance().loadAllData();

        ResourceStack[] resources = new ResourceStack[3];
        compressedModel = new CompressedModelMock();
        resources[0] = new ResourceStack(Resource.COIN, 5, 5);
        resources[1] = new ResourceStack(Resource.SERVANT, 5, 5);
        resources[2] = new ResourceStack(Resource.SHIELD, 3, 5);
        resourcePlayer = new ArrayList<>();
        resourcePlayer.add(resources);
        compressedModel.setCumulatedPlayerStorageMock(resourcePlayer);
        compressedModel.setaPMock(0);
        compressedModel.setTurnStateMock(TurnState.CHOOSE_ACTION);
        ArrayList<Integer> leaderCardList = new ArrayList<>();
        leaderCardList.add(3);
        leaderCardList.add(5);
        compressedModel.setaPHiddenLeaderCardsMock(leaderCardList);
        List<List<Integer>> allLeadercard = new ArrayList<>();
        List<Integer> playerCards = new ArrayList<>();
        allLeadercard.add(playerCards);
        compressedModel.setAllPlayersActiveLeaderCardsMock(allLeadercard);

        Stack<Integer> s1 = new Stack<>();
        Stack<Integer> s2 = new Stack<>();
        Stack<Integer> s3 = new Stack<>();
        s1.add(20);
        s2.add(21);
        s3.add(40);
        List<Stack<Integer>> devPlayer = new ArrayList<>();
        devPlayer.add(s1);
        devPlayer.add(s2);
        devPlayer.add(s3);
        List<List<Stack<Integer>>> devCardSpace = new ArrayList<>();
        devCardSpace.add(devPlayer);
        compressedModel.setDevCardSpaceMock(devCardSpace);
    }

    @Test
    void draw() {
    }

}