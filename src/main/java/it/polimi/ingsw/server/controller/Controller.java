package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.Move;
import it.polimi.ingsw.common.util.Observer;
import it.polimi.ingsw.common.util.PlayerMoveResponse;
import it.polimi.ingsw.server.exceptions.PlayerMoveException;
import it.polimi.ingsw.server.model.gamemanager.GameSession;
import it.polimi.ingsw.server.model.gamemanager.Player;
import it.polimi.ingsw.server.view.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MVC Controller component
 */
public class Controller implements Observer<PlayerEvent>
{
    //Host of the game party
    private final String sessionHost;
    private final int playerCount;
    private GameMode gameMode;
    private TurnState turnState;
    private List<String> disconnectedPlayers;
    //Debug only
    private boolean disableActionTokens = false;

    /**
     * Creates a new controller
     *
     * @param sessionHost Username of the game party's host
     * @param playerCount Count of players allowed in the party
     */
    public Controller(String sessionHost, int playerCount)
    {
        this.sessionHost = sessionHost;
        this.playerCount = playerCount;
        this.turnState = TurnState.SETUP_PLAYER_CHOICES;
        if(playerCount == 1){
            gameMode = new SinglePlayer();
        }
        else{
            gameMode = new MultiPlayer();
        }
        disconnectedPlayers = new ArrayList<>();
    }

    /**
     * Add the given virtualview to the Observers of the model
     * @param virtualView to make observer of the model
     */
    public synchronized void addObserverToTheModel(VirtualView virtualView){
        gameMode.getModel().addObserver(virtualView);
    }

    /**
     * Submit an open connection to this controller
     * @param username Player's username
     */
    public synchronized void submitConnection(String username)
    {
        Player player=new Player(username);
        gameMode.addPlayerToSession(player);

        if(gameMode.getModel().getGameSession().getPlayerList().size() == playerCount)
        {
            gameMode.getModel().getGameSession().startSession();
            gameMode.setupGame();
        }
        gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
        gameMode.getModel().notify(gameMode.getModel().getGameSession());

    }

    @Override
    public synchronized void update(PlayerEvent action)
    {
        GameSession gameSession = gameMode.getModel().getGameSession();

        //it check if Controller has to call TurnState methods to check if the move is valid, false means that the move is already invalid
        boolean actionToDo = false;
        //nextState represent the next player move needed after this Turnstate.apply
        TurnState nextState = null;

        String srcUsername = action.getPlayerUserName();
        boolean isMoveFromActivePlayer = srcUsername.equals(gameSession.getActivePlayer().getUsername());
        gameMode.getModel().getGameSession().setLatestPlayerEventSrc(srcUsername);

        boolean changeTurnAtTheEnd = false;
        boolean skipActionToken=false;


        if(isMoveFromActivePlayer)
        {

            switch(action.getPlayerMove()){

                case INITIAL_CHOICES:
                    if(turnState == TurnState.SETUP_PLAYER_CHOICES ){
                        actionToDo = true;

                        //the next player has to choose his initial resources too
                        nextState = TurnState.SETUP_PLAYER_CHOICES;
                        changeTurnAtTheEnd = true;
                        //if every player has chosen his initial resources, next player message is going to be a LEADER_ACTION type
                        if(gameSession.isPlayerChoosePhaseDone(getNextConnectedPlayerIndex()) || gameSession.getPlayerByIndex(getNextConnectedPlayerIndex()) == gameSession.getActivePlayer()){
                            nextState = TurnState.INITIAL_LEADER_ACTION;
                        }
                        else{
                            nextState = TurnState.SETUP_PLAYER_CHOICES;
                        }
                        skipActionToken=true;
                    }
                    break;

                case LEADER_ACTION:
                    if(turnState == TurnState.INITIAL_LEADER_ACTION){
                        actionToDo = true;
                        nextState = TurnState.CHOOSE_ACTION;

                    }
                    if(turnState == TurnState.FINAL_LEADER_ACTIONS){
                        actionToDo = true;
                        changeTurnAtTheEnd = true;
                        //check if the player can choose to activate a card, he could have activated or discarded all his leaderCards
                        if(!(gameSession.getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards().size() == gameSession.getActivePlayer().getBoard().getLeaderCardSpace().getChosenCards().size())){
                            if(!gameSession.isPlayerChoosePhaseDone(getNextConnectedPlayerIndex())){
                                nextState = TurnState.SETUP_PLAYER_CHOICES;
                            }
                            else{
                                nextState = TurnState.INITIAL_LEADER_ACTION;
                            }
                        }
                        else{
                            nextState = TurnState.CHOOSE_ACTION;
                        }
                    }
                    break;

                case MARKET_MOVE:
                    if(turnState == TurnState.CHOOSE_ACTION || turnState == TurnState.MARKET){
                        actionToDo = true;
                        turnState = TurnState.MARKET;
                        //check if the player can choose to activate a card, he could have activated or discarded all his leaderCards
                        if(!(gameSession.getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards().size() == gameSession.getActivePlayer().getBoard().getLeaderCardSpace().getChosenCards().size())){
                            nextState = TurnState.FINAL_LEADER_ACTIONS;
                        }
                        else{
                            if(gameSession.isPlayerChoosePhaseDone(getNextConnectedPlayerIndex()) || gameSession.getPlayerByIndex(getNextConnectedPlayerIndex()) == gameSession.getActivePlayer()){
                                nextState = TurnState.INITIAL_LEADER_ACTION;
                            }
                            else{
                                nextState = TurnState.SETUP_PLAYER_CHOICES;
                            }
                            changeTurnAtTheEnd = true;
                        }
                    }
                    break;

                case DEV_CARD_REQUEST:
                    if(turnState == TurnState.CHOOSE_ACTION || turnState == TurnState.DEV_CARD_PURCHASE){
                        actionToDo = true;
                        turnState = TurnState.DEV_CARD_PURCHASE;
                        //check if the player can choose to activate a card, he could have activated or discarded all his leaderCards
                        if(!(gameSession.getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards().size() == gameSession.getActivePlayer().getBoard().getLeaderCardSpace().getChosenCards().size())){
                            nextState = TurnState.FINAL_LEADER_ACTIONS;
                        }
                        else{
                            changeTurnAtTheEnd = true;
                            if(gameSession.isPlayerChoosePhaseDone(getNextConnectedPlayerIndex()) || gameSession.getPlayerByIndex(getNextConnectedPlayerIndex()) == gameSession.getActivePlayer()){
                                nextState = TurnState.INITIAL_LEADER_ACTION;
                            }
                            else{
                                nextState = TurnState.SETUP_PLAYER_CHOICES;
                            }
                        }
                    }
                    break;

                case PRODUCTION_POWER_REQUEST:
                    if(turnState == TurnState.CHOOSE_ACTION || turnState == TurnState.PRODUCTION_PHASE){
                        actionToDo = true;
                        turnState = TurnState.PRODUCTION_PHASE;
                        nextState = TurnState.PRODUCTION_PHASE;
                    }
                    break;

                case PRODUCTION_POWER_END:
                    if(turnState == TurnState.PRODUCTION_PHASE){
                        actionToDo = true;
                        turnState = TurnState.CLOSE_TRANSACTIONS;
                        //check if the player can choose to activate a card, he could have activated or discarded all his leaderCards
                        if(!(gameSession.getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards().size() == gameSession.getActivePlayer().getBoard().getLeaderCardSpace().getChosenCards().size())){
                            nextState = TurnState.FINAL_LEADER_ACTIONS;
                        }
                        else
                        {
                            if(gameSession.isPlayerChoosePhaseDone(getNextConnectedPlayerIndex()) || gameSession.getPlayerByIndex(getNextConnectedPlayerIndex()) == gameSession.getActivePlayer()){
                                nextState = TurnState.INITIAL_LEADER_ACTION;
                            }
                            else{
                                nextState = TurnState.SETUP_PLAYER_CHOICES;
                            }
                            changeTurnAtTheEnd = true;
                        }
                    }
                    break;
                case DISCONNECTION:
                    disconnectedPlayers.add(gameSession.getActivePlayer().getUsername());
                    //If all players are disconnected, end the session
                    if(disconnectedPlayers.size() == playerCount)
                    {
                        gameSession.endSession();
                        return;
                    }
                    //Setup next turn
                    turnState = TurnState.DISCONNECTION;
                    //When the first player create a match with n player and he disconnects before somebody else join it
                    if(getNextConnectedPlayerIndex() == -1){
                        gameSession.endSession();
                        return;
                    }
                    //If there are another player connected
                    if(gameSession.isPlayerChoosePhaseDone(getNextConnectedPlayerIndex())){
                        nextState = TurnState.INITIAL_LEADER_ACTION;
                    }
                    else{
                        nextState = TurnState.SETUP_PLAYER_CHOICES;
                    }
                    changeTurnAtTheEnd = true;
                    actionToDo = true;
                    break;

                default:
                    throw new PlayerMoveException();
            }
        }
        //A player (not the active player) has disconnected
        else if(action.getPlayerMove() == Move.DISCONNECTION)
        {
            disconnectedPlayers.add(action.getPlayerUserName());
            return;

        }
        //A player (not the active player) has reconnected
        else if(action.getPlayerMove() == Move.RECONNECTION)
        {
            disconnectedPlayers.remove(action.getPlayerUserName());
            return;
        }

        //Do turn's action i.e. call turnState.accept if the action is valid and set next state
        if(actionToDo)
        {
            turnState.accept(action, gameMode);

            if(gameSession.getLastMoveResponse() == PlayerMoveResponse.SUCCESS)
            {
                turnState = nextState;
            }

        }
        else
        {
            gameSession.endMove(PlayerMoveResponse.REJECTED);
        }

        //Advance turn if necessary
        if(changeTurnAtTheEnd && gameSession.getLastMoveResponse() == PlayerMoveResponse.SUCCESS)
        {
            gameSession.nextTurn();
            while (disconnectedPlayers.contains(gameSession.getActivePlayer().getUsername()))
            {
                gameSession.nextTurn();
            }
            if(gameSession.getIsLastRound() && gameSession.getActivePlayer().equals(gameSession.getPlayerList().get(0)))
                gameSession.endSession();

            //Singleplayer
            if(gameSession.getPlayerCount() == 1 && !disableActionTokens && !skipActionToken)
            {((SinglePlayer)gameMode).pickActionToken();}
            else{skipActionToken=false;}
        }

        //No action can be done after session has ended
        if(gameSession.hasSessionEnded())
        {
            gameMode.calculateScores();
            gameSession.endMove(PlayerMoveResponse.REJECTED);
            gameMode.getModel().notify(gameSession);
            return;
        }

        //Update the turn state in the model
        gameSession.setTurnState(turnState.getEquivalentModelState());

        //Notify changes to the model
        gameMode.getModel().notify(gameSession);
    }

    /**
     * Returns whether or not the game session is full
     * @return Is the session full?
     */
    public synchronized boolean isSessionFull()
    {
        return (playerCount - gameMode.getModel().getGameSession().getPlayerCount()) == 0;
    }

    /**
     * Returns whether or not the game session has ended
     * @return Has the session ended?
     */
    public synchronized boolean hasSessionEnded()
    {
        return gameMode.getModel().getGameSession().hasSessionEnded();
    }

    /**
     * Returns whether the username is already present in the gameSession or not
     * @param username the username to check for presence
     * @return 'true' if the username is alreasdy present in this game session, otherwhise 'false'
     */
    public synchronized boolean isUsernameAlreadyPresent(String username){
        List<String> listOfUsernames = gameMode.getModel().getGameSession().getPlayerList().stream().map(Player::getUsername).collect(Collectors.toList());
        if (listOfUsernames.contains(username)){return true;}
        else return false;
    }

    /**
     * Return a list of the usernames of the players that disconnected from their game
     * @return the list of players that disconnected from the game
     */
    public synchronized List<String> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    /** Used in test mode to disable action tokens in singleplayer
     * @param testMode Is test mode active?
     */
    public void setTestMode(boolean testMode)
    {
        this.disableActionTokens = true;
    }

    /** Used in SinglePlayer to send the model state to the player as they start the game */
    public synchronized void resendState()
    {
        gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
        gameMode.getModel().notify(gameMode.getModel().getGameSession());
    }

    /**
     * Return the next player that has to play and that is connected
     * @return the index of the next connected player
     */
    private int getNextConnectedPlayerIndex(){
        GameSession gameSession = gameMode.getModel().getGameSession();
        int activePlayerIndex = gameSession.getPlayerIndex(gameSession.getActivePlayer());
        //iterate from the active player to the end of the list searching for a connected player
        for(int i=activePlayerIndex+1; i<playerCount; i++){
            //When the first player create a match with n player and he disconnects before somebody else join it
            if(gameSession.getPlayerByIndex(i) == null){
                return -1;
            }
            if(!disconnectedPlayers.contains(gameSession.getPlayerByIndex(i).getUsername())){
                return i;
            }
        }
        //if no connected player is found, iterate from first player to the active player searching for a connected player
        for(int i=0; i<activePlayerIndex; i++){
            if(!disconnectedPlayers.contains(gameSession.getPlayerByIndex(i).getUsername())){
                return i;
            }
        }
        //If only the active player is connected
        return activePlayerIndex;
    }

}

