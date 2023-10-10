package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.common.serializable.CompressedModel;

import java.util.List;

/**
 * CLI scene that show the connected player before the match starts
 */
public class WaitingListScene extends TextScene {

    private static final String background =
            "\n" +
            "        ╔╦╗┌─┐┌─┐┌┬┐┌─┐┬─┐┌─┐  ┌─┐┌─┐  ╦═╗┌─┐┌┐┌┌─┐┬┌─┐┌─┐┌─┐┌┐┌┌─┐┌─┐\n" +
            "        ║║║├─┤└─┐ │ ├┤ ├┬┘└─┐  │ │├┤   ╠╦╝├┤ │││├─┤│└─┐└─┐├─┤││││  ├┤\n" +
            "        ╩ ╩┴ ┴└─┘ ┴ └─┘┴└─└─┘  └─┘└    ╩╚═└─┘┘└┘┴ ┴┴└─┘└─┘┴ ┴┘└┘└─┘└─┘\n" +
            "\n" +
            "     ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "     █                                                                    █\n" +
            "     █                        CONNECTED PLAYERS:                          █\n" +
            "     █                                                                    █\n" +
            "     █             »                                                      █\n" +
            "     █                                                                    █\n" +
            "     █             »                                                      █\n" +
            "     █                                                                    █\n" +
            "     █             »                                                      █\n" +
            "     █                                                                    █\n" +
            "     █             »                                                      █\n" +
            "     █                                                                    █\n" +
            "     █                                                                    █\n" +
            "     █                                                                    █\n" +
            "     █                                                                    █\n" +
            "     █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█\n";


    private static final int[] PLAYER_NAME_LINES = {9,11,13,15};
    private static final int PLAYER_NAME_STARTING_COLUMN = 22;

    @Override
    public void draw(CompressedModel compressedModel) {
        List<String> playerNames = compressedModel.getPlayerNames();
        String[] arrayToPrint = super.toArrayFromString(background);
        int lineShift = 0;
        for( String name : playerNames){
            arrayToPrint=super.substituteLineBackground(arrayToPrint, name, PLAYER_NAME_LINES[lineShift],PLAYER_NAME_STARTING_COLUMN);
            lineShift ++;
        }

        System.out.println(super.toStringFromArray(arrayToPrint));
    }

    @Override
    public void askInput(CompressedModel compressedModel)
    {
        //this is supposed to be empty because we do not have to ask anything to the user,
        // he has to insert his username before a text scene appears
    }
}
