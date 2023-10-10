package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.GUIMessageHandler;
import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.gui.gamepanels.*;
import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.PlayerMoveResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Game Window for the GUI mode
 */
public class GUI extends JFrame
{
    private JTabbedPane tabbedPane;
    private JPanel upperPanel;
    private JLabel activePlayerInfos;
    private JButton endTurnButton;
    private Boolean settedTab;
    private GUIMessageHandler messageHandler;
    private String username;
    private WelcomePanel welcomePanel;
    private CommonBoard commonBoard;
    private YourPlayerBoard yourPlayerBoard;
    private ArrayList<OthersPlayerBoard> othersPlayerBoardList;
    private CompressedModel lastCompressModel;


    /**
     * Creates a new game window  @param messageHandler the message handler
     *
     * @param messageHandler instance of the messange handler
     * @param username the username of the player that owns this gui
     */
    public GUI(GUIMessageHandler messageHandler, String username)
    {

        //Load Graphic resources
        ImageRepository.getInstance().loadAllGraphics();

        //Initialize PlayerWorkSpace
        PlayerWorkSpace.getInstance().setPlayerWorkspaceUsername(username);
        PlayerWorkSpace.getInstance().clearPlayerEvent();

        this.messageHandler = messageHandler;
        this.username = username;
        this.settedTab = false;
        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        welcomePanel = new WelcomePanel();
        commonBoard = new CommonBoard(this);
        yourPlayerBoard = new YourPlayerBoard(username, this);
        othersPlayerBoardList = new ArrayList<>();
        //Component instantiation and addition
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Welcome", welcomePanel);
        tabbedPane.addTab("Common Board", commonBoard);
        tabbedPane.addTab("Your Board", yourPlayerBoard);

        //ActivePlayer infos and END TURN BUTTON
        activePlayerInfos = new JLabel("No one is playing");
        endTurnButton = new JButton("END TURN");
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lastCompressModel.getTurnState() == TurnState.FINAL_LEADER_ACTIONS && lastCompressModel.getPlayerNames().get(lastCompressModel.getaP()).equals(username)){
                    PlayerWorkSpace.getInstance().clearPlayerEvent();
                    PlayerWorkSpace.getInstance().registerLeaderAction(null, false);
                    notifyPlayerMessage();

                }
                else{
                    JOptionPane.showMessageDialog(GUI.this, GameConstants.GENERIC_ERROR, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        upperPanel = new JPanel();
        upperPanel.add(activePlayerInfos);
        upperPanel.add(endTurnButton);

        this.setLayout(new BorderLayout());

        this.add(upperPanel, BorderLayout.PAGE_START);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.setTitle(GameConstants.WINDOW_TITLE);
        this.setSize(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        //to disable the welcome panel after tot seconds
        Timer myTimer = new Timer();
        TimerTask myTimerTask= new TimerTask() {
            @Override
            public void run() {
                tabbedPane.remove(welcomePanel);
            }
        };
        myTimer.schedule(myTimerTask,4000);
    }

    /**
     * Receives the compressed model and updates the frame where necessary
     *
     * @param compressedModel Up to date compressedModel
     */
    public void manageCompressModel(CompressedModel compressedModel)
    {
        //Frame's infos update
        this.lastCompressModel = compressedModel;
        String currentPlayerUsername = compressedModel.getPlayerNames().get(compressedModel.getaP());
        boolean isPlayerTurn = username.equals(currentPlayerUsername);
        String toDoAction =compressedModel.getTurnState() == null ? "Choose your Initial Leader Cards" : getActualTurnAction(compressedModel.getTurnState());
        activePlayerInfos.setText(isPlayerTurn ? "It's your turn!" + " " + toDoAction : "Wait for your turn, " + currentPlayerUsername + " is playing right now!");
        this.validate();
        this.repaint();

        //The game has ended
        if(compressedModel.getHasSessionEnded())
        {
            JOptionPane.showMessageDialog(this, new OutcomeDialog(compressedModel), "Session Outcome", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }

        //Show error message if the last move was rejected
        if(isPlayerTurn && compressedModel.getPlayerMoveResponse() == PlayerMoveResponse.REJECTED)
        {
            boolean wasCurrentPlayerRejected = compressedModel.getLatestActionSource().equals(currentPlayerUsername);

            if(wasCurrentPlayerRejected)
                JOptionPane.showMessageDialog(this, "Your last action was rejected", "Error", JOptionPane.ERROR_MESSAGE);
        }

        //Adds other players' panels to the tabbed panel
        if(compressedModel.getHasSessionStarted() && !settedTab)
        {
            for(String name : compressedModel.getPlayerNames())
            {
                if(!name.equals(username))
                {
                    OthersPlayerBoard other = new OthersPlayerBoard(name, this);
                    othersPlayerBoardList.add(other);
                    tabbedPane.addTab(name, other);
                }

            }
            settedTab = true;
        }

        //Only update 'yourPlayerBoard' if this client corresponds to the active player
        yourPlayerBoard.updateView(compressedModel);


        //Update all other players' boards
        for(OthersPlayerBoard other: othersPlayerBoardList)
        {
            other.updateView(compressedModel);
        }

        //Finally, update the common board
        SwingUtilities.invokeLater(() -> commonBoard.updateView(compressedModel));

    }

    /**
     * @param turnState received by a compressModel
     * @return actualTurnActionMessage to print it in the GUi main scene
     */
    private String getActualTurnAction(TurnState turnState) {
        switch (turnState){
            case INITIAL_LEADER_ACTION:
                return "You can do a leader or a main action";
            case CHOOSE_ACTION:
                return "Go to the market, buy one Development Card or activate the Production";
            case MARKET:
                return "Go to the market!";
            case PRODUCTION_PHASE:
                return "Activate another production or press END Production";
            case DEV_CARD_PURCHASE:
                return "Buy one Development Card";
            case SETUP_PLAYER_CHOICES:
                return "Choose your leaderCards and your initial resources";
            case FINAL_LEADER_ACTIONS:
                return "Activate/Discard a leader Card or press END TURN";
            default:
                return "";
        }
    }

    /**
     * Sends the Player event to the ClientSideProtocol to be delivered to the server
     */
    public synchronized void notifyPlayerMessage()
    {
        messageHandler.notify(null, PlayerWorkSpace.getInstance().getPlayerEventCopy());
    }
}
