package it.polimi.ingsw.client.cli.scenes;


import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * CLI scene to choose initial resources and leader cards
 */
public class ChooseResourcesScene extends TextScene {
    private static final String background =
            "                ╔═╗┬ ┬┌─┐┌─┐┌─┐┌─┐  ┬─┐┌─┐┌─┐┌─┐┬ ┬┬─┐┌─┐┌─┐┌─┐\n" +
            "                ║  ├─┤│ ││ │└─┐├┤   ├┬┘├┤ └─┐│ ││ │├┬┘│  ├┤ └─┐\n" +
            "                ╚═╝┴ ┴└─┘└─┘└─┘└─┘  ┴└─└─┘└─┘└─┘└─┘┴└─└─┘└─┘└─┘\n" +
            "\n" +
            "     ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "     █                                                                    █\n" +
            "     █                                                                    █\n" +
            "     █              You still have _  resources to choose.                █\n" +
            "     █                                                                    █\n" +
            "     █                                                                    █\n" +
            "     █   Chosen resources:                                                █\n" +
            "     █                                                                    █\n" +
            "     █   Type: S -> stones    to choose the resource.                     █\n" +
            "     █         C -> coins                                                 █\n" +
            "     █         X -> shields                                               █\n" +
            "     █         Y -> servants                                              █\n" +
            "     █                                                                    █\n" +
            "     █                                                                    █\n" +
            "     █                                                                    █\n" +
            "     █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█";

    private static final String background2 =
            "             ╔═╗┬ ┬┌─┐┌─┐┌─┐┌─┐  ╦  ┌─┐┌─┐┌┬┐┌─┐┬─┐  ╔═╗┌─┐┬─┐┌┬┐┌─┐\n" +
            "             ║  ├─┤│ ││ │└─┐├┤   ║  ├┤ ├─┤ ││├┤ ├┬┘  ║  ├─┤├┬┘ ││└─┐\n" +
            "             ╚═╝┴ ┴└─┘└─┘└─┘└─┘  ╩═╝└─┘┴ ┴─┴┘└─┘┴└─  ╚═╝┴ ┴┴└──┴┘└─┘\n" +
            "\n" +
            "     ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "     █                                                                    █\n" +
            "     █  You still have to choose 2 leader cards.                          █\n" +
            "     █                                                                    █\n" +
            "     █          A               B               C               D         █\n" +
            "     █  ╔══════════════╗╔══════════════╗╔══════════════╗╔══════════════╗  █\n" +
            "     █  ║pts:          ║║pts:          ║║pts:          ║║pts:          ║  █\n" +
            "     █  ║Requirements: ║║Requirements: ║║Requirements: ║║Requirements: ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║Type:         ║║Type:         ║║Type:         ║║Type:         ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ║              ║║              ║║              ║║              ║  █\n" +
            "     █  ╚══════════════╝╚══════════════╝╚══════════════╝╚══════════════╝  █\n" +
            "     █                                                                    █\n" +
            "     █  Type the letter of the first  leader card.                        █\n" +
            "     █                                                                    █\n" +
            "     █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█\n";



    //RESOURCE CHOICE
    private static final int BOX1_COL =36;
    private static final int FIRST_PLAYER_COL =17;
    private static final int BOX1_ROW = 7;
    private static final int BOX2_ROW = 10;
    private static final int BOX2_COL =  29;

    private static final String ACCEPTED_INPUTS = "SsCcXxYy";

    //LEADER CARD CHOICE
    private static final int BOX_NUM_of_RESOURCES_ROW=6;
    private static final int BOX_NUM_of_RESOURCES_COL=34;
    private static final int BOX_LEADERCARD_PTS_ROW=10;
    private static final int[] BOX_LEADERCARD_PTS_COL={15,31,47,63};
    private static final int BOX_requirements_ROW=12;
    private static final int[] BOX_requirements_COL={10,26,42,58};
    private static final int BOX_type_ROW=16;
    private static final int[] BOX_type_COL={15,31,47,63};
    private static final int BOX_TO_ERASE_ROW=10;
    private static final int[] BOX_TO_ERASE_COL={10,26,42,58};
    private static final int BOX_TO_ERASE_HEIGHT=13;
    private static final int BOX_Number_OF_CHOICE_ROW=25;
    private static final int BOX_Number_OF_CHOICE_COL=32;


    @Override
    public void draw(CompressedModel compressedModel) {
        int numberOfPlayer = compressedModel.getaP();
        String[] arrayToPrint = super.toArrayFromString(background);

        //if it's first player's turn, no resources are needed
        if(numberOfPlayer == 0){
            arrayToPrint=super.substituteLineBackground(arrayToPrint, "You are the first player, you cannot choose any resource.", BOX1_ROW,FIRST_PLAYER_COL-5);
            arrayToPrint=super.substituteLineBackground(arrayToPrint, "The match is starting, type anything to continue", BOX1_ROW+1,FIRST_PLAYER_COL);

        }
        else{
            int numberOfResources = GameConstants.INITIAL_RESOURCES_AMOUNT_FOR_PLAYER[numberOfPlayer];
            String numberOfResourcesString= Integer.toString(numberOfResources);
            arrayToPrint=super.substituteLineBackground(arrayToPrint,numberOfResourcesString, BOX1_ROW,BOX1_COL);

        }
        System.out.println(super.toStringFromArray(arrayToPrint));
    }

    @Override
    public void askInput(CompressedModel compressedModel) {
        String[] arrayToPrint = super.toArrayFromString(background);
        String box2_content= "";
        Scanner scanner=new Scanner(System.in);
        List<Pair<Resource, StorageType>> toInsertInPlayerEvent = new ArrayList<>();
        int numberOfPlayer = compressedModel.getaP();

        //if it's first player's turn, no resources are needed
        if(numberOfPlayer == 0){
            scanner.nextLine();
        }
        else {
            int numberOfResources = GameConstants.INITIAL_RESOURCES_AMOUNT_FOR_PLAYER[numberOfPlayer];
            int counter = numberOfResources;

            while (counter > 0) {
                char digit;
                String digitstring= scanner.next();
                digitstring=digitstring.toUpperCase(Locale.ROOT);
                digit = digitstring.charAt(0);


                while (ACCEPTED_INPUTS.indexOf(digit) == -1) {
                    System.out.println(GameConstants.SCENE_CHOOSE_RES_RESOURCE_NOT_RECOGNIZED);
                    digit = scanner.next().charAt(0);
                }
                counter--;
                arrayToPrint = super.substituteLineBackground(arrayToPrint, Integer.toString(counter), BOX1_ROW, BOX1_COL);


                if (digit == 'S') {
                    if (counter == numberOfResources - 1) box2_content = box2_content.concat("Stone");
                    else box2_content = box2_content.concat(",Stone");
                    Pair<Resource, StorageType> newRes = new Pair<>(Resource.STONE, StorageType.WAREHOUSE_DEPOT);
                    toInsertInPlayerEvent.add(newRes);
                } else if (digit == 'C') {
                    if (counter == numberOfResources - 1) box2_content = box2_content.concat("Coin");
                    else box2_content = box2_content.concat(",Coin");
                    Pair<Resource, StorageType> newRes = new Pair<>(Resource.COIN, StorageType.WAREHOUSE_DEPOT);
                    toInsertInPlayerEvent.add(newRes);
                } else if (digit == 'X') {
                    if (counter == numberOfResources - 1) box2_content = box2_content.concat("Shield");
                    else box2_content = box2_content.concat(",Shield");
                    Pair<Resource, StorageType> newRes = new Pair<>(Resource.SHIELD, StorageType.WAREHOUSE_DEPOT);
                    toInsertInPlayerEvent.add(newRes);
                } else if (digit == 'Y') {
                    if (counter == numberOfResources - 1) box2_content = box2_content.concat("Servant");
                    else box2_content = box2_content.concat(",Servant");
                    Pair<Resource, StorageType> newRes = new Pair<>(Resource.SERVANT, StorageType.WAREHOUSE_DEPOT);
                    toInsertInPlayerEvent.add(newRes);
                }
                arrayToPrint = super.substituteLineBackground(arrayToPrint, box2_content, BOX2_ROW, BOX2_COL);
                System.out.println(super.toStringFromArray(arrayToPrint));
            }
        }

        //LEADER CARD CHOICE
        arrayToPrint=this.drawLeadercard(compressedModel);
        System.out.println(super.toStringFromArray(arrayToPrint));

        //read the first chosen card
        scanner = new Scanner(System.in);
        String digitString = scanner.nextLine();

        char firstChoice = digitString.isEmpty() ? ' ' : Character.toUpperCase(digitString.charAt(0));

        while((digitString.length()>1)||((firstChoice!='A')&&(firstChoice!='B')&&(firstChoice!='C')&&(firstChoice!='D'))){
            System.out.println(GameConstants.SCENE_CHOOSE_RES_LEADERCARD_NOT_RECOGNIZED);
            digitString = scanner.nextLine();
            firstChoice = digitString.isEmpty() ? ' ' : Character.toUpperCase(digitString.charAt(0));
        }

        //erase the chosen card
        int cardINDEX_1 = firstChoice - 65;
        for(int p=0;p<BOX_TO_ERASE_HEIGHT;p++){arrayToPrint=substituteLineBackground(arrayToPrint,"░░░░░░░░░░░░░░",BOX_TO_ERASE_ROW+p,BOX_TO_ERASE_COL[cardINDEX_1]);}

        //change the remaining resources box AND 'FIRST' in 'second'
        arrayToPrint=substituteLineBackground(arrayToPrint,"1",BOX_NUM_of_RESOURCES_ROW,BOX_NUM_of_RESOURCES_COL);
        arrayToPrint=substituteLineBackground(arrayToPrint,"second",BOX_Number_OF_CHOICE_ROW,BOX_Number_OF_CHOICE_COL);

        //print AGAIN without thye chosen card
        System.out.println(super.toStringFromArray(arrayToPrint));

        //read the second chosen card
        digitString = scanner.nextLine();
        char secondChoice = digitString.isEmpty() ? ' ' : Character.toUpperCase(digitString.charAt(0));

        while((digitString.length()>1)||((secondChoice!='A')&&(secondChoice!='B')&&(secondChoice!='C')&&(secondChoice!='D'))||(secondChoice==firstChoice)){
            System.out.println(GameConstants.SCENE_CHOOSE_RES_LEADERCARD_NOT_RECOGNIZED);
            digitString = scanner.nextLine();
            secondChoice = digitString.isEmpty() ? ' ' : Character.toUpperCase(digitString.charAt(0));
        }
        int cardINDEX_2 = secondChoice - 65;

        int cardID_1 = compressedModel.getaPInitialChooseLeaderCard().get(cardINDEX_1);
        int cardID_2 = compressedModel.getaPInitialChooseLeaderCard().get(cardINDEX_2);
        List<Integer> chosenCards = new ArrayList<>();
        chosenCards.add(cardID_1);chosenCards.add(cardID_2);

        PlayerWorkSpace.getInstance().initialResourceAndLeaderCard(chosenCards, toInsertInPlayerEvent);
    }


    /**
     * Draw leadercard string [ ].
     *
     * @param compressedModel the compressed model
     * @return the scene background string [ ]
     */
    public String[] drawLeadercard(CompressedModel compressedModel){
        String[] arrayToPrint = super.toArrayFromString(background2);

        int size=0;
        try{size=compressedModel.getaPInitialChooseLeaderCard().size();}
        catch(Exception e){}
        for(int i=0;i < size;i++){
            LeaderCard leaderCard;
            try {
                leaderCard = CardRepository.getInstance().getLeaderCardByID(compressedModel.getaPInitialChooseLeaderCard().get(i));

                arrayToPrint = substituteLineBackground(arrayToPrint, ((Integer) leaderCard.getVictoryPoints()).toString(), BOX_LEADERCARD_PTS_ROW, BOX_LEADERCARD_PTS_COL[i]);

                //requirements
                String[] dividestring=leaderCard.getRequirementString().split("\n");
                for (int k = 0; k < dividestring.length; k++) {
                    arrayToPrint = substituteLineBackground(arrayToPrint, dividestring[k], BOX_requirements_ROW+k, BOX_requirements_COL[i]);
                }

                //power
                arrayToPrint = substituteLineBackground(arrayToPrint, leaderCard.getTypeString(), BOX_type_ROW, BOX_type_COL[i]);
                dividestring = leaderCard.getPowerString().split("\n");
                for (int k = 0; k < dividestring.length; k++) {
                    arrayToPrint = substituteLineBackground(arrayToPrint, dividestring[k], BOX_type_ROW + 1+k, BOX_requirements_COL[i]);
                }
            }
            catch(NullPointerException e){leaderCard=null;i=compressedModel.getaPInitialChooseLeaderCard().size();}
        }

        return  arrayToPrint;
    }




}

