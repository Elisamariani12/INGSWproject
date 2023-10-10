package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.cli.scenes.*;
import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.*;

import java.util.*;

/**
 * View component in the Command Line Interface execution mode
 */
public class CLI implements BiObserver<String, CompressedModel>, BiObservable<String, PlayerEvent>
{
    //Used to get input from the command line
    private Scanner consoleScanner;

    //Internal buffer for the received compressed model
    private CompressedModel compressedModel;
    //CLI scene currently being displayed
    private TextScene currentScene;
    private Optional<String> previousRequest;
    private boolean wasDisconnected;
    private String username = "";
    private boolean isAnewMatch;
    private boolean alreadyAskedIfNewMatch;

    //BiObserver list
    private List<BiObserver<String, PlayerEvent>> biObservers;

    /** Creates a new instance of the the View component in the CLI execution mode */
    public CLI() {
        biObservers = new ArrayList<>();
        previousRequest = Optional.ofNullable(null);
        wasDisconnected = false;
    }

    @Override
    public void addObserver(BiObserver<String, PlayerEvent> observer) {
        biObservers.add(observer);
    }

    @Override
    public void removeObserver(BiObserver<String, PlayerEvent> observer) {
        biObservers.remove(observer);
    }

    @Override
    public void notify(String message1, PlayerEvent message2) {
        for(BiObserver<String, PlayerEvent> observer : biObservers) observer.update(message1, message2);
    }

    @Override
    public void update(String message1, CompressedModel message2) {
        boolean isStringMessage = (message2 == null);
        String username;

        if(isStringMessage)
        {
            switch(message1)
            {
                case GameConstants.WELCOME_ACTION:
                    if(!alreadyAskedIfNewMatch){ notify(askIsANewMatch() ? GameConstants.NEW_CONNECTION : GameConstants.RECONNECTING, null);}
                    else {notify(isAnewMatch?GameConstants.NEW_CONNECTION:GameConstants.RECONNECTING,null);}
                    break;
                case GameConstants.HOST_PLAYER_MESSAGE:
                    username = askPlayerName();
                    PlayerWorkSpace.getInstance().setPlayerWorkspaceUsername(username);
                    Integer playerCount = askNumberOfPlayers();
                    previousRequest = Optional.ofNullable(GameConstants.HOST_PLAYER_MESSAGE);
                    notify(username + "-" + playerCount.toString(), null);
                    break;
                case GameConstants.GUEST_PLAYER_MESSAGE:
                    username = askPlayerName();
                    PlayerWorkSpace.getInstance().setPlayerWorkspaceUsername(username);
                    previousRequest = Optional.ofNullable(GameConstants.GUEST_PLAYER_MESSAGE);
                    notify(username, null);
                    break;
                default:
                    notify(GameConstants.NACK, null);
                    break;
            }
        }
        //Compressed Model message
        else
        {
            this.compressedModel = message2;
            previousRequest = Optional.ofNullable(null);

            PlayerWorkSpace.getInstance().clearPlayerEvent();

            //SinglePlayer only
            boolean actionTokenExtraction = compressedModel.getPlayerNames().size() == 1 &&
                    compressedModel.getTurnState() == TurnState.INITIAL_LEADER_ACTION &&
                    compressedModel.getPlayerMoveResponse() == PlayerMoveResponse.SUCCESS &&
                    !PlayerWorkSpace.getInstance().isFirstTurn();

            if(actionTokenExtraction)
            {
                displayActionTokenResult();
                WaitingTurnScene showPos = new WaitingTurnScene();
                showPos.draw(compressedModel);
            }

            nextScene();
            currentScene.draw(compressedModel);
            currentScene.askInput(compressedModel);

            //Needed in SinglePlayer so the Action Token is not picked at the start of the game
            if(compressedModel.getTurnState() == TurnState.FINAL_LEADER_ACTIONS && PlayerWorkSpace.getInstance().isFirstTurn())
                PlayerWorkSpace.getInstance().endFirstTurn();

            //Only update server if required
            if(PlayerWorkSpace.getInstance().wasModified()){
                notify(null, PlayerWorkSpace.getInstance().getPlayerEventCopy());
            }

        }
    }

    /** Used to ask the player for a username upon connecting to a server
     * @return Player's username
     */
    private String askPlayerName()
    {
        consoleScanner = new Scanner(System.in);

        boolean hasAlreadyAsked = previousRequest.isPresent() &&
                (previousRequest.get().equals(GameConstants.HOST_PLAYER_MESSAGE) || previousRequest.get().equals(GameConstants.GUEST_PLAYER_MESSAGE));

        //If the username is already taken, say it and ask again
        if(isAnewMatch){
            System.out.println(hasAlreadyAsked ? GameConstants.PLAYER_USERNAME_RETRY_MESSAGE : GameConstants.ASK_PLAYER_NAME_MESSAGE);
        }
        //If the player wants to reconnect to an already started match, but he insert a new username
        else{
            System.out.println(hasAlreadyAsked ? GameConstants.PLAYER_USERNAME_NOT_FOUND_MESSAGE : GameConstants.ASK_ALREADY_CONNECTED_PLAYER_NAME_MESSAGE);
        }

        String usernameBuffer = "";

        //Wait for the player to type in a valid string (non blank and less than MAX_USERNAME_LENGTH)
        while(usernameBuffer.isBlank() || usernameBuffer.length() > GameConstants.MAX_USERNAME_LENGTH)
        {
            usernameBuffer = consoleScanner.nextLine();
            //Notify the player if the string is invalid
            if(usernameBuffer.isBlank() || usernameBuffer.length() > GameConstants.MAX_USERNAME_LENGTH)
                System.out.println(GameConstants.PLAYER_USERNAME_OUT_OF_BOUNDS);
        }

        username = usernameBuffer;

        return usernameBuffer;
    }

    /**
     * Ask if the player want to start a new match or if he wants to reconnect
     *
     * @return true if the player want to start a new match, false if he wants to reconnect
     */
    private boolean askIsANewMatch()
    {
        consoleScanner = new Scanner(System.in);

        //Inform the player they will have to type a username
        System.out.print(GameConstants.ASK_RECONNECTING_OR_NEW);
        boolean invalidInput = true;
        char choice;
        String input = null;
        //While the player has yet to produce a valid player count...
        while(invalidInput)
        {
            try
            {
                input = consoleScanner.nextLine();
                invalidInput = false;
            }
            //In case the player input doesn't match the Integer format
            catch(InputMismatchException e)
            {
                invalidInput = true;
                consoleScanner = new Scanner(System.in);
            }

            if(input.length() > 1)
            {
                invalidInput = true;
                 continue;
            }

            //Check whether or not the input is invalid
            try {
                choice = Character.toUpperCase(input.charAt(0));
                if(choice == 'N'){
                    alreadyAskedIfNewMatch=true;
                    isAnewMatch=true;
                    return true;
                }
                else if(choice == 'R'){
                    alreadyAskedIfNewMatch=true;
                    isAnewMatch=false;
                    return false;
                }
                else{
                    invalidInput = true;
                }
            }
            catch(Exception e){
                invalidInput = true;
            }

            //Inform the player they will have to try again if the count is invalid
            System.out.println(GameConstants.INVALID_RECONNECTING_OR_NEW);
            System.out.print(GameConstants.ASK_RECONNECTING_OR_NEW);
        }
        alreadyAskedIfNewMatch=true;
        isAnewMatch=false;
        return false;
    }

    /** Used to ask the player for a session player count upon creation of a new game
     * @return Player count for the new session
     */
    private int askNumberOfPlayers()
    {
        consoleScanner = new Scanner(System.in);

        //Inform the player they will have to type a username
        System.out.print(GameConstants.NUMBER_OF_PLAYERS_MESSAGE);
        int count = -1;
        boolean invalidInput = true;

        //While the player has yet to produce a valid player count...
        while(invalidInput)
        {
            try
            {
                count = consoleScanner.nextInt();
                invalidInput = false;
            }
            //In case the player input doesn't match the Integer format
            catch(InputMismatchException e)
            {
                invalidInput = true;
                consoleScanner = new Scanner(System.in);
            }

            //Check whether or not the input is invalid
            invalidInput = invalidInput || (count <= 0 || count > 4);

            //Inform the player they will have to try again if the count is invalid
            if(invalidInput) System.out.println(GameConstants.INVALID_NUMBER_OF_PLAYERS_MESSAGE);
        }

        return count;
    }

    /** Advances the current scene based on the data from the latest compressedModel */
    private void nextScene()
    {
        boolean isActivePlayer = compressedModel.getPlayerNames().get(compressedModel.getaP()).equals(username);
        if( compressedModel.getHasSessionStarted() && !compressedModel.getHasSessionEnded() && isActivePlayer)
        {
            if(compressedModel.getTurnState() == null){
                currentScene = new ChooseResourcesScene();
            }
            else{
                switch (compressedModel.getTurnState())
                {
                    case SETUP_PLAYER_CHOICES:
                        currentScene = new ChooseResourcesScene();
                        break;
                    case INITIAL_LEADER_ACTION:
                    case FINAL_LEADER_ACTIONS:
                        currentScene = new LeaderCardScene();
                        break;
                    case CHOOSE_ACTION:
                    case MARKET:
                    case PRODUCTION_PHASE:
                    case DEV_CARD_PURCHASE:
                        currentScene = new TurnStatusScene();
                        break;
                    default:
                        break;
                }
            }


        }
        else if(!compressedModel.getHasSessionStarted()) currentScene = new WaitingListScene();
        else if(compressedModel.getHasSessionEnded()) currentScene = new EndOfMatchScene();
        else currentScene = new WaitingTurnScene();
    }

    /** Displays the latest picked action token */
    private void displayActionTokenResult()
    {
        ActionTokenType type = compressedModel.getPickedActionToken();

        String variableString = null;

        switch (type)
        {
            case DEV_CARD_DISCARD_BLUE:
                variableString = GameConstants.ACTION_TOKEN_REMOVE_BLUE;
                break;
            case DEV_CARD_DISCARD_GREEN:
                variableString = GameConstants.ACTION_TOKEN_REMOVE_GREEN;
                break;
            case DEV_CARD_DISCARD_PURPLE:
                variableString = GameConstants.ACTION_TOKEN_REMOVE_PURPLE;
                break;
            case DEV_CARD_DISCARD_YELLOW:
                variableString = GameConstants.ACTION_TOKEN_REMOVE_YELLOW;
                break;
            case ADVANCE_BLACK_CROSS:
                variableString = GameConstants.ACTION_TOKEN_ADVANCE;
                break;
            case SHUFFLE_ADVANCE_BLACK_CROSS:
                variableString = GameConstants.ACTION_TOKEN_ADVANCE_SHUFFLE;
                break;
            default:
                variableString = "to something unknown.";
                break;
        }

        System.out.println(GameConstants.ACTION_TOKEN_NOTIFICATION_START + variableString + '\n');
        System.out.println(GameConstants.ACTION_TOKEN_ASCII_ART + '\n');
        System.out.println(GameConstants.ACTION_TOKEN_NOTIFICATION_END + '\n');
    }
}
