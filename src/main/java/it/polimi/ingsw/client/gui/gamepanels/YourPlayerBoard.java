package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.components.GameButton;
import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.client.util.ImageUtils;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GUI scene that represent the playerBoard of the GUI owner
 */
public class YourPlayerBoard extends PlayerBoard {
    private String username;
    private GUI gui;
    private ArrayList<Integer> initialChoiceLeaderCards = new ArrayList<>();
    private ArrayList<Pair<Resource, StorageType>> initialChoiceResources = new ArrayList<>();
    private ArrayList<Triplet<Integer, GameButton, JButton>> inactiveLeaderCardButtons = new ArrayList<>();
    private ArrayList<Triplet<Integer, GameButton, JButton>> activeProductionLeaderCardButtons = new ArrayList<>();
    /**
     * The Dev card buttons.
     */
    protected List<Pair<Integer, GameButton>> devCardButtons;
    private HashMap<Integer, ArrayList<Resource>> activatedProductions;
    private JButton endProductionButton;
    private ArrayList<GameButton> resourcesOverActivatedCards;
    private boolean skippedInitialLeaderAction;
    private List<GameButton> myLeaderCardButtons;


    private static final int RESOURCES_PRODUCTION_SIZE = 40;
    private static final int[][] RESOURCES_PRODUCTION_X_Y_DEVIATION = {{20, 20}, {50, 20}, {20, 40}, {50, 40}};

    //----------components---------
    private List<JButton> resourcesInWDButtons;
    private JButton genericProductionButton;

    /**
     * Instantiates a new Your player board.
     *
     * @param username the username
     * @param gui      the gui
     */
    public YourPlayerBoard(String username, GUI gui) {
        super(username, gui);
        this.setLayout(null);
        this.setBackground(Color.yellow);
        this.setVisible(true);
        this.username = username;
        this.gui = gui;
        this.resourcesInWDButtons = new ArrayList<>();
        devCardButtons = new ArrayList<>();
        myLeaderCardButtons= new ArrayList<>();
        resourcesOverActivatedCards = new ArrayList<>();
        activatedProductions = new HashMap<>();
    }

    @Override
    public synchronized void updateView(CompressedModel compressedModel) {
        this.removeAll();
        super.updateView(compressedModel);

        if(compressedModel.getPlayerNames().indexOf(username)==compressedModel.getaP()) {
            if ((compressedModel.getTurnState() == TurnState.SETUP_PLAYER_CHOICES || compressedModel.getTurnState() == null) && compressedModel.getHasSessionStarted()) {
                InitialLeaderCardNResourcesDialog initialChoiceDialog = new InitialLeaderCardNResourcesDialog(compressedModel, gui);
                this.add(initialChoiceDialog);
                initialChoiceDialog.setVisible(true);
            }
            if (compressedModel.getTurnState() != TurnState.SETUP_PLAYER_CHOICES && compressedModel.getTurnState() != TurnState.FINAL_LEADER_ACTIONS) {
                    activateDevCardANDGenericProductionButtons(compressedModel);
            }
            if (compressedModel.getTurnState() == TurnState.INITIAL_LEADER_ACTION || compressedModel.getTurnState() == TurnState.FINAL_LEADER_ACTIONS || compressedModel.getTurnState() == TurnState.PRODUCTION_PHASE) {
                activateLeaderCardPurchaseOrProductionButton(compressedModel);
                if(compressedModel.getTurnState() != TurnState.PRODUCTION_PHASE ){
                    skippedInitialLeaderAction = true;
                }
            }

        }
        else{
            disableLeaderCardButtons();
        }
        this.revalidate();
        this.repaint();
    }


    //------------------------------------------------------- LEADER ACTIONS ----------------------------------------------------------
    /**
     * Draw player LeaderCards
     */
    @Override
    protected void drawLeaderCards(CompressedModel compressedModel) {
        int leaderCardSizeY = Math.round((float) getLeaderCardSizeX() / ImageUtils.getAspectRatio(getLeaderCardBack()));
        int counter = 0;


        myLeaderCardButtons = new ArrayList<>();
        inactiveLeaderCardButtons = new ArrayList<>();

        //DRAW MY ACTIVE CARDS ---------

        for(Integer cardID:super.getAllMyActiveLeaderCards()){
            //CREATES JBUTTON WITH 'ACTIVE' OR 'INACTIVE'
            JButton leadStateButton = new JButton();
            leadStateButton.setLayout(null);
            leadStateButton.setText("ACTIVE");
            leadStateButton.setBackground(Color.GREEN);
            leadStateButton.setBounds(getLeaderCardsPosX() + 80, getLeaderCardPosY()[counter], 105, 20);
            leadStateButton.setFont(new Font("Arial Black", Font.BOLD, 12));
            leadStateButton.setForeground(Color.WHITE);
            for (MouseListener mouseListener : leadStateButton.getMouseListeners())
                leadStateButton.removeMouseListener(mouseListener);
            this.add(leadStateButton);

            //CREATES GAME BUTTON OF THE CARD
            GameButton leadGameButton = new GameButton(ImageRepository.getInstance().getCardImage(cardID), getLeaderCardSizeX(), leaderCardSizeY);
            leadGameButton.setLayout(null);
            leadGameButton.setBounds(getLeaderCardsPosX(), getLeaderCardPosY()[counter], getLeaderCardSizeX(), leaderCardSizeY);
            if (compressedModel.getaP() != compressedModel.getPlayerNames().indexOf(username)) {
                leadGameButton.setEnabled(false);
            }
            this.add(leadGameButton);
            myLeaderCardButtons.add(leadGameButton);

            LeaderCard myLeaderCard= CardRepository.getInstance().getLeaderCardByID(cardID);
            if(myLeaderCard.getPower().getSpecialAbilityType()==SpecialAbility.PRODUCTION) {
                Triplet<Integer, GameButton, JButton> tripletOfButtons = new Triplet<>(cardID, leadGameButton, leadGameButton);
                activeProductionLeaderCardButtons.add(tripletOfButtons);
            }

            counter++;
        }

        //DRAW MY HIDDEN CARDS------------

        for(Integer cardID:getAllMyInactiveLeaderCards()){
            //CREATES JBUTTON WITH 'ACTIVE' OR 'INACTIVE'
            JButton leadStateButton = new JButton();
            leadStateButton.setLayout(null);
            leadStateButton.setText("INACTIVE");
            leadStateButton.setBackground(Color.RED);
            leadStateButton.setBounds(getLeaderCardsPosX() + 80, getLeaderCardPosY()[counter], 105, 20);
            leadStateButton.setFont(new Font("Arial Black", Font.BOLD, 12));
            leadStateButton.setForeground(Color.WHITE);
            for (MouseListener mouseListener : leadStateButton.getMouseListeners())
                leadStateButton.removeMouseListener(mouseListener);
            this.add(leadStateButton);

            //CREATES GAME BUTTON OF THE CARD
            GameButton leadGameButton = new GameButton(ImageRepository.getInstance().getCardImage(cardID), getLeaderCardSizeX(), leaderCardSizeY);
            leadGameButton.setLayout(null);
            leadGameButton.setBounds(getLeaderCardsPosX(), getLeaderCardPosY()[counter], getLeaderCardSizeX(), leaderCardSizeY);
            if (compressedModel.getaP() != compressedModel.getPlayerNames().indexOf(username)) {
                leadGameButton.setEnabled(false);
            }
            this.add(leadGameButton);
            myLeaderCardButtons.add(leadGameButton);

            Triplet<Integer, GameButton, JButton> tripletOfButtons = new Triplet<>(cardID, leadGameButton, leadStateButton);
            inactiveLeaderCardButtons.add(tripletOfButtons);

            counter++;
        }
    }

    /**
     * Activate leaderCard activation buttons and production buttons
     * @param compressedModel the updated compressed model
     */
    private void activateLeaderCardPurchaseOrProductionButton(CompressedModel compressedModel) {
        if(compressedModel.getaP()==compressedModel.getPlayerNames().indexOf(username)) {
            for (Triplet<Integer, GameButton, JButton> triplet : inactiveLeaderCardButtons) {
                triplet.getSecond().addActionListener(e -> enableChoiceDiscardOrActivate(compressedModel, e, triplet, false));
            }
            for (Triplet<Integer, GameButton, JButton> triplet : activeProductionLeaderCardButtons) {
                triplet.getSecond().addActionListener(e -> enableChoiceDiscardOrActivate(compressedModel, e, triplet, true));
            }
        }
    }

    /**
     * Enable leaderCard activate or discard buttons
     * @param compressedModel the updated compressed model
     * @param e event that calls this method
     * @param buttonsTriplet the leader card buttons
     * @param isActiveProduction is a leaderCard active and contains a production ability?
     */
    private void enableChoiceDiscardOrActivate(CompressedModel compressedModel, ActionEvent e, Triplet<Integer, GameButton, JButton> buttonsTriplet, Boolean isActiveProduction) {

        skippedInitialLeaderAction = false;
        if(compressedModel.getaP()==compressedModel.getPlayerNames().indexOf(username)) {
            if (!isActiveProduction) {
                //create button to activate the card
                JButton activateButton = new JButton("Activate");
                activateButton.setBackground(Color.BLACK);
                activateButton.setForeground(Color.white);
                activateButton.setBounds(buttonsTriplet.getSecond().getLocation().x + 90, buttonsTriplet.getSecond().getBounds().getLocation().y + 250, 105, 20);


                //create the button to discard the card
                JButton discardButton = new JButton("Discard");
                discardButton.setBackground(Color.BLACK);
                discardButton.setForeground(Color.white);
                discardButton.setBounds(buttonsTriplet.getSecond().getLocation().x - 30, buttonsTriplet.getSecond().getBounds().getLocation().y + 250, 105, 20);

                activateButton.addActionListener(event -> checkActivationOfLeaderCardRequest(compressedModel, event, buttonsTriplet, activateButton, discardButton));
                discardButton.addActionListener(event -> discardLeaderCard(event, buttonsTriplet, activateButton, discardButton));

                this.add(activateButton);
                this.add(discardButton);
            } else {
                if (compressedModel.getTurnState() != TurnState.FINAL_LEADER_ACTIONS) {
                    //create button to activate production of the card
                    JButton activateProductionButton = new JButton("Activate Production");
                    activateProductionButton.setBackground(Color.BLACK);
                    activateProductionButton.setForeground(Color.white);
                    activateProductionButton.setBounds(buttonsTriplet.getSecond().getLocation().x, buttonsTriplet.getSecond().getBounds().getLocation().y + 250, 105, 20);
                    activateProductionButton.addActionListener(event -> executeProduction(compressedModel, e, false, true, buttonsTriplet.getFirst()));
                    this.add(activateProductionButton);
                }
            }
            this.revalidate();
            this.repaint();
        }
        else{
           disableLeaderCardButtons();
        }
    }

    /**
     * Discard leaderCard handler
     * @param event the event that called this method
     * @param buttonsTriplet leaderCard buttons triplet
     * @param activateButton leaderCard activate button
     * @param discardButton leaderCard discard button
     */
    private void discardLeaderCard(ActionEvent event, Triplet<Integer, GameButton, JButton> buttonsTriplet, JButton activateButton, JButton discardButton) {

        LeaderCard leaderCardToDiscard = CardRepository.getInstance().getLeaderCardByID(buttonsTriplet.getFirst());

        //remove the buttons of this card
        this.remove(activateButton);
        this.remove(discardButton);
        this.remove(buttonsTriplet.getSecond());
        this.remove(buttonsTriplet.getThird());

        if(super.getAllMyInactiveLeaderCards().contains(leaderCardToDiscard.getCardID()))super.getAllMyInactiveLeaderCards().remove(((Integer) leaderCardToDiscard.getCardID()));

        //disable the all the leadercard buttons
        for (Triplet<Integer, GameButton, JButton> triplet : inactiveLeaderCardButtons) {
            triplet.getThird().setEnabled(false);
            triplet.getSecond().setEnabled(false);
        }

        PlayerWorkSpace.getInstance().clearPlayerEvent();
        PlayerWorkSpace.getInstance().registerLeaderAction(leaderCardToDiscard, true);
        gui.notifyPlayerMessage();
    }

    /**
     * Check if it's possible to activate the requested leaderCard
     * @param compressedModel the updated compressed model
     * @param e the event that called this method
     * @param buttonsTriplet leaderCard buttons
     * @param activateButton leaderCard activate button
     * @param discardButton leaderCard discard button
     */
    private void checkActivationOfLeaderCardRequest(CompressedModel compressedModel, ActionEvent e, Triplet<Integer, GameButton, JButton> buttonsTriplet, JButton activateButton, JButton discardButton) {

        boolean activableCard = true;
        //check banner requirements

        LeaderCard leaderCardToActivate = CardRepository.getInstance().getLeaderCardByID(buttonsTriplet.getFirst());
        List<Stack<Integer>> devCardsOwnedID = compressedModel.getDevCardSpace().get(compressedModel.getaP());
        List<DevelopmentCard> developmentCardsOwned = new ArrayList<>();

        for (Stack<Integer> deck : devCardsOwnedID) {
            developmentCardsOwned.addAll(deck.stream()
                    .map(CardRepository.getInstance()::getDevCardByID)
                    .collect(Collectors.toList()));
        }

        for (Triplet<BannerColor, Integer, Integer> bannerRequirements : leaderCardToActivate.getBannerRequirements()) {
            int amount = 0;

            //Add one to amount for every matching card
            for (DevelopmentCard devCard : developmentCardsOwned) {
                if (devCard.getBannerColor() == bannerRequirements.getFirst() && devCard.getCardLevel() == bannerRequirements.getSecond())
                    amount++;
            }
            if (amount < bannerRequirements.getThird()) activableCard = false;
        }

        //check resources Requirements
        List<ResourceStack> playerResources = Arrays.stream(compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP()))
                .collect(Collectors.toList());

        for (ResourceStack requirement : leaderCardToActivate.getRequiredResources()) {
            boolean requirementSatisfied = false;
            for (ResourceStack owned : playerResources) {
                if (owned.getResourceType() == requirement.getResourceType() && owned.getAmount() >= requirement.getAmount())
                    requirementSatisfied = true;
            }
            if (!requirementSatisfied) activableCard = false;
        }


        if (!activableCard) {
            JOptionPane.showMessageDialog(this, GameConstants.GENERIC_ERROR, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            //remove the buttons of this card
            this.remove(activateButton);
            this.remove(discardButton);

            buttonsTriplet.getThird().setText("ACTIVE");
            buttonsTriplet.getThird().setBackground(Color.GREEN);
            if(super.getAllMyInactiveLeaderCards().contains(leaderCardToActivate.getCardID()))super.getAllMyInactiveLeaderCards().remove(((Integer) leaderCardToActivate.getCardID()));
            if(!super.getAllMyInactiveLeaderCards().contains(leaderCardToActivate.getCardID()))super.getAllMyActiveLeaderCards().add(((Integer) leaderCardToActivate.getCardID()));

            PlayerWorkSpace.getInstance().clearPlayerEvent();
            PlayerWorkSpace.getInstance().registerLeaderAction(leaderCardToActivate, false);
            gui.notifyPlayerMessage();
        }
    }

    private void disableLeaderCardButtons() {
        for(GameButton gameButton:myLeaderCardButtons){
            gameButton.setEnabled(false);
        }
        revalidate();
    }
    //--------------------------------------------------------- PRODUCTIONS ---------------------------------------------------------

    @Override
    protected void drawDevCards(CompressedModel compressedModel) {

        //replace buttons
        int devCardSizeY = Math.round((float) getDevCardSizeX() / ImageUtils.getAspectRatio(getLeaderCardBack()));
        devCardButtons = new ArrayList<>();
        //Draw active devCards (NB: only the active devCards are drawn here as button, inactive cards are drawn as Images in PlayerBoardClass)
        int counter = 0;
        List<Stack<Integer>> devCardsStacksOrNULL = Optional.ofNullable(getDevCardsStacks()).orElse(Collections.emptyList());
        for (Stack<Integer> stack : devCardsStacksOrNULL) {
            if (stack != null && !stack.empty()) {
                Integer cardNumber = stack.peek();
                GameButton devCardGameButton = new GameButton(ImageRepository.getInstance().getCardImage(cardNumber), getDevCardSizeX(), devCardSizeY);
                devCardGameButton.setBounds(getDevCardPosX()[counter], getDevCardPosY(), getDevCardSizeX(), devCardSizeY);
                this.add(devCardGameButton);
                devCardGameButton.setVisible(true);
                Pair<Integer, GameButton> pairToInsert = new Pair<>(cardNumber, devCardGameButton);
                devCardButtons.add(pairToInsert);

            }
            counter++;
        }

        //For the alreadyActivated Production, print earned resources
        if(compressedModel.getPlayerNames().indexOf(username)==compressedModel.getaP()) {
            drawToGetResourcesInProduction(compressedModel);
            resourcesOverActivatedCards = new ArrayList<>();
            for (Map.Entry<Integer, ArrayList<Resource>> production : activatedProductions.entrySet()) {
                GameButton button = null;
                for (Pair<Integer, GameButton> gameButtonPair : devCardButtons) {
                    if (gameButtonPair.getFirst().equals(production.getKey())) {
                        button = gameButtonPair.getSecond();
                    }
                }
                button.setGameButtonImage(ImageRepository.getInstance().getLeaderCardBackImage());
                int counterRes = 0;
                for (Resource res : production.getValue()) {
                    if (res != Resource.FAITH) {
                        GameButton resource = new GameButton(ImageRepository.getInstance().getResourceImage(res), RESOURCES_PRODUCTION_SIZE, RESOURCES_PRODUCTION_SIZE);
                        resource.setBounds(RESOURCES_PRODUCTION_X_Y_DEVIATION[counterRes][0], RESOURCES_PRODUCTION_X_Y_DEVIATION[counterRes][1], RESOURCES_PRODUCTION_SIZE, RESOURCES_PRODUCTION_SIZE);
                        button.setLayout(null);
                        button.add(resource);
                        resourcesOverActivatedCards.add(resource);
                        resource.setVisible(true);
                    }
                    counterRes++;
                }
            }
        }
    }

    /**
     * Activate the devCard button and the generic production button
     * @param compressedModel the updatedCompressed model
     */
    private void activateDevCardANDGenericProductionButtons(CompressedModel compressedModel) {

        //Generic Production
        //If this production wasn't already activated
        if (!compressedModel.getTurnAlreadyUsedProductionCardIDs().contains(-1)) {
            genericProductionButton = new JButton("<html>Generic<br />Production</html>");
            genericProductionButton.setBounds(220, 450, 100, 100);
            genericProductionButton.addActionListener(e -> executeProduction(compressedModel, e, true, false, 0));
            this.add(genericProductionButton);
        }


        //Development cards
        for (Pair<Integer, GameButton> pair : devCardButtons) {
            //If this production wasn't already activated
            if (!compressedModel.getTurnAlreadyUsedProductionCardIDs().contains(pair.getFirst())) {
                GameButton cardButton = pair.getSecond();
                cardButton.setEnabled(true);
                cardButton.addActionListener(e -> executeProduction(compressedModel, e, false, false, pair.getFirst()));
            }
        }

        // Active LeaderCards
        if(compressedModel.getTurnState()==TurnState.INITIAL_LEADER_ACTION){
            for (Triplet<Integer, GameButton, JButton> triplet : activeProductionLeaderCardButtons) {
                triplet.getSecond().addActionListener(e -> enableChoiceDiscardOrActivate(compressedModel, e, triplet, true));
            }
        }

        //End production button
        if (compressedModel.getTurnAlreadyUsedProductionCardIDs() != null && !compressedModel.getTurnAlreadyUsedProductionCardIDs().isEmpty()) {
            endProductionButton = new JButton("<html>END Production Phase</html>");
            endProductionButton.setBounds(220, 300, 100, 100);
            endProductionButton.addActionListener(e -> endProduction(e, compressedModel));
            this.add(endProductionButton);
            endProductionButton.setVisible(true);
            endProductionButton.setEnabled(true);
        }
    }

    /**
     * End of production button handler
     * @param event the event that called this method
     * @param compressedModel the updated compressed model
     */
    private void endProduction(ActionEvent event, CompressedModel compressedModel) {
        JButton pressed = (JButton) event.getSource();
        pressed.setEnabled(false);
        pressed.setVisible(false);
        PlayerWorkSpace.getInstance().clearPlayerEvent();
        PlayerWorkSpace.getInstance().endProductionEvent();
        for (Pair<Integer, GameButton> pair : devCardButtons) pair.getSecond().setEnabled(false);
        gui.notifyPlayerMessage();
        activatedProductions = new HashMap<>();
    }

    /**
     * Draw to get resources in production.
     *
     * @param compressedModel the compressed model
     */
    protected void drawToGetResourcesInProduction(CompressedModel compressedModel) {
        activatedProductions = new HashMap<>();
        List<Integer> usedCards = compressedModel.getTurnAlreadyUsedProductionCardIDs().stream().filter(x -> x >= 17).collect(Collectors.toList());
        for (Integer usedCard : usedCards) {
            List<ResourceStack> reward = CardRepository.getInstance().getDevCardByID(usedCard).getProductionPower().getReward();
            ArrayList<Resource> flatReward = new ArrayList<>();
            for (ResourceStack resourceStack : reward) {
                for (int num = 0; num < resourceStack.getAmount(); num++) {
                    flatReward.add(resourceStack.getResourceType());
                }
            }
            activatedProductions.put(usedCard, flatReward);
        }
    }

    /**
     * Execute production handler
     * @param compressedModel the updated compressed model
     * @param event the event that called this method
     * @param isGeneric is this a generic production?
     * @param isLeader is this a leader production?
     * @param cardNumber production card number
     */
    private synchronized void executeProduction(CompressedModel compressedModel, ActionEvent event, boolean isGeneric, boolean isLeader, Integer cardNumber) {


        //create the empty packet for the initial leader action
        if ((compressedModel.getTurnState() == TurnState.INITIAL_LEADER_ACTION) || (skippedInitialLeaderAction)) {
            PlayerWorkSpace.getInstance().clearPlayerEvent();
            PlayerWorkSpace.getInstance().registerLeaderAction(null, false);
            gui.notifyPlayerMessage();
            skippedInitialLeaderAction = false;
        }


        if (isGeneric) {
            genericProductionHandler(compressedModel, event);
        } else if (isLeader) {

            Resource outputRes = new ChooseResourceProductionDialog(false).showSingleResults();
            GameButton pressed = (GameButton) event.getSource();
            pressed.setEnabled(false);
            ArrayList<Resource> outputResArray = new ArrayList<>();
            outputResArray.add(outputRes);


            boolean productionActivable = true;

            //check if the player has enough resources (in all the deposits)
            LeaderCard leaderCard = CardRepository.getInstance().getLeaderCardByID(cardNumber);
            if (leaderCard.getPower().getSpecialAbilityType() == SpecialAbility.PRODUCTION) {

                Power powerOfProduction = leaderCard.getPower();
                List<ResourceStack> listInputs = ((ProductionPower) powerOfProduction).getRequirements();
                for (ResourceStack resourceStack : listInputs) {

                    if (resourceStack.getAmount() > getTotalAmountOfResources(resourceStack.getResourceType(), compressedModel))
                    {productionActivable = false;}

                    }

                if (productionActivable) {
                    PlayerWorkSpace.getInstance().clearPlayerEvent();
                    PlayerWorkSpace.getInstance().leadCardProductionEvent(cardNumber, outputResArray);
                    gui.notifyPlayerMessage();
                    pressed.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this, GameConstants.PRODUCTION_FAILED_NOT_ENOUGH_RESOURCES, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, GameConstants.GENERIC_ERROR, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {        //DEVELOPMENT CARD PRODUCTION

            GameButton pressed = (GameButton) event.getSource();
            pressed.setEnabled(false);
            boolean productionActivable = true;

            //check if the player has enough resources (in all the deposits)

            DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(cardNumber);
            List<ResourceStack> listInputs = developmentCard.getProductionPower().getRequirements();
            for (ResourceStack resourceStack : listInputs) {
                if (resourceStack.getAmount() > getTotalAmountOfResources(resourceStack.getResourceType(), compressedModel))
                    productionActivable = false;
            }

            if (productionActivable) {

                PlayerWorkSpace.getInstance().clearPlayerEvent();
                PlayerWorkSpace.getInstance().devCardProductionEvent(cardNumber);
                gui.notifyPlayerMessage();
            } else {
                JOptionPane.showMessageDialog(this, GameConstants.GENERIC_ERROR, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    /**
     * Generic production handler
     * @param compressedModel the updated compressed model
     * @param event the event that called this method
     */
    private void genericProductionHandler(CompressedModel compressedModel, ActionEvent event) {
        boolean requestedInputsAreOk = false;
        ArrayList<Pair<Resource, StorageType>> genericProductionResourceList = null;
        boolean alreadyTriedOnce = false;
        boolean exitIsClicked = false;

        List<ResourceStack> updatedWarehouseDepot = new ArrayList<>();
        updatedWarehouseDepot.add(getWd_layer1());
        updatedWarehouseDepot.add(getWd_layer2());
        updatedWarehouseDepot.add(getWd_layer3());

        List<ResourceStack> updatedSB = getStrongbox();

        while ((!requestedInputsAreOk) && (!exitIsClicked)) {
            requestedInputsAreOk = false;
            ChooseResourceProductionDialog chooseResourceProductionDialog = new ChooseResourceProductionDialog(true);

            //make a banner 'YOU HAVE NOT ENOUGH RESOURCES" appear in the dialog if we have already tried once
            if (alreadyTriedOnce) {
                chooseResourceProductionDialog.makeBannerWrongResources();
            }
            genericProductionResourceList = chooseResourceProductionDialog.showResults();
            boolean sameResourceAndStorage = genericProductionResourceList.get(0).getFirst() == genericProductionResourceList.get(1).getFirst()
                    && genericProductionResourceList.get(0).getSecond() == genericProductionResourceList.get(1).getSecond();

            //case if 'EXIT' is clicked
            if (chooseResourceProductionDialog.hasBeenClosed()) exitIsClicked = true;
            else {
                //check inputs (0 & 1 in the genericProductionResourceList )
                for (int i = 0; i < 2; i++) {
                    requestedInputsAreOk = false;
                    //chosen from WD
                    if (genericProductionResourceList.get(i).getSecond() == StorageType.WAREHOUSE_DEPOT) {

                        for (ResourceStack resourceStack : updatedWarehouseDepot) {

                            if ((resourceStack.getResourceType() == genericProductionResourceList.get(i).getFirst()) && (resourceStack.getAmount() > (sameResourceAndStorage ? 1 : 0))) {
                                requestedInputsAreOk = true;
                            }
                        }
                    }
                    //chosen from SB
                    else if (genericProductionResourceList.get(i).getSecond() == StorageType.STRONG_BOX) {

                        for (ResourceStack resourceStack : updatedSB) {
                            if (resourceStack.getResourceType() == genericProductionResourceList.get(i).getFirst() && resourceStack.getAmount() >= (sameResourceAndStorage ? 2 : 1)) {
                                requestedInputsAreOk = true;
                            }
                        }
                    }
                    //chosen from Leader card storage
                    else if (genericProductionResourceList.get(i).getSecond() == StorageType.LEADER_CARD) {

                        for (ResourceStack resourceStack : getResourcesInLeaderCardStorage()) {
                            if (resourceStack.getResourceType() == genericProductionResourceList.get(i).getFirst() && resourceStack.getAmount() >= (sameResourceAndStorage ? 2 : 1)) {
                                requestedInputsAreOk = true;

                            }
                        }
                    }

                    if (!requestedInputsAreOk) break;
                }
                if (!requestedInputsAreOk) alreadyTriedOnce = true;
                exitIsClicked = false;
            }
        }

        //if the player inserted resources that are valid do the production, otherwise he has necessarily clicked 'exit', in this case no package is sent
        if (!exitIsClicked) {
            JButton pressed = (JButton) event.getSource();
            pressed.setEnabled(false);
            Resource outRes = genericProductionResourceList.get(2).getFirst();
            genericProductionResourceList.remove(2);


            PlayerWorkSpace.getInstance().clearPlayerEvent();
            PlayerWorkSpace.getInstance().genericProductionEvent(genericProductionResourceList, outRes);
            gui.notifyPlayerMessage();
        }
    }

    /**
     * Gets the total amount of the selected resource owned by active player
     * @param res the Resource type
     * @param compressedModel the updated comrpessed model
     * @return the number of resources
     */
    private int getTotalAmountOfResources(Resource res, CompressedModel compressedModel) {
        int numberOfRes;
        try {
            numberOfRes = Arrays.stream(compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP())).filter((x) -> (x.getResourceType() == res)).findFirst().get().getAmount();
        } catch (NoSuchElementException e) {
            numberOfRes = 0;
        }

        return numberOfRes;
    }

}