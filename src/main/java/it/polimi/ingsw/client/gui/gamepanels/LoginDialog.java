package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.ClientApp;
import it.polimi.ingsw.client.ClientSideProtocol;
import it.polimi.ingsw.client.GUIMessageHandler;
import it.polimi.ingsw.common.util.GameConstants;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

/**
 * Initial dialog presented to the player to get Server IP Address and credentials
 */
public class LoginDialog extends JFrame implements ActionListener
{
    private GUIMessageHandler messageHandler;

    private JPanel mainPanel;

    //Ip address card
    private JPanel ipPanel;
    private JLabel welcomeLabel;
    private JLabel ipAddressLabel;
    private JTextField ipAddressField;
    private JButton connectButton;

    //Name & Players panel
    private JPanel nameNumPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel numLabel;
    private JSpinner numSpinner;
    private JButton startGameButton;

    //Waiting start panel
    private JPanel waitStartPanel;
    private JLabel waitMessage;

    private CardLayout dialogLayout;

    private boolean hasAlreadyAsked = false;
    private boolean isSessionCreation = true;

    /* -- CardLayout CONSTANTS -- */
    private final String IP_PANEL_ID = "IP_PANEL";
    private final String DATA_PANEL_ID = "NAME_NUM_PANEL";
    private final String WAIT_PANEL_ID = "WAIT_PANEL";

    /* -- BUTTON CONSTANTS -- */
    private final String CONNECT_STR = "Connect";
    private final String START_STR = "Start Game";

    /**
     * Creates a new LoginDialog  @param messageHandler the message handler
     * @param messageHandler the instance of the messages Handler
     */
    public LoginDialog(GUIMessageHandler messageHandler)
    {
        super();
        this.messageHandler = messageHandler;

        mainPanel = new JPanel();

        dialogLayout = new CardLayout();
        mainPanel.setLayout(dialogLayout);

        setupIpPanel();
        mainPanel.add(ipPanel, IP_PANEL_ID);
        setupNameNumPanel();
        mainPanel.add(nameNumPanel, DATA_PANEL_ID);
        setupWaitPanel();
        mainPanel.add(waitStartPanel, WAIT_PANEL_ID);

        add(mainPanel);

        this.setTitle("Masters of the Renaissance - Login");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(300, 170);
        this.setResizable(false);
        //Appear at the center of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width/2, dim.height/2 - this.getSize().height/2);
        this.setVisible(true);
    }

    /** Initializes IP Address asking card */
    private void setupIpPanel()
    {
        ipPanel = new JPanel();

        //Welcome Label
        welcomeLabel = new JLabel("Masters of the Renaissance");
        welcomeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        ipPanel.add(welcomeLabel);
        JSeparator newSep = new JSeparator(SwingConstants.HORIZONTAL);
        newSep.setPreferredSize(new Dimension(200, 2));
        ipPanel.add(newSep);

        //Ip Label
        ipAddressLabel = new JLabel("Server IP Address:");
        ipPanel.add(ipAddressLabel);

        //Ip text field
        ipAddressField = new JTextField();
        ipAddressField.setToolTipText("ex. 0.0.0.0");
        ipAddressField.setPreferredSize(new Dimension(150, 20));
        ipPanel.add(ipAddressField);

        //Buttons
        connectButton = new JButton(CONNECT_STR);
        connectButton.addActionListener(this);
        getRootPane().setDefaultButton(connectButton);
        ipPanel.add(connectButton);
    }

    /** Initializes Name and Number asking card */
    private void setupNameNumPanel()
    {
        nameNumPanel = new JPanel();
        nameNumPanel.setLayout(null);

        //Name Label
        nameLabel = new JLabel("Username: ");
        nameLabel.setBounds(30, 10, 80, 20);
        nameNumPanel.add(nameLabel);

        //Username Text Field
        nameField = new JTextField();
        nameField.setToolTipText("max 12 characters...");
        nameField.setPreferredSize(new Dimension(150, 20));
        nameField.setBounds(100, 10, 130, 20);
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { }

            @Override
            public void removeUpdate(DocumentEvent e) { }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if(e.getDocument().getLength() > GameConstants.MAX_USERNAME_LENGTH)
                {
                    String prevString = nameField.getText();
                    nameField.setText(prevString.substring(0, prevString.length() - 1));
                }
            }
        });
        nameNumPanel.add(nameField);

        //Number Label
        numLabel = new JLabel("Players: ");
        numLabel.setBounds(30, 50, 80, 20);
        nameNumPanel.add(numLabel);

        //Number spinner
        numSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        numSpinner.setBounds(100, 50, 130, 20);
        nameNumPanel.add(numSpinner);

        //Start Game Button
        startGameButton = new JButton(START_STR);
        startGameButton.setBounds(100, 85, 130, 30);
        startGameButton.addActionListener(this);
        nameNumPanel.add(startGameButton);

    }

    /** Initializes Waiting card */
    private void setupWaitPanel()
    {
        waitStartPanel = new JPanel();
        waitMessage = new JLabel("Done. Wait for other players to connect");
        waitStartPanel.add(waitMessage);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case CONNECT_STR:
                attemptConnection();
                break;
            case START_STR:
                //Player has entered an empty string
                if(nameField.getText().isBlank())
                {
                    JOptionPane.showMessageDialog(this, GameConstants.PLAYER_USERNAME_OUT_OF_BOUNDS, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Notify asked values
                if(messageHandler.isNewSession() && isSessionCreation)
                    messageHandler.notify(nameField.getText() + "-" + ((Integer)numSpinner.getValue()).toString(), null);
                else
                    messageHandler.notify(nameField.getText(), null);

                //All done, wait for the session to start
                startGameButton.setEnabled(false);
                hasAlreadyAsked = true;
                messageHandler.setUsername(nameField.getText());
                dialogLayout.show(mainPanel, WAIT_PANEL_ID);
                break;
            default:
                break;
        }
    }

    /* ------ Change Dialogue card ------ */

    /**
     * Makes the Dialog only show the Name insertion card
     */
    public void showNameCard(boolean isReconnection)
    {
        numLabel.setVisible(false);
        numSpinner.setVisible(false);
        numSpinner.setEnabled(false);
        isSessionCreation = false;
        if(hasAlreadyAsked){
            if(isReconnection)showUsernameNotFoundWarning();
            else showAlreadyAskedWarning();
            startGameButton.setEnabled(true);
        }
        dialogLayout.show(mainPanel, DATA_PANEL_ID);
    }

    /**
     * Makes the Dialog show the Name and Player count insertion card
     */
    public void showNameAndNumberCard()
    {
        numLabel.setVisible(true);
        numSpinner.setVisible(true);
        numSpinner.setEnabled(true);
        isSessionCreation = true;
        if(hasAlreadyAsked){
            showAlreadyAskedWarning();
            startGameButton.setEnabled(true);
        }
        dialogLayout.show(mainPanel, DATA_PANEL_ID);
    }

    /* ------ Connection Establishment ------ */

    /** Checks if IP Address is correct and, if so, attempts to start the connection */
    private void attemptConnection()
    {
        connectButton.setEnabled(false);
        Thread connectionThread = new Thread(() -> connect(ipAddressField.getText()));
        connectionThread.start();
    }

    /** Tries to start a connection
     * @param ip Ip address
     */
    private void connect(String ip)
    {
        Socket clientSocket = null, clientSecondarySocket = null;
        boolean connectionEstablished = false;

        try
        {
            clientSocket = new Socket(ip, ClientApp.port);
            clientSecondarySocket = new Socket(ClientApp.secondaryIP.isBlank() ? ip : ClientApp.secondaryIP, ClientApp.secondaryPort);
            connectionEstablished = true;
        }
        catch (IOException e)
        {
            connectionEstablished = false;
        }

        if(!connectionEstablished)
        {
            connectButton.setEnabled(true);
            JOptionPane.showMessageDialog(this, GameConstants.INVALID_IP_ADDRESS, "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            //Pair socket handlers to message handler
            ClientSideProtocol clientSideProtocol = new ClientSideProtocol(clientSocket, clientSecondarySocket);
            clientSideProtocol.addObserver(messageHandler);
            messageHandler.addObserver(clientSideProtocol);
            new Thread(clientSideProtocol).start();
            getRootPane().setDefaultButton(startGameButton);
        }
    }

    /**
     * Shows a warning if the player selected an already chosen username
     */
    private void showAlreadyAskedWarning()
    {
        JOptionPane.showMessageDialog(this, GameConstants.PLAYER_USERNAME_RETRY_MESSAGE, "Error!", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a warning that the inserted name is not recognized: the player must insert  the same username
     */
    private void showUsernameNotFoundWarning()
    {
        JOptionPane.showMessageDialog(this, GameConstants.PLAYER_USERNAME_NOT_FOUND_MESSAGE, "Error!", JOptionPane.ERROR_MESSAGE);
    }
}
