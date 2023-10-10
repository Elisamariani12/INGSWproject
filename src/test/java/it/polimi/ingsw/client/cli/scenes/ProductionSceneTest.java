package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.mocks.CompressedModelMock;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class ProductionSceneTest {

    @Test
    void draw() {
        CompressedModelMock compressedModel=new CompressedModelMock();
        //ACTIVE PLAYER IS THE PLAYER 1
        compressedModel.setaPMock(0);
        ArrayList<Integer> activatedCards = new ArrayList<>();

        //ADD RESOURCES TO THE PLAYERS
        //build resource stacks
        ResourceStack resourceStack1= new ResourceStack();
        ResourceStack resourceStack2= new ResourceStack();
        ResourceStack resourceStack3= new ResourceStack();
        ResourceStack resourceStack4= new ResourceStack();
        try {resourceStack1.setMaxSize(100);resourceStack1.setType(Resource.COIN);resourceStack1.setAmount(3);
            resourceStack2.setType(Resource.STONE);resourceStack2.setMaxSize(100);resourceStack2.setAmount(5);
            resourceStack3.setType(Resource.SERVANT);resourceStack3.setMaxSize(100);resourceStack3.setAmount(1);
            resourceStack4.setType(Resource.SHIELD);resourceStack4.setMaxSize(100);resourceStack4.setAmount(2);}
        catch (FullResourceStackException e){e.printStackTrace();}
        ResourceStack[] resourceStacks={resourceStack1,resourceStack2};                                 //resources of player 0
        ResourceStack[] resourceStacks2={resourceStack1,resourceStack2,resourceStack3,resourceStack4};  //resources of player 1
        List<ResourceStack[]> list=new ArrayList<>();
        list.add(resourceStacks2);list.add(resourceStacks);
        compressedModel.setCumulatedPlayerStorageMock(list);

        //ADD DEV CARDS TO THE PLAYERS
        CardRepository.getInstance().loadAllData();

        Stack<Integer> s1 = new Stack<>();
        Stack<Integer> s2 = new Stack<>();
        Stack<Integer> s3 = new Stack<>();
        s1.add(20);
        s2.add(21);
        List<Stack<Integer>> devPlayer = new ArrayList<>();
        devPlayer.add(s1);
        devPlayer.add(s3);
        devPlayer.add(s2);
        List<List<Stack<Integer>>> devCardSpace = new ArrayList<>();
        devCardSpace.add(devPlayer);


        compressedModel.setDevCardSpaceMock(devCardSpace);

        //add LEADER CARDS TO THE PLAYER
        ArrayList<Integer> leaderCardList = new ArrayList<>();
        leaderCardList.add(14);
        leaderCardList.add(14);
        compressedModel.setaPHiddenLeaderCardsMock(leaderCardList);
        List<List<Integer>> allLeadercard = new ArrayList<>();
        ArrayList<Integer> playerCards = new ArrayList<>();
        playerCards.add(14);
        //playerCards.add(14);
        allLeadercard.add(playerCards);
        compressedModel.setAllPlayersActiveLeaderCardsMock(allLeadercard);
        //activatedCards.add(20);
        //activatedCards.add(21);
        //activatedCards.add(14);
        //activatedCards.add(-1);
        compressedModel.setTurnAlreadyUsedProductionCardIDs(activatedCards);

        compressedModel.setTurnStateMock(TurnState.PRODUCTION_PHASE);
        PlayerEvent playerEvent = new PlayerEvent();
        ProductionScene productionScene= new ProductionScene();
    }

    @Test
    void askInput() {
    }
}