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

class MarketSceneTest {
    private CompressedModelMock compressedModel;
    private List<ResourceStack[]> resourcePlayer;
    private TurnStatusScene turnStatusScene;

    @BeforeEach
    void setUp() throws FullResourceStackException {
        CardRepository.getInstance().loadAllData();

        compressedModel = new CompressedModelMock();
        compressedModel.setaPMock(0);
        compressedModel.setTurnStateMock(TurnState.MARKET);

        ArrayList<Integer> leaderCardList = new ArrayList<>();
        leaderCardList.add(3);
        //leaderCardList.add(5);
        List<List<Integer>> allLeadercard = new ArrayList<>();
        allLeadercard.add(leaderCardList);
        compressedModel.setAllPlayersActiveLeaderCardsMock(allLeadercard);

        Resource[][] resources = new Resource[3][4];
        resources[0][0] = Resource.COIN;
        resources[0][1] = Resource.WHITE;
        resources[0][2] = Resource.WHITE;
        resources[0][3] = Resource.WHITE;
        resources[1][0] = Resource.SERVANT;
        resources[1][1] = Resource.WHITE;
        resources[1][2] = Resource.WHITE;
        resources[1][3] = Resource.WHITE;
        resources[2][0] = Resource.STONE;
        resources[2][1] = Resource.WHITE;
        resources[2][2] = Resource.COIN;
        resources[2][3] = Resource.WHITE;
        compressedModel.setMarketStateMock(resources);

        ArrayList<ResourceStack> resourceWD = new ArrayList<>();
        resourceWD.add(new ResourceStack(Resource.COIN, 5, 5));
        resourceWD.add(new ResourceStack(Resource.SERVANT, 5, 5));
        compressedModel.setaPWDMock(resourceWD);

        ArrayList<ResourceStack> resourceSB = new ArrayList<>();
        resourceSB.add(new ResourceStack(Resource.STONE, 1, 5));
        resourceSB.add(new ResourceStack(Resource.SERVANT, 5, 5));
        compressedModel.setaPSBMock(resourceSB);

        ArrayList<ResourceStack> resourceLeader= new ArrayList<>();
        resourceLeader.add(new ResourceStack(Resource.COIN, 2, 5));
        resourceLeader.add(new ResourceStack(Resource.SERVANT, 2, 5));
        compressedModel.setaPLeaderStorageMock(resourceLeader);



    }

    @Test
    void draw() {
    }
}