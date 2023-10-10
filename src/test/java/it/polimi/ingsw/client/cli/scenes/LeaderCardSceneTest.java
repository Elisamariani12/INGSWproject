package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.mocks.CompressedModelMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class LeaderCardSceneTest {

    @Test
    void draw() {
        CardRepository.getInstance().loadAllData();

        CompressedModelMock compressedModel=new CompressedModelMock();
        compressedModel.setaP(0);
        LeaderCardScene leaderCardScene=new LeaderCardScene();

        List<Integer> list=new ArrayList<Integer>();
        list.add(15);
        List<List<Integer>> allplayerActiveCards=new ArrayList<>();
        List<Integer> listActive=new ArrayList<Integer>();
        listActive.add(12);
        allplayerActiveCards.add(listActive);

        compressedModel.setaPHiddenLeaderCardsMock(list);
        compressedModel.setAllPlayersActiveLeaderCardsMock(allplayerActiveCards);
        leaderCardScene.draw(compressedModel);

    }

    @Test
    void askInput() {
        //to copy in a main in the class leader card scene for testing
        /*CardRepository.getInstance().loadLeaderCardsFromFile("src/test/resources/lead_cards.json");
        CardRepository.getInstance().loadDevelopmentCardsFromFile("src/test/resources/dev_cards.json");


        CompressedModelMock compressedModel=new CompressedModelMock();
        compressedModel.setaPMock(0);
        LeaderCardScene leaderCardScene=new LeaderCardScene();

        List<Integer> list=new ArrayList<Integer>();
        list.add(15);
        List<List<Integer>> allplayerActiveCards=new ArrayList<>();
        List<Integer> listActive=new ArrayList<Integer>();
        listActive.add(12);
        allplayerActiveCards.add(listActive);

        //for dev cards
        List<List<Stack<Integer>>> devcards=new ArrayList<>();
        Stack<Integer> devcardstack=new Stack<>();
        devcardstack.add(17);
        devcardstack.add(53);
        List<Stack<Integer>> devcardlist= new ArrayList<>();
        devcardlist.add(devcardstack);
        devcards.add(devcardlist);

        compressedModel.setDevCardSpaceMock(devcards);
        compressedModel.setaPHiddenChosenLeaderCards(list);
        compressedModel.setAllPlayersActiveLeaderCardsMock(allplayerActiveCards);
        ResourceStack resourceStack=new ResourceStack();
        ResourceStack[] resourceStacks={resourceStack};
        List<ResourceStack[]> resourceStackList=new ArrayList<>();
        resourceStackList.add(resourceStacks);
        compressedModel.setCumulatedPlayerStorageMock(resourceStackList);

        leaderCardScene.draw(compressedModel);
        leaderCardScene.askInput(compressedModel);*/
    }
}