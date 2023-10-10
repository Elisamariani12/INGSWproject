package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.common.model.ActionToken;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.util.ActionTokenType;
import it.polimi.ingsw.common.util.Deck;
import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.PlayerMoveResponse;
import it.polimi.ingsw.server.exceptions.PlayerCountOutOfBoundsException;
import it.polimi.ingsw.server.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.server.model.gameplay.DevelopmentCardGrid;
import it.polimi.ingsw.server.model.gameplay.LorenzoDeMedici;
import it.polimi.ingsw.server.model.gameplay.MarketBoard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents a unique game session
 */
public class GameSession
{
    //In-game players
    private List<Player> players;
    private int activePlayer;

    private boolean hasStarted = false;

    private List<Boolean> isPlayersInitialChoiceDone;
    private Deck<ActionToken> actionTokens;
    private Deck<LeaderCard> leaderCards;
    private DevelopmentCardGrid developmentCardGrid;
    private MarketBoard marketBoard;
    private LorenzoDeMedici lorenzo;
    //Represents the state of the active player's turn
    private it.polimi.ingsw.common.model.TurnState turnState;
    private boolean isLastRound;
    //What was the result of the last player move?
    private PlayerMoveResponse lastMoveResponse = PlayerMoveResponse.REJECTED;
    //Last picked action token
    private ActionTokenType lastPickedActionToken = null;
    private boolean hasSessionEnded = false;

    //Represent final scores of the players, it gets update only with calculateScores() method in GameMode at the end of the game
    private LinkedHashMap<Player,Integer> OrderedScoreBoard;

    //Username to answer to in case of rejected actions
    private String latestPlayerEventSrc;

    /**
     * Creates a new game session (should only be used by the GameMode controller)
     *
     * @param actionTokens A well shuffled deck of all action tokens
     * @param leaderCards  A well shuffled deck of all leader cards
     */
    public GameSession(Deck<ActionToken> actionTokens, Deck<LeaderCard> leaderCards)
    {
        players = new ArrayList<Player>();
        developmentCardGrid = new DevelopmentCardGrid();
        marketBoard = new MarketBoard();
        OrderedScoreBoard = new LinkedHashMap<>();
        this.actionTokens = actionTokens;
        this.leaderCards = leaderCards;
        isLastRound = false;
        lorenzo = new LorenzoDeMedici();
        turnState = null;
        isPlayersInitialChoiceDone = new ArrayList<>();
    }

    /**
     * Returns whether or not the session has started
     *
     * @return Has the session started?
     */
    public boolean hasSessionStarted()
    {
        return hasStarted;
    }

    /**
     * Is player choose phase done boolean.
     *
     * @param playerNumber the player number
     * @return is player choose phase done?
     */
    public boolean isPlayerChoosePhaseDone(int playerNumber) {
        return this.isPlayersInitialChoiceDone.get(playerNumber);
    }

    /**
     * Sets player choose phase as done.
     *
     * @param playerNumber the player number
     */
    public void setPlayerChoosePhaseAsDone(int playerNumber) {
        this.isPlayersInitialChoiceDone.set(playerNumber, true);
    }

    /**
     * Returns the common development card space
     *
     * @return Development card grid object
     */
    public DevelopmentCardGrid getDevelopmentCardGrid()
    {
        return developmentCardGrid;
    }

    /**
     * Returns the common market board
     *
     * @return Market board object
     */
    public MarketBoard getMarketBoard()
    {
        return marketBoard;
    }

    /**
     * Adds a player to the session
     *
     * @param player The player that joins the session
     * @throws PlayerCountOutOfBoundsException the player count out of bounds exception
     */
    public void addPlayer(Player player) throws PlayerCountOutOfBoundsException
    {
        if(players.size() >= GameConstants.MAX_PLAYER_COUNT) throw new PlayerCountOutOfBoundsException();
        else{
            players.add(player);
            isPlayersInitialChoiceDone.add(false);
        }
    }

    /**
     * Returns the player currently having his turn
     *
     * @return Active player
     */
    public Player getActivePlayer()
    {
        return players.get(activePlayer);
    }

    /**
     * Returns a list of all players who have joined the session
     *
     * @return List of players
     */
    public List<Player> getPlayerList()
    {
        return players;
    }

    /**
     * Returns the player at a specified index
     *
     * @param index Index of the player
     * @return Player at index
     */
    public Player getPlayerByIndex(int index)
    {
        try{
            return players.get(index);
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Returns the index associated to the player
     *
     * @param player Player of which we require the index
     * @return Plyer 's index
     * @throws PlayerNotFoundException the player not found exception
     */
    public int getPlayerIndex(Player player) throws PlayerNotFoundException
    {
        for(int n = 0; n < players.size(); n++)
            if(player == players.get(n)) return n;

        throw new PlayerNotFoundException();
    }

    /**
     * Returns the count of all players who have joined the session
     *
     * @return Player count
     */
    public int getPlayerCount()
    {
        return players.size();
    }

    /**
     * Returns the common deck of action tokens for this session
     *
     * @return Deck of action tokens
     */
    public ActionToken pickActionToken()
    {
        ActionToken token = actionTokens.peekAndReinsert();
        lastPickedActionToken = token.getType();
        return token;
    }

    /**
     * Shuffle action token.
     */
    public void shuffleActionTokens(){
        actionTokens.shuffle();
    }

    /**
     * Returns the common deck of leader cards for this session
     *
     * @return Deck of leader cards
     */
    public Deck<LeaderCard> getLeaderCardDeck()
    {
        return leaderCards;
    }

    /**
     * Returns the object representing the opponent for the SinglePlayer game mode
     *
     * @return Unique LorenzoDeMedici object for this session
     */
    public LorenzoDeMedici getLorenzoDeMedici()
    {
        return lorenzo;
    }

    /**
     * Starts the game with the players associated to the current session
     */
    public void startSession()
    {
        hasStarted = true;
        activePlayer = 0;
    }

    /**
     * Changes the current player to the next one on the list
     */
    public void nextTurn()
    {
        activePlayer++;
        if(activePlayer >= getPlayerCount()) activePlayer = 0;
    }

    /**
     * Returns the current turn state
     *
     * @return Current turn state
     */
    public it.polimi.ingsw.common.model.TurnState getTurnState()
    {
        return turnState;
    }

    /**
     * Sets the turn state
     *
     * @param state New turn state
     */
    public void setTurnState(it.polimi.ingsw.common.model.TurnState state)
    {
        this.turnState = state;
    }

    /**
     * Returns whether or not this is the last round
     *
     * @return the boolean
     */
    public boolean getIsLastRound() {
        return isLastRound;
    }

    /**
     * Sets the game session to the last round.
     */
    public void setIsLastRound() {
        isLastRound = true;
    }

    /**
     * Gets score board.
     *
     * @return the score board ordered by scores: the player who reached highest score is in first position, other positions are casual
     */
    public LinkedHashMap<Player,Integer> getOrderedScoreBoard() { return OrderedScoreBoard; }

    /**
     * Returns the outcome of the last action: success or rejected
     *
     * @return the last move response
     */
    public PlayerMoveResponse getLastMoveResponse() {
        return lastMoveResponse;
    }

    /**
     * Set the outcome of the last action: success or rejected
     *
     * @param lastMoveResponse the last move response
     */
    public void endMove(PlayerMoveResponse lastMoveResponse) { this.lastMoveResponse = lastMoveResponse; }

    /**
     * Returns the last action token type picked
     *
     * @return The action token type corresponding to the latest pick
     */
    public ActionTokenType getLastPickedActionToken()
    {
        return lastPickedActionToken;
    }

    /**
     * Returns whether or not the game session has ended
     *
     * @return Has the session ended?
     */
    public boolean hasSessionEnded()
    {
        return hasSessionEnded;
    }

    /**
     * Sets the session state to ended
     */
    public void endSession()
    {
        hasSessionEnded = true;
    }

    /**
     * Shuffle players order.
     */
    public void shufflePlayersOrder(){
        Collections.shuffle(players);
    }

    /**
     * Sets the latest player event source username
     * @param username The player's username
     */
    public void setLatestPlayerEventSrc(String username)
    {
        this.latestPlayerEventSrc = username;
    }

    /** Returns the latest player event source username
     * @return Player event source username
     */
    public String getLatestPlayerEventSrc()
    {
        return this.latestPlayerEventSrc;
    }


}

