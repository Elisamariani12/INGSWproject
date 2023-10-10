package it.polimi.ingsw.mocks;

import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.ActionTokenType;
import it.polimi.ingsw.common.util.PlayerMoveResponse;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CompressedModelMock extends CompressedModel {
    private List<String> playerNames;
    /**
     * The Positions of the players on the faith tracks.
     */
    private List<Integer> positionsOfFaithTrack;
    /**
     * The Market state.
     */
    private Resource[][] marketState;
    /**
     * The External marble in the market.
     */
    private Resource externalMarketState;
    /**
     * The Development card grid
     */
    private int[][] devCardGridState;
    /**
     * The Development card space.
     */
    private List<List<Stack<Integer>>> devCardSpace;
    /**
     * The Active player index
     */
    private int aP;
    /**
     * The Active player hidden leader cards ID
     */
    private List<Integer> aPHiddenLeaderCards;
    /**
     * The active leader cards of every player
     */
    private List<List<Integer>> allPlayersActiveLeaderCards;
    /**
     * The Active player's possible resources stacked in active leader cards
     */
    private List<ResourceStack> aPLeaderStorage;
    /**
     * The resources of the players in all of their deposits.
     */
    private List<ResourceStack[]> cumulatedPlayerStorage;
    /**
     * The Active player WareHouseDepot
     */
    private List<ResourceStack> aPWD; //refers to activePLayer's warehouse depot
    /**
     * The Active player strongbox
     */
    private List<ResourceStack> aPSB; //refers to activePLayer's strongBox
    /**
     * The Picked action token.
     */
    private ActionTokenType pickedActionToken;
    /**
     * The outcome of the player's move: rejected or success
     */
    private PlayerMoveResponse playerMoveResponse;
    /**
     * The Turn state.
     */
    private TurnState turnState;
    /**
     * Has the game session started?
     */
    private boolean hasSessionStarted;
    /**
     * List of disconnected players
     */
    private List<String> disconnectedPlayers;

    private ArrayList<Integer> turnAlreadyUsedProductionCardIDs;

    @Override
    public ArrayList<Integer> getTurnAlreadyUsedProductionCardIDs() {
        return turnAlreadyUsedProductionCardIDs;
    }

    public void setTurnAlreadyUsedProductionCardIDs(ArrayList<Integer> turnAlreadyUsedProductionCardIDs) {
        this.turnAlreadyUsedProductionCardIDs = turnAlreadyUsedProductionCardIDs;
    }

    @Override
    public List<String> getPlayerNames() {
        return playerNames;
    }

    @Override
    public List<Integer> getPositionsOfFaithTrack() {
        return positionsOfFaithTrack;
    }

    @Override
    public List<ResourceStack> getaPLeaderStorage() {
        return aPLeaderStorage;
    }

    @Override
    public TurnState getTurnState() {
        return turnState;
    }

    @Override
    public PlayerMoveResponse getPlayerMoveResponse() {
        return playerMoveResponse;
    }

    @Override
    public ActionTokenType getPickedActionToken() {
        return pickedActionToken;
    }

    @Override
    public List<ResourceStack> getaPSB() {
        return aPSB;
    }

    @Override
    public List<ResourceStack> getaPWD() {
        return aPWD;
    }


    @Override
    public List<ResourceStack[]> getCumulatedPlayerStorage() {
        return cumulatedPlayerStorage;
    }

    @Override
    public int getaP() {
        return aP;
    }

    @Override
    public List<List<Integer>> getAllPlayersActiveLeaderCards() {
        return allPlayersActiveLeaderCards;
    }

    @Override
    public List<Integer> getaPHiddenChosenLeaderCards() {
        return aPHiddenLeaderCards;
    }

    @Override
    public List<List<Stack<Integer>>> getDevCardSpace() {
        return devCardSpace;
    }

    @Override
    public int[][] getDevCardGridState() {
        return devCardGridState;
    }

    @Override
    public Resource getExternalMarketState() {
        return externalMarketState;
    }

    @Override
    public Resource[][] getMarketState() {
        return marketState;
    }

    public void setPlayerNamesMock(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public void setPositionsOfFaithTrackMock(List<Integer> positionsOfFaithTrack) {
        this.positionsOfFaithTrack = positionsOfFaithTrack;
    }

    public void setMarketStateMock(Resource[][] marketState) {
        this.marketState = marketState;
    }

    public void setExternalMarketStateMock(Resource externalMarketState) {
        this.externalMarketState = externalMarketState;
    }

    public void setDevCardGridStateMock(int[][] devCardGridState) {
        this.devCardGridState = devCardGridState;
    }

    public void setDevCardSpaceMock(List<List<Stack<Integer>>> devCardSpace) {
        this.devCardSpace = devCardSpace;
    }

    public void setaPMock(int aP) {
        this.aP = aP;
    }

    public void setaPHiddenLeaderCardsMock(List<Integer> aPHiddenLeaderCards) {
        this.aPHiddenLeaderCards = aPHiddenLeaderCards;
    }

    public void setAllPlayersActiveLeaderCardsMock(List<List<Integer>> allPlayersActiveLeaderCards) {
        this.allPlayersActiveLeaderCards = allPlayersActiveLeaderCards;
    }

    public void setaPLeaderStorageMock(List<ResourceStack> aPLeaderStorage) {
        this.aPLeaderStorage = aPLeaderStorage;
    }

    public void setCumulatedPlayerStorageMock(List<ResourceStack[]> cumulatedPlayerStorage) {
        this.cumulatedPlayerStorage = cumulatedPlayerStorage;
    }

    public void setaPWDMock(List<ResourceStack> aPWD) {
        this.aPWD = aPWD;
    }

    public void setaPSBMock(List<ResourceStack> aPSB) {
        this.aPSB = aPSB;
    }

    public void setPickedActionTokenMock(ActionTokenType pickedActionToken) {
        this.pickedActionToken = pickedActionToken;
    }

    public void setPlayerMoveResponseMock(PlayerMoveResponse playerMoveResponse) {
        this.playerMoveResponse = playerMoveResponse;
    }

    public void setTurnStateMock(TurnState turnState) {
        this.turnState = turnState;
    }

    public void setHasSessionStartedMock(boolean hasSessionStarted) {
        this.hasSessionStarted = hasSessionStarted;
    }

    public void setDisconnectedPlayersMock(List<String> disconnectedPlayers) {
        this.disconnectedPlayers = disconnectedPlayers;
    }
}
