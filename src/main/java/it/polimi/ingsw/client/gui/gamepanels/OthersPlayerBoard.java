package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.components.GameButton;
import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.client.util.ImageUtils;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.*;

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.*;

/**
 * The type Others player board.
 */
public class OthersPlayerBoard extends PlayerBoard{
    private String username;
    private List<Pair<Integer,GameButton>> devCardButtons;

    /**
     * Instantiates a new Others player board.
     *
     * @param username the username of the player thant owns this specific board
     * @param gui      the gui instance, to manage updates
     */
    public OthersPlayerBoard(String username, GUI gui) {
        super(username, gui );
        this.username=username;
        devCardButtons= new ArrayList<>();
    }

    @Override
    public synchronized void updateView(CompressedModel compressedModel) {
        this.removeAll();
        super.updateView(compressedModel);
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void drawLeaderCards(CompressedModel compressedModel) {
        int leaderCardSizeY = Math.round((float)getLeaderCardSizeX() / ImageUtils.getAspectRatio(getLeaderCardBack()));
        int counter = 0;

        int indexOfMyPlayerInTheListOfAllTheActiveLeaderCards=compressedModel.getPlayerNames().indexOf(super.getUsername());

        if(indexOfMyPlayerInTheListOfAllTheActiveLeaderCards!=-1){
            List<Integer> myLeaders=compressedModel.getAllPlayersActiveLeaderCards().get(indexOfMyPlayerInTheListOfAllTheActiveLeaderCards);
            for (Integer activeCard:myLeaders) {

                //CREATE CARD BUTTON
                GameButton leadGameButton = new GameButton(ImageRepository.getInstance().getCardImage(activeCard), getLeaderCardSizeX(), leaderCardSizeY);
                leadGameButton.setBounds(getLeaderCardsPosX(), getLeaderCardPosY()[counter], getLeaderCardSizeX(), leaderCardSizeY);
                leadGameButton.setVisible(true);
                leadGameButton.setEnabled(false);
                this.add(leadGameButton);

                counter++;
            }
        }

    }


    @Override
    protected void drawDevCards(CompressedModel compressedModel) {
        int devCardSizeY = Math.round((float)getDevCardSizeX() / ImageUtils.getAspectRatio(getLeaderCardBack()));


        //Draw devCards
        int counter = 0;
        List<Stack<Integer>> devCardsStacksOrNULL = Optional.ofNullable(getDevCardsStacks()).orElse(Collections.emptyList());
        for(Stack<Integer> stack: devCardsStacksOrNULL){
            if(stack != null && !stack.empty()) {
                Integer cardNumber = stack.peek();
                GameButton devCardGameButton = new GameButton(ImageRepository.getInstance().getCardImage(cardNumber), getDevCardSizeX(),devCardSizeY);
                for(MouseListener mouseListener:devCardGameButton.getMouseListeners())devCardGameButton.removeMouseListener(mouseListener);
                devCardGameButton.setBounds(getDevCardPosX()[counter],getDevCardPosY(), getDevCardSizeX(),devCardSizeY);
                this.add(devCardGameButton);
                devCardGameButton.setVisible(true);
                Pair<Integer,GameButton> pairToInsert=new Pair<>(cardNumber,devCardGameButton);
                devCardButtons.add(pairToInsert);
            }
            counter++;
        }
    }



}
