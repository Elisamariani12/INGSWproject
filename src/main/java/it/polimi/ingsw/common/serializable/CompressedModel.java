package it.polimi.ingsw.common.serializable;

import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.model.gamemanager.GameSession;
import it.polimi.ingsw.server.model.gamemanager.Player;
import it.polimi.ingsw.server.model.gameplay.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * The type Compressed model.
 */
public class CompressedModel {

    /**
     * The Player usernames.
     */
    private List<String> playerNames;
    /**
     * The Positions of the players on the faith tracks. (in SinglePlayer the opponent is player 2)
     */
    private List<Integer> positionsOfFaithTrack;
    /**
     * The values of the pope favours activation flags for all players. (in SinglePlayer the opponent is player 2)
     */
    private List<boolean[]> popeFavours;
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
    private List<Integer> aPHiddenChosenLeaderCards;
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
    /**
     * Has the session ended?
     */
    private boolean hasSessionEnded;
    /**
     * The array containing already activated Production Power's card IDs, if Generic Power is activated it contains -1
     */
    private ArrayList<Integer> turnAlreadyUsedProductionCardIDs;
    /**
     * Leader Board created at the end of the session
     */
    private LinkedHashMap<String, Integer> endOfSessionLeaderBoard;

    private ArrayList<Integer> aPInitialChooseLeaderCard;

    private List<List<ResourceStack>> allPlayersWD;

    private List<List<ResourceStack>> allPlayersSB;

    private List<List<ResourceStack>> allPlayersLeaderStorage;

    //Username of the player who sent the latest compressed model
    private String latestActionSource;

    /**
     * Gets active player initial chosen leader cards.
     *
     * @return the active player initial chosen leader cards
     */
    public ArrayList<Integer> getaPInitialChooseLeaderCard() {
        return aPInitialChooseLeaderCard;
    }

    /**
     * Sets active player initial chosen leader cards.
     *
     * @param gameSession the game session
     */
    public void setaPInitialChooseLeaderCard(GameSession gameSession) {
        LeaderCardSpace space = gameSession.getActivePlayer().getBoard().getLeaderCardSpace();
        //Select hidden cards
        List<LeaderCard> initialCards = space.getAllCards().stream().collect(Collectors.toList());
        initialCards.removeAll(space.getActiveCards());
        initialCards.removeAll(space.getChosenCards());
        this.aPInitialChooseLeaderCard = (ArrayList<Integer>) initialCards.stream()
                .map(LeaderCard::getCardID).collect(Collectors.toList());
    }

    /**
     * Inserts values from game session to the compressed model instance for the specified player
     *
     * @param username            Receiver of the compressed model
     * @param gameSession         Reference to the party's game session model
     * @param disconnectedPlayers the disconnected players
     */
    public void retrieveValues(String username, GameSession gameSession, List<String> disconnectedPlayers)
    {

        List<PersonalBoard> playerBoards = gameSession.getPlayerList().stream()
                .map(Player::getBoard).collect(Collectors.toList());

        this.playerNames = gameSession.getPlayerList().stream()
                .map(Player::getUsername).collect(Collectors.toList());

        this.aP = gameSession.getPlayerIndex(gameSession.getActivePlayer());

        setPositionsOfFaithTrack(playerBoards);
        setPopeFavours(playerBoards);

        //SinglePlayer only
        if(playerBoards.size() == 1)
        {
            this.positionsOfFaithTrack.add(gameSession.getLorenzoDeMedici().getFaithTrackPosition());
            boolean[] lorenzoPopeFavours = new boolean[3];
            for(int i = 1; i <= 3; i++) lorenzoPopeFavours[i-1] = gameSession.getLorenzoDeMedici().getPopeFavour(i);
            this.popeFavours.add(lorenzoPopeFavours);
        }

        //Market board
        this.marketState = gameSession.getMarketBoard().getSerializableState();
        this.externalMarketState = gameSession.getMarketBoard().getExternalResource();

        setDevCardGridState(gameSession);
        setDevCardSpace(playerBoards);

        //Only send hidden leader cards to the active player
        if(username.equals(gameSession.getActivePlayer().getUsername()))
        {
            setaPHiddenChosenLeaderCards(gameSession);
        }

        setAllPlayersActiveLeaderCards(playerBoards);
        setAllPlayersLeaderStorage(gameSession);
        setAllPlayersWD(gameSession);
        setaPSB(gameSession);
        setAlreadyActiveProductionCardIDs(gameSession);
        setCumulatedPlayerStorage(playerBoards);
        setaPInitialChooseLeaderCard(gameSession);
        this.pickedActionToken = gameSession.getLastPickedActionToken();
        this.playerMoveResponse = gameSession.getLastMoveResponse();
        this.turnState = gameSession.getTurnState();
        this.hasSessionStarted = gameSession.hasSessionStarted();
        this.disconnectedPlayers = disconnectedPlayers;
        this.hasSessionEnded = gameSession.hasSessionEnded();
        this.latestActionSource = gameSession.getLatestPlayerEventSrc();

        //End of session leader board
        if(hasSessionEnded)
        {
            this.endOfSessionLeaderBoard = new LinkedHashMap<>();
            for (Player player : gameSession.getOrderedScoreBoard().keySet()) {
                this.endOfSessionLeaderBoard.put(player.getUsername(), gameSession.getOrderedScoreBoard().get(player));
            }
        }

        //Singleplayer
        if(gameSession.getPlayerCount() == 1)
        {
            this.positionsOfFaithTrack.add(gameSession.getLorenzoDeMedici().getFaithTrackPosition());
        }
    }

    /**
     * Returns the username of the player the compressed model is intended for
     *
     * @return username player names
     */
    public List<String> getPlayerNames() {
        return playerNames;
    }

    /**
     * Get the positions on the faith track of all players.
     *
     * @return Positions on faith track
     */
    public List<Integer> getPositionsOfFaithTrack() {
        return positionsOfFaithTrack;
    }

    /**
     * Inserts the positions on the faith track for all players
     * @param playerBoards Ordered list of player's personal boards
     */
    private void setPositionsOfFaithTrack(List<PersonalBoard> playerBoards)
    {
        this.positionsOfFaithTrack = playerBoards.stream()
                .map(pb -> pb.getFaithTrack().getPosition()).collect(Collectors.toList());
    }

    /**
     * Sets the list of cards with a production power already used in the turn
     * @param gameSession Reference to the game session
     */
    private void setAlreadyActiveProductionCardIDs(GameSession gameSession) {
        PersonalBoard activePlayerBoard = gameSession.getActivePlayer().getBoard();
        turnAlreadyUsedProductionCardIDs = activePlayerBoard.getProductionPowerRegistry().getAlreadyUsedInActualTurnIDs();
    }

    /**
     * returns any resources contained in the additional storage of the leader cards of the active player
     *
     * @return the active player resources in leadercards storage
     */
    public List<ResourceStack> getaPLeaderStorage() {
        return aPLeaderStorage;
    }


    /**
     * Gets turn state.
     *
     * @return the turn state
     */
    public TurnState getTurnState() {
        return turnState;
    }

    /**
     * Gets the outcome of the player's request
     *
     * @return the player move response
     */
    public PlayerMoveResponse getPlayerMoveResponse() {
        return playerMoveResponse;
    }

    /**
     * Gets picked action token.
     *
     * @return the picked action token
     */
    public ActionTokenType getPickedActionToken() {
        return pickedActionToken;
    }

    /**
     * Returns active player resources inserted in the strongbox
     *
     * @return four resource stack for the 4 types of resources
     */
    public List<ResourceStack> getaPSB() {
        return aPSB;
    }

    /**
     * Sets the all and active player's strong box storage
     *
     * @param gameSession Instance of the game session for this party
     */
    public void setaPSB(GameSession gameSession) {

        this.allPlayersSB = new ArrayList<>();

        for(int playerIndex = 0; playerIndex < gameSession.getPlayerCount(); playerIndex++) {
            PersonalBoard currentBoard = gameSession.getPlayerByIndex(playerIndex).getBoard();
            List<ResourceStack> playerStrongbox = new ArrayList<>();
            for(Resource type : GameConstants.STORAGE_COMPATIBLE_RESOURCES)
            {
                int amount = currentBoard.getStrongBox().getResourceAmount(type);
                try
                {
                    ResourceStack stackBuffer = new ResourceStack(type, amount, amount);
                    playerStrongbox.add(stackBuffer);
                }
                catch(FullResourceStackException e) {e.printStackTrace();}
            }
            //Active player's strongbox
            if(currentBoard == gameSession.getActivePlayer().getBoard()){
                this.aPSB = playerStrongbox;
            }
            this.allPlayersSB.add(playerStrongbox);
        }
    }

    /**
     * Get active player resources inserted in the warehousedepot
     *
     * @return four resource stack for the 4 types of resources
     */
    public List<ResourceStack> getaPWD() {
        return aPWD;
    }

    /**
     * Sets the all and active player's warehouse depot storage
     *
     * @param gameSession Instance of the game session for this party
     */
    public void setAllPlayersWD(GameSession gameSession) {
        this.allPlayersWD = new ArrayList<>();

        for(int playerIndex = 0; playerIndex < gameSession.getPlayerCount(); playerIndex++){
            PersonalBoard currentBoard = gameSession.getPlayerByIndex(playerIndex).getBoard();
            List<ResourceStack> playerWarehouseDepot = new ArrayList<>();
            for(int i = 1; i <=GameConstants.WAREHOUSE_DEPOT_NUMBER_OF_LAYERS; i++)
            {
                int amount = currentBoard.getWarehouseDepot().getLayerAmount(i);
                Resource type = currentBoard.getWarehouseDepot().getLayerType(i);
                try
                {
                    ResourceStack stackBuffer = new ResourceStack(type, amount, amount);
                    playerWarehouseDepot.add(stackBuffer);
                }
                catch(FullResourceStackException e) {e.printStackTrace();}
            }
            //Active player's warehouse depot
            if(currentBoard == gameSession.getActivePlayer().getBoard()){
                this.aPWD = playerWarehouseDepot;
            }

            this.allPlayersWD.add(playerWarehouseDepot);
        }
    }

    /**
     * Returns the resources contained in all of the player's storages
     *
     * @return A list of all players resources
     */
    public List<ResourceStack[]> getCumulatedPlayerStorage() {
        return cumulatedPlayerStorage;
    }

    /**
     * Sets the player's cumulated storage
     *
     * @param playerBoards Ordered list of all player's personal boards
     */
    public void setCumulatedPlayerStorage(List<PersonalBoard> playerBoards) {
        //Player cumulated storage
        List<ResourceStack[]> playersCumulatedStorage = new ArrayList<>();

        //For each player
        for(PersonalBoard board : playerBoards)
        {
            //List all owned resources and amounts
            List<ResourceStack> playerInventory = new ArrayList<>();
            for(Resource type : GameConstants.STORAGE_COMPATIBLE_RESOURCES)
            {
                int amount = board.getTotalStoredAmount(type);
                try
                {
                    ResourceStack stackBuffer = new ResourceStack(type, amount, amount);
                    playerInventory.add(stackBuffer);
                }
                catch(FullResourceStackException e) {e.printStackTrace();}
            }

           //Safely turn the inner list into an array
            ResourceStack[] rigidPlayerInventory = new ResourceStack[playerInventory.size()];
            for(int i = 0; i < rigidPlayerInventory.length; i++)
            {
                rigidPlayerInventory[i] = playerInventory.get(i);
            }

            //Add the player's possessions to the list
            playersCumulatedStorage.add(rigidPlayerInventory);
        }

        this.cumulatedPlayerStorage = playersCumulatedStorage;
    }


    /**
     * Sets the active player index
     *
     * @param aP index of the active player
     */
    @Deprecated
    public void setaP(int aP) {
        this.aP = aP;
    }

    /**
     * Returns the active player index
     *
     * @return the index of the active player
     */
    public int getaP() {
        return aP;
    }

    /**
     * Returns active leader cards for all players
     *
     * @return Active leader cards for each player
     */
    public List<List<Integer>> getAllPlayersActiveLeaderCards() {
        return allPlayersActiveLeaderCards;
    }

    /**
     * Sets Active leader cards for all players
     *
     * @param playerBoards Ordered list of personal boards
     */
    public void setAllPlayersActiveLeaderCards(List<PersonalBoard> playerBoards) {
        //Active leader cards for all players
        List<List<Integer>> playerActiveLeaderCards = new ArrayList<>();
        //For each player
        for(PersonalBoard board : playerBoards)
        {
            //Create a list of all active leader card ids and add it to the general list
            List<Integer> currentActiveCards = board.getLeaderCardSpace().getActiveCards().stream()
                    .map(LeaderCard::getCardID).collect(Collectors.toList());
            playerActiveLeaderCards.add(currentActiveCards);
        }
        this.allPlayersActiveLeaderCards = playerActiveLeaderCards;

    }

    /**
     * Sets the active and all player's leader storage
     *
     * @param gameSession Instance of the game session for this party
     */
    public void setAllPlayersLeaderStorage(GameSession gameSession) {
        //Leader Storage
        this.allPlayersLeaderStorage = new ArrayList<>();

        for(int playerIndex = 0; playerIndex < gameSession.getPlayerCount(); playerIndex++) {
            PersonalBoard currentBoard = gameSession.getPlayerByIndex(playerIndex).getBoard();
            //Active Player
            List<ResourceStack> leaderStorage = currentBoard.getLeaderCardSpace().getActiveStorageCards().stream()
                    .map(Pair::getSecond).collect(Collectors.toList());
            if (currentBoard == gameSession.getActivePlayer().getBoard()) {
                this.aPLeaderStorage = leaderStorage;
            }
            allPlayersLeaderStorage.add(leaderStorage);
        }
    }

    /**
     * Returns Active player's hidden leader card IDs
     *
     * @return the Ids of Active player hidden leader cards
     */
    public List<Integer> getaPHiddenChosenLeaderCards() {
        return aPHiddenChosenLeaderCards;
    }

    /**
     * Sets the hidden leader cards for the active player
     *
     * @param gameSession Instance of the game session for this party
     */
    public void setaPHiddenChosenLeaderCards(GameSession gameSession) {
        LeaderCardSpace space = gameSession.getActivePlayer().getBoard().getLeaderCardSpace();
        //Select hidden cards
        List<LeaderCard> hiddenCards = space.getChosenCards().stream().collect(Collectors.toList());
        hiddenCards.removeAll(space.getActiveCards());

        this.aPHiddenChosenLeaderCards = hiddenCards.stream()
                .map(LeaderCard::getCardID).collect(Collectors.toList());
    }


    /**
     * Returns devCardSpace state for each player
     *
     * @return devCardSpace state for each player, List of player that contains a List of 3 Stacks : the devCardSpace
     */
    public List<List<Stack<Integer>>> getDevCardSpace() {
        return devCardSpace;
    }

    /**
     * Sets devCardSpace state for each player
     *
     * @param playerBoards Ordered list of player personal boards
     */
    public void setDevCardSpace(List<PersonalBoard> playerBoards) {
        //Dev card space for each player
        List<List<Stack<Integer>>> devCardSpaces = new ArrayList<>();
        //For all players
        for(PersonalBoard board : playerBoards)
        {
            DevelopmentCardSpace space = board.getDevelopmentCardSpace();
            List<Stack<Integer>> playerDevCardSpace = new ArrayList<>();
            //For all leader card space decks
            for(int i = 1; i <= GameConstants.DEV_CARD_NUMBER_OF_SPACES; i++)
            {
                //Convert deck to stack of ids
                Stack<Integer> deck_i = new Stack<>();
                deck_i.addAll(space.getDevelopmentCardDeck(i).getSerializableState().stream()
                        .map(DevelopmentCard::getCardID).collect(Collectors.toList()));
                playerDevCardSpace.add(deck_i);
            }
            devCardSpaces.add(playerDevCardSpace);
        }
        this.devCardSpace = devCardSpaces;
    }

    /**
     * Get all highest cards ID in the development card grid
     *
     * @return ID of the highest cards in development card grid
     */
    public int[][] getDevCardGridState() {
        return devCardGridState;
    }

    /**
     * Sets the state of the Development Card grid
     *
     * @param gameSession Instance of the game session for this party
     */
    public void setDevCardGridState(GameSession gameSession) {
        //Dev Card grid
        DevelopmentCardGrid grid = gameSession.getDevelopmentCardGrid();

        int[][] serializableDevCardGrid = new int[GameConstants.DEV_CARD_GRID_ROWS_COUNT][GameConstants.DEV_CARD_GRID_COLS_COUNT];
        for(int j = 0; j < GameConstants.DEV_CARD_GRID_ROWS_COUNT; j++)
        {
            for(int i = 0; i < GameConstants.DEV_CARD_GRID_COLS_COUNT; i++)
            {
                DevelopmentCard card = grid.getDeck(j, i) == null ? null : grid.getDeck(j, i).peekElement();
                serializableDevCardGrid[j][i] = card == null ? -1 : card.getCardID();
            }
        }
        this.devCardGridState = serializableDevCardGrid;
    }

    /**
     * Returns external marble int the market
     *
     * @return the type of resource of the external marble
     */
    public Resource getExternalMarketState() {
        return externalMarketState;
    }


    /**
     * Returns the state of the market
     *
     * @return the grid of resources in the market
     */
    public Resource[][] getMarketState() {
        return marketState;
    }

    /**
     * Returns whether or not the session has started
     *
     * @return Has the session started?
     */
    public boolean getHasSessionStarted() {return hasSessionStarted;}

    /**
     * Returns a list of players who have lost connection to the server
     *
     * @return List of disconnected players
     */
    public List<String> getDisconnectedPlayers() {return  disconnectedPlayers;}

    /**
     * Returns whether or not the session has ended
     *
     * @return Has the session ended?
     */
    public boolean getHasSessionEnded()
    {
        return hasSessionEnded;
    }

    /**
     * Returns already Used production card IDS, -1 stands for generic production already activated
     *
     * @return a list of int Card IDs
     */
    public ArrayList<Integer> getTurnAlreadyUsedProductionCardIDs() {
        return turnAlreadyUsedProductionCardIDs;
    }

    /**
     * Returns an ordered score board corresponding for the game session (if it has ended)
     *
     * @return Map of Player's Username -> Score
     */
    public LinkedHashMap<String, Integer> getEndOfSessionLeaderBoard()
    {
        return endOfSessionLeaderBoard;
    }

    /**
     * Retrieves pope favour flags from player's personal boards
     *
     * @param personalBoards List of personal boards
     */
    public void setPopeFavours(List<PersonalBoard> personalBoards)
    {
        this.popeFavours = new ArrayList<>();

        List<FaithTrack> faithTrackList = personalBoards.stream().map(PersonalBoard::getFaithTrack).collect(Collectors.toList());
        for(FaithTrack ft : faithTrackList)
        {
            boolean[] playerPopeFavours = new boolean[3];
            for(int i = 1; i <= 3; i++)
            {
                playerPopeFavours[i - 1] = ft.getPopeFavour(i);
            }

            this.popeFavours.add(playerPopeFavours);
        }
    }

    /**
     * Returns the pope favour activation flags for all players (in SinglePlayer the opponent is player 2)
     *
     * @return Pope favour flags for all players
     */
    public List<boolean[]> getPopeFavours()
    {
        return popeFavours;
    }


    /**
     * Get selected player wd list.
     *
     * @param playerNumber the player number
     * @return the list
     */
    public List<ResourceStack> getSelectedPlayerWD(int playerNumber){
        if(playerNumber < 0 || playerNumber >= allPlayersWD.size()){
            return null;
        }
        return allPlayersWD.get(playerNumber);
    }

    /**
     * Get selected player sb list.
     *
     * @param playerNumber the player number
     * @return the list
     */
    public List<ResourceStack> getSelectedPlayerSB(int playerNumber){
        if(playerNumber < 0 || playerNumber >= allPlayersWD.size()){
            return null;
        }
        return new ArrayList<>(allPlayersSB.get(playerNumber));
    }


    /**
     * Get selected player leader storage list.
     *
     * @param playerNumber the player number
     * @return the list
     */
    public List<ResourceStack> getSelectedPlayerLeaderStorage(int playerNumber){
        if(playerNumber < 0 || playerNumber >= allPlayersWD.size()){
            return null;
        }
        return new ArrayList<>(allPlayersLeaderStorage.get(playerNumber));
    }

    /** Returns the username of the player who last sent a PlayerEvent
     * @return The players' username
     */
    public String getLatestActionSource()
    {
        return latestActionSource;
    }

}
