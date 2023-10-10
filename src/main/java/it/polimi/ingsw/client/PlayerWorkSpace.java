package it.polimi.ingsw.client;

import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.Move;
import it.polimi.ingsw.common.util.Pair;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.StorageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used to fill in PlayerEvent fields before finalizing the event and sending it to the server [Singleton]
 */
public class PlayerWorkSpace
{
    private PlayerEvent playerEvent;
    private String playerUsername;
    private static PlayerWorkSpace _instance;
    private boolean hasBeenModified;
    private boolean firstTurn;

    /** Creates a new player workspace, defined private as specified in the Singleton pattern
     */
    private PlayerWorkSpace() {
        this.playerEvent = new PlayerEvent();
        this.hasBeenModified = false;
        this.firstTurn = true;
    }

    /**
     * Returns the unique instance of this class
     *
     * @return Instance of this class
     */
    public static PlayerWorkSpace getInstance()
    {
        if(_instance == null) _instance = new PlayerWorkSpace();
        return _instance;
    }

    /**
     * Returns the player event object as currently filled
     *
     * @return PlayerEvent player event copy
     */
    public PlayerEvent getPlayerEventCopy() {
        return playerEvent.copy();
    }


    /**
     * Sets the player username in the player event
     *
     * @param username Player username
     */
    public void setPlayerWorkspaceUsername(String username)
    {
        playerUsername = username;
    }

    /**
     * Returns the player's username
     *
     * @return Player's username
     */
    public String getPlayerUsername()
    {
        return playerEvent.getPlayerUserName();
    }

    /**
     * Resets the PlayerEvent data and set the actual player username
     */
    public void clearPlayerEvent(){
        playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName(playerUsername);
        this.hasBeenModified = false;
    }

    /**
     * Returns whether or not the PlayerEvent was modified since its last clear
     *
     * @return Has the PlayerEvent been modified?
     */
    public boolean wasModified()
    {
        return hasBeenModified;
    }

    /**
     * Registers a leader action
     *
     * @param card      target Leader Card
     * @param isDiscard Whether or not the leader action is of type DISCARD
     */
    public void registerLeaderAction(LeaderCard card, boolean isDiscard)
    {
        clearPlayerEvent();
        playerEvent.setPlayerMove(Move.LEADER_ACTION);
        //Create list if it's the first leader action of the turn
        if(playerEvent.getLeaderCards() == null) playerEvent.setLeaderCards(new ArrayList<>());

        if(card != null){
            playerEvent.getLeaderCards().add(card.getCardID());
            playerEvent.setHasDiscarded(isDiscard);
        }

        if(isDiscard)
        {   //Create list if it's the first leader card discard action of the turn
            if(playerEvent.getDiscardedList() == null) playerEvent.setDiscardedList(new ArrayList<>());
            playerEvent.getDiscardedList().add(card.getCardID());
        }
        this.hasBeenModified = true;
    }

    /**
     * Initial resource and leader card.
     *
     * @param chosenCards           the chosen cards
     * @param toInsertInPlayerEvent the to insert in player event
     */
    public void initialResourceAndLeaderCard(List<Integer> chosenCards, List<Pair<Resource, StorageType>> toInsertInPlayerEvent){
        clearPlayerEvent();
        playerEvent.setPlayerMove(Move.INITIAL_CHOICES);
        playerEvent.setLeaderCards(chosenCards);
        playerEvent.setInputResources(toInsertInPlayerEvent);
        this.hasBeenModified = true;
    }

    /**
     * Register market action.
     *
     * @param row                      the row
     * @param column                   the column
     * @param whiteMarbleSubstituition the white marble substituition
     * @param hasDiscarded             the has discarded
     * @param resourcesToDiscard       the resources to discard
     */
    public void registerMarketAction(int row, int column, HashMap<Resource, Integer> whiteMarbleSubstituition, boolean hasDiscarded, List<Resource> resourcesToDiscard){
        clearPlayerEvent();
        playerEvent.setPlayerMove(Move.MARKET_MOVE);
        playerEvent.setRow(row);
        playerEvent.setCol(column);
        playerEvent.setWhiteMarbleSubstitution(whiteMarbleSubstituition);
        playerEvent.setHasDiscarded(hasDiscarded);
        playerEvent.setOutputResources(resourcesToDiscard);
        this.hasBeenModified = true;
    }

    /**
     * Generic production event.
     *
     * @param inputResourcesWithDeposit the input resources with deposit
     * @param resource                  the output resource
     */
    public void genericProductionEvent(ArrayList<Pair<Resource, StorageType>> inputResourcesWithDeposit, Resource resource) {
        clearPlayerEvent();
        playerEvent.setPlayerMove(Move.PRODUCTION_POWER_REQUEST);
        playerEvent.setDevCard(-1);
        playerEvent.setLeaderCards(null);
        playerEvent.setInputResources(inputResourcesWithDeposit);
        ArrayList<Resource> outResources = new ArrayList<>();
        outResources.add(resource);
        playerEvent.setOutputResources(outResources);
        this.hasBeenModified = true;
    }

    /**
     * Dev card production event.
     *
     * @param devCard the dev card chosen
     */
    public void devCardProductionEvent(Integer devCard) {
        clearPlayerEvent();
        playerEvent.setPlayerMove(Move.PRODUCTION_POWER_REQUEST);
        playerEvent.setDevCard(devCard);
        playerEvent.setLeaderCards(null);
        playerEvent.setInputResources(null);
        playerEvent.setOutputResources(null);
        this.hasBeenModified = true;
    }

    /**
     * Lead card production event.
     *
     * @param leadCard               the lead card chosen
     * @param outputGenericResources the output generic resources
     */
    public void leadCardProductionEvent(Integer leadCard, ArrayList<Resource> outputGenericResources) {
        clearPlayerEvent();
        playerEvent.setPlayerMove(Move.PRODUCTION_POWER_REQUEST);
        playerEvent.setDevCard(-1);
        List<Integer> leadCards=new ArrayList<>();leadCards.add(leadCard);
        playerEvent.setLeaderCards(leadCards);
        playerEvent.setInputResources(null);
        playerEvent.setOutputResources(outputGenericResources);
        this.hasBeenModified = true;
    }

    /**
     * End production event.
     */
    public void endProductionEvent() {
        clearPlayerEvent();
        playerEvent.setPlayerMove(Move.PRODUCTION_POWER_END);
        playerEvent.setDevCard(-1);
        playerEvent.setLeaderCards(null);
        playerEvent.setInputResources(null);
        playerEvent.setOutputResources(null);
        this.hasBeenModified = true;
    }

    /**
     * Dev card purchase action.
     *
     * @param cardID       the card id
     * @param rowToInsert  the row to insert
     * @param colToInsert  the col to insert
     * @param numberofslot the numberofslot wher the purchased card must be placed
     */
    public void devCardPurchaseAction(int cardID, int rowToInsert, int colToInsert, int numberofslot) {
        clearPlayerEvent();
        playerEvent.setPlayerMove(Move.DEV_CARD_REQUEST);
        playerEvent.setDevCard(cardID);
        playerEvent.setRow(rowToInsert);
        playerEvent.setCol(colToInsert);
        playerEvent.setIndex(numberofslot);
        this.hasBeenModified = true;
    }

    /**
     * Returns whether or not the player is in their first turn (useful in SinglePlayer mode)
     *
     * @return is the player in their first turn?
     */
    public boolean isFirstTurn()
    {
        return firstTurn;
    }

    /**
     * Sets first turn flag to false
     */
    public void endFirstTurn()
    {
        this.firstTurn = false;
    }
}
