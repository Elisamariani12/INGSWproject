package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.mocks.CompressedModelMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ChooseResourcesSceneTest {

    @Test
    void draw() {
        CardRepository.getInstance().loadAllData();

        CompressedModelMock compressedModel=new CompressedModelMock();
        compressedModel.setaP(0);
        ChooseResourcesScene chooseResourcesScene= new ChooseResourcesScene();
        chooseResourcesScene.draw(compressedModel);

        List<Integer> list=new ArrayList<Integer>();
        list.add(3);
        list.add(13);
        list.add(14);
        list.add(15);

        compressedModel.setaPHiddenLeaderCardsMock(list);
        chooseResourcesScene.drawLeadercard(compressedModel);

    }

    @Test
    void askInput() {
        //CompressedModel compressedModel=new CompressedModel();
        //compressedModel.setaP(3);
        //ChooseResourcesScene chooseResourcesScene= new ChooseResourcesScene();
        //chooseResourcesScene.draw(compressedModel);
        //chooseResourcesScene.askInput(compressedModel);
        //System.out.println(chooseResourcesScene.getUpdatedPlayerEvent().getInputResources());

    }



}