package it.polimi.ingsw.client;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.gamepanels.LoginDialog;
import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.BiObservable;
import it.polimi.ingsw.common.util.BiObserver;
import it.polimi.ingsw.common.util.GameConstants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Message handler used by the GUI, implements a BiObserver/BiObservable
 */
public class GUIMessageHandler  implements BiObserver<String, CompressedModel>, BiObservable<String, PlayerEvent>
{
    //Used to get input from the command line
    private LoginDialog loginDialog;

    //Internal buffer for the received compressed model
    private CompressedModel compressedModel;
    private boolean isNewMatch;
    private String username = "";

    //BiObserver list
    private List<BiObserver<String, PlayerEvent>> biObservers;

    private GUI gui;

    /** Creates a new instance of the the View component in the CLI execution mode */
    public GUIMessageHandler() {
        biObservers = new ArrayList<>();

        Object[] options = {"Reconnect", "Create / Join a new game"};
        final int JOIN_OPTION = 1;

        //	showOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue)
        int answer = JOptionPane.showOptionDialog(
                null,
                "Welcome, do you want to reconnect to a previous session?",
                "Masters of the Renaissance",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

        //The Window has been closed
        if(answer < 0 || answer > JOIN_OPTION) System.exit(0);

        isNewMatch = (answer == JOIN_OPTION);

        loginDialog = new LoginDialog(this);
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

        if(isStringMessage)
        {
            switch(message1)
            {
                case GameConstants.WELCOME_ACTION:
                    notify(isNewMatch ? GameConstants.NEW_CONNECTION : GameConstants.RECONNECTING, null);
                    break;
                case GameConstants.HOST_PLAYER_MESSAGE:
                    loginDialog.showNameAndNumberCard();
                    break;
                case GameConstants.GUEST_PLAYER_MESSAGE:
                    loginDialog.showNameCard(!isNewMatch);
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

            PlayerWorkSpace.getInstance().clearPlayerEvent();

            if(gui == null)
            {
                gui = new GUI(this, username);
                loginDialog.setVisible(false);
            }
            gui.manageCompressModel(compressedModel);

            //Needed in SinglePlayer so the Action Token is not picked at the start of the game
            if(compressedModel.getTurnState() == TurnState.FINAL_LEADER_ACTIONS && PlayerWorkSpace.getInstance().isFirstTurn())
                PlayerWorkSpace.getInstance().endFirstTurn();

        }
    }

    /** Returns whether or not the player has requested a new Game Session
     * @return Has the player requested a new session?
     */
    public boolean isNewSession()
    {
        return isNewMatch;
    }

    /** Sets the player username
     * @param username Player's username
     */
    public void setUsername(String username)
    {
        this.username = username;
    }
}
