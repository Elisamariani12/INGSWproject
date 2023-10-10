package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.ResourceStack;

import java.util.Scanner;
import java.util.Stack;

/**
 * CLI scene used when a player has to wait his turn
 */
public class WaitingTurnScene extends TextScene {
    private static final String background =
            "              ╦ ╦┌─┐┬┌┬┐  ┌─┐┌─┐┬─┐  ┬ ┬┌─┐┬ ┬┬─┐  ┌┬┐┬ ┬┬─┐┌┐┌\n" +
            "              ║║║├─┤│ │   ├┤ │ │├┬┘  └┬┘│ ││ │├┬┘   │ │ │├┬┘│││\n" +
            "              ╚╩╝┴ ┴┴ ┴   └  └─┘┴└─   ┴ └─┘└─┘┴└─   ┴ └─┘┴└─┘└┘\n" +
            "\n" +
            "    ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "    █                                                                    █\n" +
            "    █ ╔1═════╦2═════╦3═════╦4═════╦5═════╦6═════╦7═════╦8═════╦9═════╗   █\n" +
            "    █ ║      ║      ║      ║      ║░░░░░░║░░░░░░║░░░░░░║▓▓▓▓▓▓║      ║   █\n" +    //row 7
            "    █ ╚══════╩══════╩══════╩══════╩══════╩══════╩══════╩══════╬10════╣   █\n" +
            "    █                                                         ║      ║   █\n" +
            "    █ ╔19════╦18════╦17════╦16════╦15════╦14════╦13════╦12════╬11════╣   █\n" +
            "    █ ║      ║      ║      ║▓▓▓▓▓▓║░░░░░░║░░░░░░║░░░░░░║      ║      ║   █\n" +    //row 11
            "    █ ╠20════╬══════╩══════╩══════╩══════╩══════╩══════╩══════╩══════╝   █\n" +
            "    █ ║      ║                         Players' boards summary:          █\n" +
            "    █ ╠21════╬22════╦23════╦24════╗   @ _ dev.cards,_  resources         █\n" +
            "    █ ║░░░░░░║░░░░░░║░░░░░░║▓▓▓▓▓▓║   $ _ dev.cards,_  resources         █\n" +    //row 15
            "    █ ╚══════╩══════╩══════╩══════╝   § _ dev.cards,_  resources         █\n" +
            "    █  Playing:                       # _ dev.cards,_  resources         █\n" +
            "    █                                                                    █\n" +
            "    █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█\n";

    private static final String backgroundSinglePlayer =
            "              ╦ ╦┌─┐┬┌┬┐  ┌─┐┌─┐┬─┐  ┬ ┬┌─┐┬ ┬┬─┐  ┌┬┐┬ ┬┬─┐┌┐┌\n" +
            "              ║║║├─┤│ │   ├┤ │ │├┬┘  └┬┘│ ││ │├┬┘   │ │ │├┬┘│││\n" +
            "              ╚╩╝┴ ┴┴ ┴   └  └─┘┴└─   ┴ └─┘└─┘┴└─   ┴ └─┘┴└─┘└┘\n" +
            "\n" +
            "    ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "    █                                                                    █\n" +
            "    █ ╔1═════╦2═════╦3═════╦4═════╦5═════╦6═════╦7═════╦8═════╦9═════╗   █\n" +
            "    █ ║      ║      ║      ║      ║░░░░░░║░░░░░░║░░░░░░║▓▓▓▓▓▓║      ║   █\n" +    //row 7
            "    █ ╚══════╩══════╩══════╩══════╩══════╩══════╩══════╩══════╬10════╣   █\n" +
            "    █                                                         ║      ║   █\n" +
            "    █ ╔19════╦18════╦17════╦16════╦15════╦14════╦13════╦12════╬11════╣   █\n" +
            "    █ ║      ║      ║      ║▓▓▓▓▓▓║░░░░░░║░░░░░░║░░░░░░║      ║      ║   █\n" +    //row 11
            "    █ ╠20════╬══════╩══════╩══════╩══════╩══════╩══════╩══════╩══════╝   █\n" +
            "    █ ║      ║                                                           █\n" +
            "    █ ╠21════╬22════╦23════╦24════╗  L = mover of Lorenzo il Magnifico   █\n" +
            "    █ ║░░░░░░║░░░░░░║░░░░░░║▓▓▓▓▓▓║                                      █\n" +    //row 15
            "    █ ╚══════╩══════╩══════╩══════╝                                      █\n" +
            "    █                                                                    █\n" +
            "    █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█\n";

    private static final int[][] BOXES_faithTrack = {{5,9},{7,9},{7,16},{7,23},{7,30},{7,37},{7,44},{7,51},{7,58},{7,65},{9,65},
                                                      {11,65},{11,58},{11,51},{11,44},{11,37},{11,30},{11,23},{11,16},{11,9},
                                                     {13,9},{15,9},{15,16},{15,23},{15,30}};

    private static final int SUMMARY_BOXES_FIRSTROW=14;
    private static final int SUMMARY_BOXES_COL1=41;
    private static final int SUMMARY_BOXES_COL2=53;
    private static final int ACTIVEPLAYER_BOX_COL=17;
    private static final int ACTIVEPLAYER_BOX_ROW=17;


    @Override
    public void draw(CompressedModel compressedModel) {
        String[] arrayToPrint;
        boolean singleplayer=false;
        if(compressedModel.getPlayerNames().size()==1){
             arrayToPrint= super.toArrayFromString(backgroundSinglePlayer);
             singleplayer=true;
        }
        else{
            arrayToPrint = super.toArrayFromString(background);
        }

        for(int i=0;i<compressedModel.getPlayerNames().size();i++){
            int boxOfThePlayer=compressedModel.getPositionsOfFaithTrack().get(i);
            char toInsert=GameConstants.PLAYER_MARKERS[i];
            String toInsert2=Character.toString(toInsert);
            arrayToPrint=substituteLineBackground(arrayToPrint,toInsert2,BOXES_faithTrack[boxOfThePlayer][0],BOXES_faithTrack[boxOfThePlayer][1]+i);

            if(!singleplayer) {
                int countDevCards=0;
                for(Stack<Integer> stack:compressedModel.getDevCardSpace().get(i)){
                    countDevCards=countDevCards+stack.size();
                }
                arrayToPrint=substituteLineBackground(arrayToPrint, Integer.toString(countDevCards),SUMMARY_BOXES_FIRSTROW+i,SUMMARY_BOXES_COL1);


                int totalresources = 0;
                for (ResourceStack resourceStack : compressedModel.getCumulatedPlayerStorage().get(i)) {
                    totalresources += resourceStack.getAmount();
                }
                arrayToPrint = substituteLineBackground(arrayToPrint, Integer.toString(totalresources), SUMMARY_BOXES_FIRSTROW + i, SUMMARY_BOXES_COL2);
            }
        }

        if(!singleplayer){
            char activeplayer=GameConstants.PLAYER_MARKERS[compressedModel.getaP()];
            String aPstring=Character.toString(activeplayer);
            arrayToPrint=substituteLineBackground(arrayToPrint,aPstring,ACTIVEPLAYER_BOX_ROW,ACTIVEPLAYER_BOX_COL);
            System.out.println(super.toStringFromArray(arrayToPrint));
        }

        if(singleplayer){
            //we decided that the 2nd value in positionOfFaithTrack represents the position of Lorenzo il Magnifico
            int boxOfLorenzo=compressedModel.getPositionsOfFaithTrack().get(1);
            arrayToPrint=substituteLineBackground(arrayToPrint,"L",BOXES_faithTrack[boxOfLorenzo][0],BOXES_faithTrack[boxOfLorenzo][1]+1);
            System.out.println(super.toStringFromArray(arrayToPrint));
            System.out.println("Type something to continue");
            (new Scanner(System.in)).nextLine();
        }
    }


    @Override
    public void askInput(CompressedModel compressedModel)
    {
    }
}
