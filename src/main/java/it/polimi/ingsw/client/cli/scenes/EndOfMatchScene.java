package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.client.util.WinningChecker;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.GameConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


/**
 * CLI scene that appears when the match ends
 */
public class EndOfMatchScene extends TextScene {
    private static final String WIN_background =
            "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n" +
            "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n" +
            "\n" +
            "\n" +
            "                       ╔═╗╔═╗╔╦╗╔═╗  ╔═╗╦  ╦╔═╗╦═╗\n" +
            "                       ║ ╦╠═╣║║║║╣   ║ ║╚╗╔╝║╣ ╠╦╝\n" +
            "                       ╚═╝╩ ╩╩ ╩╚═╝  ╚═╝ ╚╝ ╚═╝╩╚═\n" +
            "\n" +
            "\n" +
            "       ██╗░░░██╗░█████╗░██╗░░░██╗  ░██╗░░░░░░░██╗░█████╗░███╗░░██╗██╗██╗\n" +
            "       ╚██╗░██╔╝██╔══██╗██║░░░██║  ░██║░░██╗░░██║██╔══██╗████╗░██║██║██║\n" +
            "       ░╚████╔╝░██║░░██║██║░░░██║  ░╚██╗████╗██╔╝██║░░██║██╔██╗██║██║██║\n" +
            "       ░░╚██╔╝░░██║░░██║██║░░░██║  ░░████╔═████║░██║░░██║██║╚████║╚═╝╚═╝\n" +
            "       ░░░██║░░░╚█████╔╝╚██████╔╝  ░░╚██╔╝░╚██╔╝░╚█████╔╝██║░╚███║██╗██╗\n" +
            "       ░░░╚═╝░░░░╚════╝░░╚═════╝░  ░░░╚═╝░░░╚═╝░░░╚════╝░╚═╝░░╚══╝╚═╝╚═╝\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                           TOTAL SCORE:                                 \n" +
            "\n" +
            "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n" +
            "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒";

    private static final String LOSE_background ="" +
            "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n" +
            "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n" +
            "\n" +
            "\n" +
            "\n" +
            "                       ╔═╗╔═╗╔╦╗╔═╗  ╔═╗╦  ╦╔═╗╦═╗\n" +
            "                       ║ ╦╠═╣║║║║╣   ║ ║╚╗╔╝║╣ ╠╦╝\n" +
            "                       ╚═╝╩ ╩╩ ╩╚═╝  ╚═╝ ╚╝ ╚═╝╩╚═\n" +
            "\n" +
            "\n" +
            "      ██    ██  ██████  ██    ██     ██       ██████  ███████ ███████ ██\n" +
            "       ██  ██  ██    ██ ██    ██     ██      ██    ██ ██      ██      ██\n" +
            "        ████   ██    ██ ██    ██     ██      ██    ██ ███████ █████   ██\n" +
            "         ██    ██    ██ ██    ██     ██      ██    ██      ██ ██\n" +
            "         ██     ██████   ██████      ███████  ██████  ███████ ███████ ██\n" +
            "                                                                        \n" +
            "                                                                        \n" +
            "                           TOTAL SCORE:                                 \n" +
            "\n" +
            "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n" +
            "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒";


    //Positions of score "text field"
    private static int SCORE_ROW_WIN = 19;
    private static int SCORE_LINE_POS_WIN = 40;
    private static int SCORE_ROW_LOSE = 17;
    private static int SCORE_LINE_POS_LOSE = 40;
    private Scanner scanner;

    /** Creates a new EndOfMatchScene
     */
    public EndOfMatchScene()
    {
        super();
        scanner = new Scanner(System.in);
    }


    @Override
    public void draw(CompressedModel compressedModel)
    {

        String[] background;

        WinningChecker winningChecker = new WinningChecker(compressedModel);
        LinkedHashMap<String, Integer> leaderBoard = winningChecker.getLeaderBoard();

        //Determine and show whether or not the player has won
        if(winningChecker.hasWon()) {
            background = toArrayFromString(WIN_background);
            background = substituteLineBackground(background,leaderBoard.get(PlayerWorkSpace.getInstance().getPlayerUsername()).toString(), SCORE_ROW_WIN, SCORE_LINE_POS_WIN);
        }
        else {
            background = toArrayFromString(LOSE_background);
            if(leaderBoard.get(PlayerWorkSpace.getInstance().getPlayerUsername())!=null){ background = substituteLineBackground(background, leaderBoard.get(PlayerWorkSpace.getInstance().getPlayerUsername()).toString(), SCORE_ROW_LOSE, SCORE_LINE_POS_LOSE);}
        }


        System.out.println(toStringFromArray(background) + '\n');

        //Print ordered score board
        System.out.println("FINAL SCORE BOARD:");
        int index = 1;
        for(String name : leaderBoard.keySet())
        {
            System.out.println(index + ". " + name + " : " + leaderBoard.get(name));
            index++;
        }

        //Empty line
        System.out.println();
    }

    @Override
    public void askInput(CompressedModel compressedModel)
    {
        System.out.println(GameConstants.SCENE_END_OF_MATCH_MESSAGE);
        //Just wait for any input
        scanner.nextLine();
        System.exit(0);
    }
}
