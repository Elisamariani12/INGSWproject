package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.components.GameButton;
import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.client.util.ImageUtils;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.Pair;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.StorageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 * The type Initial leader card n resources dialog.
 */
public class InitialLeaderCardNResourcesDialog extends JPanel {
    private static final int LEADER_CARD_SIZE_X = 250;
    private static final int RESOURCE_CHOICE_SIZE_X = 50;


    private ArrayList<Integer> chosenLeaderCards;
    private ArrayList<Pair<Resource,StorageType>> chosenResources;
    private int numberOfResources;
    private boolean isActionValid = false;
    private GUI gui;

    /**
     * Instantiates a new Initial leaderCardNResources dialog, this is used to ask the initial choices to the plyer.
     *
     * @param compressedModel the compressed model
     * @param gui             the gui
     */
    public InitialLeaderCardNResourcesDialog(CompressedModel compressedModel , GUI gui) {
        this.gui = gui;
        numberOfResources = GameConstants.INITIAL_RESOURCES_AMOUNT_FOR_PLAYER[compressedModel.getaP()];
        this.setLayout(new BorderLayout());
        JTabbedPane internalTabbedPane = new JTabbedPane();
        JPanel leaderMenu = leaderCardInitialChoose(compressedModel);
        JPanel resourceMenu = resourceInitialChoice(numberOfResources);
        JButton closeButton = new JButton("Click to close");
        internalTabbedPane.addTab( "LeaderCards", leaderMenu);
        if(numberOfResources > 0){
            internalTabbedPane.addTab("Resources", resourceMenu);
        }
        internalTabbedPane.setSize(GameConstants.WINDOW_WIDTH-300, GameConstants.WINDOW_HEIGHT-350);

        chosenLeaderCards = new ArrayList<>();
        chosenResources = new ArrayList<>();

        this.add(internalTabbedPane, BorderLayout.CENTER);
        this.add(closeButton, BorderLayout.PAGE_END);
        this.setSize(GameConstants.WINDOW_WIDTH-300, GameConstants.WINDOW_HEIGHT-350);
        this.setLocation(200, 100);

        closeButton.addActionListener(e -> {
            isActionValid = chosenLeaderCards.size() == GameConstants.LEADER_CARDS_CHOICE_AMOUNT && chosenResources.size() == numberOfResources;
            if(isActionValid) {
                PlayerWorkSpace.getInstance().clearPlayerEvent();
                PlayerWorkSpace.getInstance().initialResourceAndLeaderCard(chosenLeaderCards, chosenResources);
                gui.notifyPlayerMessage();
                setVisible(false);
            }
            else {
                JOptionPane.showMessageDialog(null, "Something is missing, select it!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });
    }


    /**
     * Receives the compressed model and retrieve the leaderCards from which the player can choose
     *
     * @param compressedModel Up to date compressedModel
     * @return a Jpanel that contains leaderCards Initial choice
     */
    private JPanel leaderCardInitialChoose(CompressedModel compressedModel){
        ImageRepository imgRepo = ImageRepository.getInstance();
        int leaderCardSizeY = Math.round((float)LEADER_CARD_SIZE_X * ImageUtils.getAspectRatio(ImageRepository.getInstance().getCardImage(1)));

        ArrayList<Integer> toChooseCards = compressedModel.getaPInitialChooseLeaderCard();
        JPanel leadCardMainPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Choose 2 LeaderCard!");
        leadCardMainPanel.add(label, BorderLayout.NORTH);
        LeaderCardChooseListener listener= new LeaderCardChooseListener();

        JPanel leadCardPanel = new JPanel();
        leadCardPanel.setLayout(new GridLayout(1,4));
        for(Integer card : toChooseCards){
            GameButton gameButton = new GameButton(imgRepo.getCardImage(card), leaderCardSizeY, LEADER_CARD_SIZE_X);
            gameButton.setActionCommand(String.valueOf(card));
            gameButton.addActionListener(listener);
            leadCardPanel.add(gameButton);
        }
        leadCardMainPanel.add(leadCardPanel, BorderLayout.CENTER);
        return leadCardMainPanel;
    }


    /**
     * The type Leader card choose listener. It register the leaderCards choosen
     */
    class LeaderCardChooseListener implements ActionListener {
        /**
         * The Choose card counter.
         */
        int chooseCardCounter = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            if(chosenLeaderCards.size()<2){
                chosenLeaderCards.add(Integer.valueOf(e.getActionCommand()));
                chooseCardCounter++;
                GameButton pressed = (GameButton) e.getSource();
                pressed.setEnabled(false);
                pressed.setGameButtonImage(ImageRepository.getInstance().getLeaderCardBackImage());
            }
            else{
                JOptionPane.showMessageDialog(null, "You have already choose your cards!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    /**
     * Receives the number of resources that the player can choose
     *
     * @param resourceNumber that the player can choose
     * @return a Jpanel that contains resources Initial choice
     */
    private JPanel resourceInitialChoice(int resourceNumber){
        ImageRepository imgRepo = ImageRepository.getInstance();
        JPanel resourcesPanel = new JPanel();
        resourcesPanel.setLayout(null);

        JLabel resourceLabel = new JLabel("Choose "+ resourceNumber+ " resources");
        JPanel resourcesChoice = new JPanel();
        GameButton box = new GameButton(ImageRepository.getInstance().getProductionResourcesChest(), 300, 250);

        ResourcesToChooseListener listener= new ResourcesToChooseListener(resourceNumber, resourceLabel, box);

        resourcesChoice.setLayout(new GridLayout(1,4));
        for(Resource r:Resource.values()){
            if((r!=Resource.FAITH)&&(r!=Resource.WHITE)&&(r!=Resource.GENERIC)) {
                Image imageResource= imgRepo.getResourceImage(r);
                GameButton gameButton = new GameButton(imageResource, RESOURCE_CHOICE_SIZE_X, Math.round((float) RESOURCE_CHOICE_SIZE_X/ ImageUtils.getAspectRatio(imageResource)));
                gameButton.setActionCommand(String.valueOf(r).toUpperCase());
                gameButton.addActionListener(listener);
                resourcesChoice.add(gameButton);
            }
        }

        resourceLabel.setBounds(0,0,300,50);
        resourcesChoice.setBounds(0,50,300, 70);
        box.setBounds(450, 0, 300, 250);

        resourcesPanel.add(box);
        resourcesPanel.add(resourcesChoice);
        resourcesPanel.add(resourceLabel);
        return resourcesPanel;
    }

    /**
     * The type ResourcesToChoose listener. It register the choosen resources
     */
    class ResourcesToChooseListener implements ActionListener{
        /**
         * The Res number.
         */
        int resNumber;
        /**
         * The Res lab.
         */
        JLabel resLab;
        /**
         * The Res box.
         */
        JButton resBox;

        /**
         * Instantiates a new ResourcesToChooseListener.
         *
         * @param resourceNumber the resource number
         * @param resourceLabel  the resource label
         * @param box            the box
         */
        public ResourcesToChooseListener(int resourceNumber, JLabel resourceLabel, JButton box) {
            resNumber = resourceNumber;
            resLab = resourceLabel;
            resBox = box;
            resBox.setLayout(null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(chosenResources.size() < resNumber){
                chosenResources.add(new Pair<>(Resource.valueOf(e.getActionCommand()), StorageType.WAREHOUSE_DEPOT));
                resLab.setText("Choose "+ (resNumber - chosenResources.size()) + " resources");
                GameButton res = new GameButton(ImageRepository.getInstance().getResourceImage(Resource.valueOf(e.getActionCommand())),40, 40);
                res.setBounds((chosenResources.size()-1)*100 + 100, 125,40, 40);
                resBox.add(res);
                resBox.revalidate();
                resBox.repaint();

            }
            else{
                JOptionPane.showMessageDialog(null, "You can't choose another resource", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



}

