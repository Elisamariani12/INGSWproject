package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.mocks.CompressedModelMock;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class WaitingTurnSceneTest {

    @Test
    void draw() {
        CompressedModelMock compressedModel=new CompressedModelMock();

        List<String> list_of_names = new ArrayList<>();
        List<Integer> list_of_pos = new ArrayList<>();
        list_of_names.add("aaa");
        list_of_names.add("aa");
        list_of_pos.add(22);
        list_of_pos.add(5);
        compressedModel.setaPMock(2);
        compressedModel.setPlayerNamesMock(list_of_names);
        compressedModel.setPositionsOfFaithTrackMock(list_of_pos);

        //ADD RESOURCES TO THE PLAYERS
        ResourceStack resourceStack1 = new ResourceStack();
        ResourceStack resourceStack2 = new ResourceStack();
        try
        {
            resourceStack1.setMaxSize(100);
            resourceStack1.setType(Resource.COIN);
            resourceStack1.setAmount(3);
            resourceStack2.setType(Resource.STONE);
            resourceStack2.setMaxSize(100);
            resourceStack2.setAmount(5);
        }
        catch (FullResourceStackException e){e.printStackTrace();}


        ResourceStack[] resourceStacks = {resourceStack1,resourceStack1};
        ResourceStack[] resourceStacks2 = {resourceStack1,resourceStack2,resourceStack2};
        List<ResourceStack[]> list =new ArrayList<>();
        list.add(resourceStacks2);
        list.add(resourceStacks);
        list.add(resourceStacks);
        compressedModel.setCumulatedPlayerStorageMock(list);

        Stack<Integer> stack = new Stack<>();stack.add(1);stack.add(2);
        Stack<Integer> stack2 = new Stack<>();stack2.add(1);stack2.add(2);
        Stack<Integer> stack3 = new Stack<>();stack3.add(1);stack3.add(2);stack3.add(2);
        //devcard for player 1: he has only 2 cards
        List<Stack<Integer>> list1 = new ArrayList<>(); list1.add(stack);
        //devcard for player 2: he has 4 cards
        List<Stack<Integer>> list2 = new ArrayList<>(); list2.add(stack);list2.add(stack2);
        //devcard for player 3: he has 7 cards
        List<Stack<Integer>> list3 = new ArrayList<>(); list3.add(stack);list3.add(stack2);list3.add(stack3);

        List<List<Stack<Integer>>> totalList = new ArrayList<>(); totalList.add(list1);totalList.add(list2);totalList.add(list3);
        compressedModel.setDevCardSpaceMock(totalList);

        WaitingTurnScene waitingTurnScene = new WaitingTurnScene();
        waitingTurnScene.draw(compressedModel);
    }

    @Test
    void askInput() {
    }
}