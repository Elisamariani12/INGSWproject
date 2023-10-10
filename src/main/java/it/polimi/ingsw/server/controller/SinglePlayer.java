package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.model.ActionToken;
import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.server.exceptions.FaithTrackOutOfBoundsException;
import it.polimi.ingsw.server.exceptions.ResourceNotInsertableException;
import it.polimi.ingsw.server.model.gamemanager.GameSession;
import it.polimi.ingsw.server.model.gamemanager.Player;

import java.util.HashMap;
import java.util.List;

/**
 * The type Single player.
 */
public class SinglePlayer extends GameMode{
    private GameSession gameSession = getModel().getGameSession();

    /**
     * Instantiates a new Single player.
     */
    public SinglePlayer() {
        super();
    }

    @Override
    public void setupGame() {
        prepareDevCardGrid();
        dealLeaderCard();
        preparePersonalBoards();
    }

    @Override
    public void calculateScores() {
        getModel().getGameSession().getOrderedScoreBoard().put(gameSession.getPlayerByIndex(0),countAllVictoryPointsForPlayer(0));
    }


    @Override
    public void tryAdvance(Player player, int amount) {
        int actualPosition;
        actualPosition = player.getBoard().getFaithTrack().getPosition();

        if(actualPosition + amount == GameConstants.FAITH_TRACK_LAST_SPACE_INDEX){
            gameSession.setIsLastRound();
        }

        int index = 1;
        for(int pos : GameConstants.FAITH_TRACK_POSITION_POPE_SPACE){

            if(actualPosition < pos && pos <= actualPosition + amount){
                if(!player.getBoard().getFaithTrack().getPopeFavour(index) && !gameSession.getLorenzoDeMedici().getPopeFavour(index)){
                    vaticanReport(0,index);
                }
            }
            index++;
        }
        try {
            player.getBoard().getFaithTrack().advance(amount);
        } catch (FaithTrackOutOfBoundsException e) {
            int endValue = GameConstants.FAITH_TRACK_LAST_SPACE_INDEX - actualPosition;
            try {
                player.getBoard().getFaithTrack().advance(endValue);
            } catch (FaithTrackOutOfBoundsException faithTrackOutOfBoundsException) {
                faithTrackOutOfBoundsException.printStackTrace();
            }
            gameSession.setIsLastRound();
        }
    }

    @Override
    public void vaticanReport(int playerActivatingVR,int index) {
        getModel().getGameSession().getPlayerByIndex(0).getBoard().getFaithTrack().activatePopeFavour(index);
    }

    /**
     * Update Lorenzo position
     * @param amount the amount of steps that lorenzo has to do
     */
    private void lorenzoTryAdvance(int amount){
        int actualPosition;
        actualPosition = gameSession.getLorenzoDeMedici().getFaithTrackPosition();

        //If Lorenzo reach the last space, game state is set to last round
        if(actualPosition + amount == GameConstants.FAITH_TRACK_LAST_SPACE_INDEX){
            gameSession.setIsLastRound();
        }

        int index = 1;
        //lorenzo papal reports activation
        for(int pos : GameConstants.FAITH_TRACK_POSITION_POPE_SPACE){

            if(actualPosition < pos && pos <= actualPosition + amount){
                    gameSession.getLorenzoDeMedici().activatePopeFavour(index);
                    if(gameSession.getPlayerByIndex(0).getBoard().getFaithTrack().getPosition() > pos- GameConstants.FAITH_TRACK_LENGTH_VATICAN_REPORT_SECTION[index-1]){
                        gameSession.getPlayerByIndex(0).getBoard().getFaithTrack().activatePopeFavour(index);
                    }
            }
            index++;
        }
        try {
            gameSession.getLorenzoDeMedici().advance(amount);
        } catch (FaithTrackOutOfBoundsException e) {
            int endValue = GameConstants.FAITH_TRACK_LAST_SPACE_INDEX - actualPosition;
            try {
                gameSession.getLorenzoDeMedici().advance(endValue);
            } catch (FaithTrackOutOfBoundsException faithTrackOutOfBoundsException) {
                faithTrackOutOfBoundsException.printStackTrace();
            }
            gameSession.setIsLastRound();
        }

    }

    /**
     * Removes a devCard from a specified column
     * @param column the column
     */
    private void removeCards(int column){
        int row = 0;
        //find first not-empty level
        while(gameSession.getDevelopmentCardGrid().isDeckEmpty(row, column)){
            row++;
        }
        if(row < 2){
            gameSession.getDevelopmentCardGrid().pickCard(row, column);
            gameSession.getDevelopmentCardGrid().pickCard(row, column);
        }
        //if there aren't, lorenzo wins
        else{
            gameSession.setIsLastRound();
        }
    }

    /**
     * Pick action token.
     */
    protected void pickActionToken(){
        ActionToken actualToken;
        actualToken = gameSession.pickActionToken();
        switch(actualToken.getType()){
            case ADVANCE_BLACK_CROSS:
                //advance by two positions
                lorenzoTryAdvance(2);
                break;
            case SHUFFLE_ADVANCE_BLACK_CROSS:
                //advance by one position
                lorenzoTryAdvance(1);
                gameSession.shuffleActionTokens();
                break;
             //remove cards from specific columns
            case DEV_CARD_DISCARD_GREEN:
                removeCards(0);
                break;
            case DEV_CARD_DISCARD_BLUE:
                removeCards(1);
                break;
            case DEV_CARD_DISCARD_YELLOW:
                removeCards(2);
                break;
            case DEV_CARD_DISCARD_PURPLE:
                removeCards(3);
                break;
        }
    }

    @Override
    protected void selectMarketRow(int row, HashMap<Resource, Integer> whiteMarbleChoice, List<Resource> resourcesToDiscard) throws ResourceNotInsertableException {
        super.selectMarketRow(row, whiteMarbleChoice, resourcesToDiscard);
        lorenzoTryAdvance(resourcesToDiscard.size());
    }

    @Override
    protected void selectMarketColumn(int column, HashMap<Resource, Integer> whiteMarbleChoice, List<Resource> resourcesToDiscard) throws ResourceNotInsertableException {
        super.selectMarketColumn(column, whiteMarbleChoice, resourcesToDiscard);
        lorenzoTryAdvance(resourcesToDiscard.size());
    }

    /**
     * This method should never be called in singleplayer
     * @param player Meaningless
     * @param initialResources Meaningless
     */
    @Override
    protected void distributeInitialResources(int player, List<Resource> initialResources) {
        //Nothing
    }
}
