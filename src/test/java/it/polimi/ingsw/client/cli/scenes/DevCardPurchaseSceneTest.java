package it.polimi.ingsw.client.cli.scenes;

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

class DevCardPurchaseSceneTest {

    @Test
    void draw() {
        CompressedModelMock compressedModel=new CompressedModelMock();
        PlayerEvent playerEvent = new PlayerEvent();
        DevCardPurchaseScene devCardPurchaseScene=new DevCardPurchaseScene();

        compressedModel.setaPMock(0);

        //ADD DEV CARDS TO THE PLAYERS
        CardRepository.getInstance().loadAllData();
        Stack<Integer> s1 = new Stack<>();
        Stack<Integer> s2 = new Stack<>();
        Stack<Integer> s3 = new Stack<>();
        s1.add(19);
        //s2.add(23);
        //s3.add(22);
        List<Stack<Integer>> devPlayer = new ArrayList<>();
        devPlayer.add(s1);
        devPlayer.add(s3);
        devPlayer.add(s2);
        List<List<Stack<Integer>>> devCardSpace = new ArrayList<>();
        devCardSpace.add(devPlayer);
        compressedModel.setDevCardSpaceMock(devCardSpace);

        //ADD RESOURCES TO THE PLAYERS
        //build resource stacks
        ResourceStack resourceStack1= new ResourceStack();
        ResourceStack resourceStack2= new ResourceStack();
        ResourceStack resourceStack3= new ResourceStack();
        ResourceStack resourceStack4= new ResourceStack();
        try {resourceStack1.setMaxSize(100);resourceStack1.setType(Resource.COIN);resourceStack1.setAmount(10);
            resourceStack2.setType(Resource.STONE);resourceStack2.setMaxSize(100);resourceStack2.setAmount(10);
            resourceStack3.setType(Resource.SERVANT);resourceStack3.setMaxSize(100);resourceStack3.setAmount(10);
            resourceStack4.setType(Resource.SHIELD);resourceStack4.setMaxSize(100);resourceStack4.setAmount(10);}
        catch (FullResourceStackException e){e.printStackTrace();}
        ResourceStack[] resourceStacks={resourceStack1,resourceStack2};                                 //resources of player 0
        ResourceStack[] resourceStacks2={resourceStack1,resourceStack2,resourceStack3,resourceStack4};  //resources of player 1
        List<ResourceStack[]> list=new ArrayList<>();
        list.add(resourceStacks2);list.add(resourceStacks);
        compressedModel.setCumulatedPlayerStorageMock(list);

        //SET DEV CARD GRID
        int[][] cards={{17,18,19,20},{21,22,23,24},{29,30,31,32}};
        compressedModel.setDevCardGridStateMock(cards);

    }

    @Test
    void askInput() {
        //TO TEST, COPY AND PASTE IN MAIN
        //it.polimi.ingsw.common.serializable.CompressedModelMock compressedModel=new it.polimi.ingsw.common.serializable.CompressedModelMock();
        //DevCardPurchaseScene devCardPurchaseScene=new DevCardPurchaseScene();

       // compressedModel.setaPMock(0);

        //ADD DEV CARDS TO THE PLAYERS
     //   CardRepository.getInstance().loadActionTokensFromFile("src/test/resources/action_token_stock.json");
      //  CardRepository.getInstance().loadLeaderCardsFromFile("src/test/resources/lead_cards.json");
      //  CardRepository.getInstance().loadDevelopmentCardsFromFile("src/test/resources/dev_cards.json");
      //  Stack<Integer> s1 = new Stack<>();
      //  Stack<Integer> s2 = new Stack<>();
        //Stack<Integer> s3 = new Stack<>();
       // s1.add(19);
        //s2.add(23);
        //s3.add(22);
       // List<Stack<Integer>> devPlayer = new ArrayList<>();
       // devPlayer.add(s1);
       // devPlayer.add(s3);
       // devPlayer.add(s2);
       // List<List<Stack<Integer>>> devCardSpace = new ArrayList<>();
       // devCardSpace.add(devPlayer);
        //compressedModel.setDevCardSpaceMock(devCardSpace);

        //ADD RESOURCES TO THE PLAYERS
        //build resource stacks
        //ResourceStack resourceStack1= new ResourceStack();
        //ResourceStack resourceStack2= new ResourceStack();
        //ResourceStack resourceStack3= new ResourceStack();
        //ResourceStack resourceStack4= new ResourceStack();
        //try {resourceStack1.setMaxSize(100);resourceStack1.setType(Resource.COIN);resourceStack1.setAmount(10);
         //   resourceStack2.setType(Resource.STONE);resourceStack2.setMaxSize(100);resourceStack2.setAmount(10);
         //   resourceStack3.setType(Resource.SERVANT);resourceStack3.setMaxSize(100);resourceStack3.setAmount(10);
         //   resourceStack4.setType(Resource.SHIELD);resourceStack4.setMaxSize(100);resourceStack4.setAmount(10);}
       // catch (FullResourceStackException e){e.printStackTrace();}
       // ResourceStack[] resourceStacks={resourceStack1,resourceStack2};                                 //resources of player 0
       // ResourceStack[] resourceStacks2={resourceStack1,resourceStack2,resourceStack3,resourceStack4};  //resources of player 1
       // List<ResourceStack[]> list=new ArrayList<>();
       // list.add(resourceStacks2);list.add(resourceStacks);
       // compressedModel.setCumulatedPlayerStorageMock(list);

        //SET DEV CARD GRID
       // int[][] cards={{17,18,19,20},{21,22,23,24},{29,30,31,32}};
       // compressedModel.setDevCardGridStateMock(cards);



       // devCardPurchaseScene.draw(compressedModel);
       // devCardPurchaseScene.askInput(compressedModel);
    }
}