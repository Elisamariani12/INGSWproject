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
import it.polimi.ingsw.common.exception.FullResourceStackException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Board that presents Market and Development Card Grid
 */
public class CommonBoard extends JPanel
{
    //Image sources
    private Image marketBoardBackground;
    private Image marbleBoardBackground;
    private Image marketArrowH, marketArrowV;

    /* ---- POSITIONING CONSTANTS  for 1280x720 ----- */
    private final int MARKET_BOARD_POS_X = 777;
    private final int MARKET_BOARD_POS_Y = 0;
    private final int MARKET_BOARD_SIZE_Y = 620;

    private final int MARBLE_BOARD_POS_X = 850;
    private final int MARBLE_BOARD_POS_Y = 78;
    private final int MARBLE_BOARD_SIZE_X = 330;

    private final int MARKET_ARROW_H_X = 1196;
    private final int MARKET_ARROW_H_INITIAL_Y = 147;
    private final int MARKET_ARROW_H_INTERVAL_Y = 55;
    private final int MARKET_ARROW_H_WIDTH = 63;

    private final int MARKET_ARROW_V_INITIAL_X = 924;
    private final int MARKET_ARROW_V_Y = 366;
    private final int MARKET_ARROW_V_INTERVAL_X = 55;
    private final int MARKET_ARROW_V_HEIGHT = 84;

    private final int MARBLE_FIRST_POS_X = 910;
    private final int MARBLE_FIRST_POS_Y = 151;
    private final int MARBLE_INTERVAL_X = 54;
    private final int MARBLE_INTERVAL_Y = 47;
    private final int MARBLE_SIZE = 48;
    private final int MARBLE_EXT_POS_X = 1115;
    private final int MARBLE_EXT_POS_Y = 96;

    private final int DEV_CARD_GRID_INITIAL_POS_X = 5;
    private final int DEV_CARD_GRID_INITIAL_POS_Y = 5;
    private final int DEV_CARD_GRID_INTERVAL_X = 180;
    private final int DEV_CARD_GRID_INTERVAL_Y = 270;
    private final int DEV_CARD_SIZE_Y = 250;

    //Components
    private GameButton[] marketButtonsH, marketButtonsV;
    private GameButton[][] devCardGridButtons;
    private JPanel gridPanel;
    private JScrollPane scrollPane;

    //State buffers
    private TurnState latestTurnState;
    private Resource[][] latestMarketState;
    private Resource latestExternalMarble;
    private int[][] latestGridState;
    private int[] latestDevCardSpace;
    private java.util.List<LeaderCard> latestLeaderCards;
    private Optional<Resource> whiteMarbleSubstitution1 = Optional.ofNullable(null);
    private Optional<Resource> whiteMarbleSubstitution2 = Optional.ofNullable(null);
    private java.util.List<ResourceStack> latestWarehouseDepot;
    private ResourceStack[] latestCumulatedStorage;

    private boolean discardRejection;

    //Reference to GUI class
    private GUI guiHandle;

    /**
     * Creates a new instance of the CommonPanel
     * @param guiHandle the gui instance
     */
    public CommonBoard(GUI guiHandle)
    {
        super();
        this.setLayout(null);
        this.guiHandle = guiHandle;
        this.discardRejection = false;

        initMarket();
        initDevCardGrid();

        this.latestDevCardSpace = new int[GameConstants.DEV_CARD_NUMBER_OF_SPACES];
    }

    /* ------- INIT FUNCTIONS ------- */

    /** Initializes market related components and graphics */
    private void initMarket()
    {
        //Resource loading
        ImageRepository imgRepo = ImageRepository.getInstance();

        marketBoardBackground = imgRepo.getMarketBoardImage();
        marbleBoardBackground = imgRepo.getMarbleBoardImg();
        marketArrowH = imgRepo.getMarketArrowImage(true);
        marketArrowV = imgRepo.getMarketArrowImage(false);

        //Market Buttons init
        marketButtonsH = new GameButton[GameConstants.MARKET_ROWS_COUNT];
        marketButtonsV = new GameButton[GameConstants.MARKET_COLS_COUNT];

        int marketButtonH_Height = Math.round((float) MARKET_ARROW_H_WIDTH / ImageUtils.getAspectRatio(marketArrowH));
        int marketButtonV_Width = Math.round(ImageUtils.getAspectRatio(marketArrowV) * (float) MARKET_ARROW_V_HEIGHT);

        //Handle row market arrows
        for(int i = 0; i < GameConstants.MARKET_ROWS_COUNT; i++)
        {
            marketButtonsH[i] = new GameButton(marketArrowH, MARKET_ARROW_H_WIDTH, marketButtonH_Height);
            marketButtonsH[i].setBounds(MARKET_ARROW_H_X, MARKET_ARROW_H_INITIAL_Y + i * MARKET_ARROW_H_INTERVAL_Y, MARKET_ARROW_H_WIDTH, marketButtonH_Height);
            final int index = i;
            marketButtonsH[i].addActionListener((event) -> executeMarketAction(true, index));
            add(marketButtonsH[i]);
        }

        //Handle column market arrows
        for(int j = 0; j < GameConstants.MARKET_COLS_COUNT; j++)
        {
            marketButtonsV[j] = new GameButton(marketArrowV, marketButtonV_Width, MARKET_ARROW_V_HEIGHT);
            marketButtonsV[j].setBounds(MARKET_ARROW_V_INITIAL_X + j * MARKET_ARROW_V_INTERVAL_X, MARKET_ARROW_V_Y, marketButtonV_Width, MARKET_ARROW_V_HEIGHT);
            final int index = j;
            marketButtonsV[j].addActionListener((event) -> executeMarketAction(false, index));
            add(marketButtonsV[j]);
        }

        //Before the first model state arrives this will be the placeholder values
        latestMarketState = new Resource[GameConstants.MARKET_ROWS_COUNT][GameConstants.MARKET_COLS_COUNT];
        for(int j = 0; j < GameConstants.MARKET_ROWS_COUNT; j++)
            for(int i = 0; i < GameConstants.MARKET_COLS_COUNT; i++)
                latestMarketState[j][i] = Resource.WHITE;

        latestExternalMarble = Resource.WHITE;

    }

    /** Initializes DevCardGrid related components and graphics */
    private void initDevCardGrid()
    {
        final int gridWidth = MARKET_BOARD_POS_X - 15;
        final int gridHeight = GameConstants.WINDOW_HEIGHT + 130;
        final int scrollPaneWidth = MARKET_BOARD_POS_X - 10;
        final int scrollPaneHeight = MARKET_BOARD_SIZE_Y - 5;
        final int scrollMouseWheelIncrement = 16;

        //Dev Card Grid Buttons init
        devCardGridButtons = new GameButton[GameConstants.DEV_CARD_GRID_ROWS_COUNT][GameConstants.DEV_CARD_GRID_COLS_COUNT];
        gridPanel = new JPanel();
        gridPanel.setLayout(null);
        gridPanel.setBounds(0, 0, gridWidth, gridHeight);
        gridPanel.setPreferredSize(new Dimension(gridWidth, gridHeight));

        //Create scroll pane to contain panel
        scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.darkGray, 2, true));
        scrollPane.setBounds(0, 0, scrollPaneWidth, scrollPaneHeight);
        scrollPane.getVerticalScrollBar().setUnitIncrement(scrollMouseWheelIncrement);

        int dev_card_size_x = Math.round((float) DEV_CARD_SIZE_Y * ImageUtils.getAspectRatio(ImageRepository.getInstance().getCardImage(1)));
        for(int j = 0; j < GameConstants.DEV_CARD_GRID_ROWS_COUNT; j++)
        {
            for(int i = 0; i < GameConstants.DEV_CARD_GRID_COLS_COUNT; i++)
            {
                //Initial image is null
                devCardGridButtons[j][i] = new GameButton(null, dev_card_size_x, DEV_CARD_SIZE_Y);
                devCardGridButtons[j][i].setBounds(DEV_CARD_GRID_INITIAL_POS_X + i * DEV_CARD_GRID_INTERVAL_X,
                        DEV_CARD_GRID_INITIAL_POS_Y + j * DEV_CARD_GRID_INTERVAL_Y,
                        dev_card_size_x, DEV_CARD_SIZE_Y);
                final int row = j, col = i;
                devCardGridButtons[j][i].addActionListener((event) -> executeDevCardPurchase(row, col));
                gridPanel.add(devCardGridButtons[j][i]);
            }
        }

        add(scrollPane);

        latestGridState = new int[GameConstants.DEV_CARD_GRID_ROWS_COUNT][GameConstants.DEV_CARD_GRID_COLS_COUNT];
        for(int j = 0; j < GameConstants.DEV_CARD_GRID_ROWS_COUNT; j++)
            for(int i = 0; i < GameConstants.DEV_CARD_GRID_COLS_COUNT; i++)
                latestGridState[j][i] = -1;
    }

    /* --------- PAINT FUNCTIONS --------- */

    @Override
    public void paintComponent(Graphics g)
    {
        int market_board_size_x = Math.round(ImageUtils.getAspectRatio(marketBoardBackground) * (float) MARKET_BOARD_SIZE_Y);
        int marble_board_size_y = Math.round((float)MARBLE_BOARD_SIZE_X / ImageUtils.getAspectRatio(marbleBoardBackground));

        g.drawImage(marketBoardBackground, MARKET_BOARD_POS_X, MARKET_BOARD_POS_Y, market_board_size_x, MARKET_BOARD_SIZE_Y, null);
        g.drawImage(marbleBoardBackground, MARBLE_BOARD_POS_X, MARBLE_BOARD_POS_Y, MARBLE_BOARD_SIZE_X, marble_board_size_y, null);
        paintMarketState(g);
    }

    /** Paints the marbles in the correct position
     * @param g Handle to the Graphics object of the paintComponent method
     */
    private void paintMarketState(Graphics g)
    {
        boolean hasWhiteSub = whiteMarbleSubstitution1.isPresent();

        for(int j = 0; j < GameConstants.MARKET_ROWS_COUNT; j++)
        {
            for(int i = 0; i < GameConstants.MARKET_COLS_COUNT; i++)
            {
                Image marbleSprite = ImageRepository.getInstance().getMarbleImage(latestMarketState[j][i], hasWhiteSub);
                g.drawImage(marbleSprite, MARBLE_FIRST_POS_X + i * MARBLE_INTERVAL_X, MARBLE_FIRST_POS_Y + j * MARBLE_INTERVAL_Y, MARBLE_SIZE, MARBLE_SIZE, null);
            }
        }

        Image extSprite = ImageRepository.getInstance().getMarbleImage(latestExternalMarble, hasWhiteSub);
        g.drawImage(extSprite, MARBLE_EXT_POS_X, MARBLE_EXT_POS_Y, MARBLE_SIZE, MARBLE_SIZE, null);
    }

    /** Changes dev card buttons' sprites to conform to the grid state*/
    private synchronized void applyDevCardGraphics()
    {
        for(int j = 0; j < GameConstants.DEV_CARD_GRID_ROWS_COUNT; j++)
            for(int i = 0; i < GameConstants.DEV_CARD_GRID_COLS_COUNT; i++)
                if(latestGridState[j][i] < 0)
                    devCardGridButtons[j][i].setGameButtonImage(null);
                else
                {
                    Image newImage = ImageRepository.getInstance().getCardImage(latestGridState[j][i]);
                    devCardGridButtons[j][i].setGameButtonImage(newImage);
                }
    }

    /**
     * Updates the view with the given compressedModel information
     *
     * @param compressedModel Up to date compressed model
     */
    public synchronized void updateView(CompressedModel compressedModel)
    {
        String currentPlayerUsername = compressedModel.getPlayerNames().get(compressedModel.getaP());
        boolean isPlayerTurn = PlayerWorkSpace.getInstance().getPlayerUsername().equals(currentPlayerUsername);

        //Update data
        this.latestTurnState = compressedModel.getTurnState();
        this.latestMarketState = compressedModel.getMarketState();
        this.latestExternalMarble = compressedModel.getExternalMarketState();
        this.latestGridState = compressedModel.getDevCardGridState();
        //There's something wrong with compressedModel reception
        if(isPlayerTurn && compressedModel.getaPWD().size() == 3)
        {
            this.latestCumulatedStorage = compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP());
            this.latestWarehouseDepot = compressedModel.getaPWD();
        }
        else if (compressedModel.getaPWD().size() != 3)
            System.out.println("WARNING: Non-Conforming packet at CommonBoard updateView: " + compressedModel.getaPWD());

        //Update dev card grid graphics
        applyDevCardGraphics();

        TurnState currentTurnState = compressedModel.getTurnState();

        boolean canInteract = isPlayerTurn && currentTurnState != null &&
                (currentTurnState.equals(TurnState.CHOOSE_ACTION) ||
                currentTurnState.equals(TurnState.MARKET) ||
                currentTurnState.equals(TurnState.DEV_CARD_PURCHASE) ||
                currentTurnState.equals(TurnState.INITIAL_LEADER_ACTION));

        boolean leaderSkip = canInteract && currentTurnState.equals(TurnState.INITIAL_LEADER_ACTION);

        boolean canDoMarket = canInteract &&
                (leaderSkip || currentTurnState.equals(TurnState.CHOOSE_ACTION) ||
                currentTurnState.equals(TurnState.MARKET));

        boolean canDoPurchase = canInteract &&
                (leaderSkip || currentTurnState.equals(TurnState.CHOOSE_ACTION) ||
                currentTurnState.equals(TurnState.DEV_CARD_PURCHASE));

        //Retrieve White Marble substitution
        if(canInteract)
        {
            latestLeaderCards = compressedModel.getAllPlayersActiveLeaderCards().get(compressedModel.getaP())
                    .stream().map(CardRepository.getInstance()::getLeaderCardByID).collect(Collectors.toList());

            java.util.List<Resource> substitutions = latestLeaderCards.stream()
                    .filter(card -> card.getPower().getSpecialAbilityType() == SpecialAbility.WHITE_MARBLE_SUBSTITUTION)
                    .map(LeaderCard::getPower)
                    .map(power -> (AbilityPower)power)
                    .map(AbilityPower::getResourceType)
                    .collect(Collectors.toList());

            //If there are any WMS, register them in the optional variables
            if(substitutions.size() > 0) this.whiteMarbleSubstitution1 = Optional.of(substitutions.get(0));
            if(substitutions.size() == 2) this.whiteMarbleSubstitution2 = Optional.of(substitutions.get(1));

            java.util.List<Stack<Integer>> devSpaceList = compressedModel.getDevCardSpace().get(compressedModel.getaP());
            java.util.List<Integer> topMostDevSpace = new ArrayList<>();
            //Protection for EmptyStackException
            for(Stack<Integer> deck : devSpaceList)
            {
                if(deck.isEmpty()) topMostDevSpace.add(-1);
                else topMostDevSpace.add(deck.peek());
            }

            for(int i = 0; i < GameConstants.DEV_CARD_NUMBER_OF_SPACES; i++) this.latestDevCardSpace[i] = topMostDevSpace.get(i);
        }

        //Only enable buttons when they can be pressed
        for(GameButton marketArrow : marketButtonsH) marketArrow.setEnabled(canDoMarket);
        for(GameButton marketArrow : marketButtonsV) marketArrow.setEnabled(canDoMarket);

        //If any deck is empty, the corresponding button will have to be disabled
        for(int j = 0; j < GameConstants.DEV_CARD_GRID_ROWS_COUNT; j++)
            for(int i = 0; i < GameConstants.DEV_CARD_GRID_COLS_COUNT; i++)
            {
                boolean isCardEnabled = canDoPurchase && latestGridState[j][i] > 0;
                devCardGridButtons[j][i].setEnabled(isCardEnabled);
            }

        repaint();
    }

    /** Does all necessary actions to request a market action to the server
     * @param isHorizontal Is the request button an horizontal market arrow or a vertical one?
     * @param index Index of the marketButton
     */
    private synchronized void executeMarketAction(boolean isHorizontal, int index)
    {

        int row, col;
        HashMap<Resource, Integer> wmsMap;

        row = !isHorizontal ? -1 : index;
        col = isHorizontal ? -1 : index;

        int whiteMarbleCount = countResources(isHorizontal, isHorizontal ? row : col, Optional.of(Resource.WHITE));

        //if no WMS
        if(whiteMarbleSubstitution1.isEmpty())
        {
            wmsMap = null;
        }
        //if the WMS is unique
        else if(whiteMarbleSubstitution2.isEmpty())
        {
            wmsMap = new HashMap<>();

            //Count white marbles in the selected strip
            wmsMap.put(whiteMarbleSubstitution1.get(), whiteMarbleCount);
        }
        //if there are two WMS
        else
        {
            wmsMap = new HashMap<>();

            boolean hasGivenCorrectAnswer = false;
            while(!hasGivenCorrectAnswer)
            {
                WhiteSubstitutionDialog requestDialog = new WhiteSubstitutionDialog(whiteMarbleCount);
                JOptionPane.showMessageDialog(this, requestDialog, GameConstants.RESOURCE_SUBSTITUTION_REQUEST, JOptionPane.QUESTION_MESSAGE);
                hasGivenCorrectAnswer = requestDialog.wasChoiceCorrect();
                if(hasGivenCorrectAnswer) wmsMap = requestDialog.getChoices();
            }

            wmsMap.put(whiteMarbleSubstitution1.get(), whiteMarbleCount);
        }

        //Request resources to discard and register the action
        java.util.List<Resource> discardedResources = getDiscardedResources(row, col, wmsMap);

        if(discardRejection)
        {
            discardRejection = false;
            return;
        }

        boolean leaderSkip = (latestTurnState == TurnState.INITIAL_LEADER_ACTION);

        //Skip initial leader action
        if(leaderSkip)
        {
            PlayerWorkSpace.getInstance().registerLeaderAction(null, false);
            guiHandle.notifyPlayerMessage();
        }


        PlayerWorkSpace.getInstance().registerMarketAction(row, col, wmsMap, !discardedResources.isEmpty(), discardedResources);
        guiHandle.notifyPlayerMessage();

    }

    /** Does all necessary actions to request a Development Card Purchase to the server
     * @param row Row on DevCardGrid
     * @param col Col on DevCardGrid
     */
    private synchronized void executeDevCardPurchase(int row, int col)
    {
        //Get requirements and subtract discounts
        int cardID = latestGridState[row][col];
        DevelopmentCard requestedCard = cardID > 0 ? CardRepository.getInstance().getDevCardByID(cardID) : null;

        if(requestedCard == null) return;

        java.util.List<ResourceStack> requirements = requestedCard.getRequiredResources().stream().collect(Collectors.toList());
        java.util.List<Resource> discounts = latestLeaderCards.stream()
                .filter(card -> card.getPower().getSpecialAbilityType() == SpecialAbility.DISCOUNT)
                .map(LeaderCard::getPower)
                .map(power -> (AbilityPower) power)
                .map(AbilityPower::getResourceType)
                .collect(Collectors.toList());

        //Remove discounts from requirements
        for(ResourceStack stack : requirements)
        {
            if(discounts.contains(stack.getResourceType()))
            {
                if(stack.getAmount() == 1) requirements.remove(stack);
                else
                    try {stack.setAmount(stack.getAmount() - 1);}
                    catch (FullResourceStackException e) {e.printStackTrace();}
            }
        }

        //Get cumulated resources
        boolean canExecuteTransaction = true;
        //Convert array to list
        ArrayList<ResourceStack> storageList = new ArrayList<>();
        for(ResourceStack stack : latestCumulatedStorage) storageList.add(stack);

        for(ResourceStack reqStack : requirements)
        {
            Optional<ResourceStack> match = storageList.stream()
                    .filter(stack -> stack.getResourceType() == reqStack.getResourceType())
                    .findFirst();

            if(match.isEmpty() || match.get().getAmount() < reqStack.getAmount())
                canExecuteTransaction = false;
        }

        //Formulate request
        if(canExecuteTransaction)
        {
            CardSlotSelectionDialog dialog = new CardSlotSelectionDialog(this.latestDevCardSpace, requestedCard.getCardLevel());
            int result = JOptionPane.showConfirmDialog(this, dialog, "Choose a card slot", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if(result == JOptionPane.OK_OPTION && dialog.getChosenSlot() > 0)
            {
                boolean leaderSkip = latestTurnState.equals(TurnState.INITIAL_LEADER_ACTION);

                if(leaderSkip)
                {
                    PlayerWorkSpace.getInstance().registerLeaderAction(null, false);
                    guiHandle.notifyPlayerMessage();
                }
                PlayerWorkSpace.getInstance().devCardPurchaseAction(latestGridState[row][col], row, col, dialog.getChosenSlot());
                guiHandle.notifyPlayerMessage();
            }
        }
        else JOptionPane.showMessageDialog(this, "You do not have enough resources to buy that card", "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Counts the number of resources in a market strip
     * @param isHorizontal Is is an horizontal strip or a vertical one?
     * @param index Index of the row / col
     * @param filter Optional resource type, if left empty, it will count every marble that is not Faith
     * @return Count
     */
    private int countResources(boolean isHorizontal, int index, Optional<Resource> filter)
    {
        int resourceCount = 0;
        for(int n = 0; n < (isHorizontal ? GameConstants.MARKET_COLS_COUNT : GameConstants.MARKET_ROWS_COUNT); n++)
        {
            Resource currentResource = latestMarketState[isHorizontal ? index : n][isHorizontal ? n : index];
            if((filter.isEmpty() && currentResource != Resource.FAITH) || currentResource == filter.get()) resourceCount++;
        }

        return resourceCount;
    }

    /** Returns a list of discarded resources, retrieved using a JOptionPane dialog
     * @param row Market row (-1 if it's a col)
     * @param col Market column (-1 if it's a row)
     * @param wmsMap Map of white marble substitution choices
     * @return List of discarded Resources
     */
    private synchronized java.util.List<Resource> getDiscardedResources(int row, int col, HashMap<Resource, Integer> wmsMap)
    {
        java.util.List<Resource> resourcesToPlace = new ArrayList<>();
        java.util.List<Resource> nonWhiteResources = new ArrayList<>();

        //WMS Resources are to be evaluated
        if(wmsMap != null)
        {
            for (Resource res : wmsMap.keySet()) {
                for(int i = 0; i < wmsMap.get(res); i++) resourcesToPlace.add(res);
            }
        }

        //Put in all resources in the market strip
        for(int n = 0; n < (row >= 0 ? GameConstants.MARKET_COLS_COUNT : GameConstants.MARKET_ROWS_COUNT); n++)
        {
            Resource currentResource = latestMarketState[row >= 0 ? row : n][col >= 0 ? col : n];
            if(currentResource != Resource.WHITE && currentResource != Resource.FAITH)
            {
                resourcesToPlace.add(currentResource);
                nonWhiteResources.add(currentResource);
            }
        }

        //Ask with dialog
        if(!resourcesToPlace.isEmpty())
        {
            MarketDiscardDialog discardDialog = new MarketDiscardDialog(this.latestWarehouseDepot, resourcesToPlace);
            int result = JOptionPane.showConfirmDialog(this, discardDialog, "Choose resources to keep", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if(result == JOptionPane.OK_OPTION)
            {
                java.util.List<Resource> selected = discardDialog.getSelectedResources();
                //Copy
                java.util.List<Resource> discarded = resourcesToPlace.stream().collect(Collectors.toList());
                for(Resource res : selected) discarded.remove(res);

                //White marble substitution discarding is not real discarding
                if(whiteMarbleSubstitution1.isPresent())
                {
                    for (Resource subRes : wmsMap.keySet())
                    {
                        final Predicate<Resource> isSameResource = (res) -> res == subRes;

                        //How many white marbles do I want to ignore?
                        int totalDiscard = (int) discarded.stream().filter(isSameResource).count();
                        int wmsDiscardAmount = Math.min(wmsMap.get(subRes), totalDiscard);

                        //How many white marbles am I substituting?
                        int totalAcquire = (int) selected.stream().filter(isSameResource).count();
                        int actualResourceCount = (int) nonWhiteResources.stream().filter(isSameResource).count();
                        int wmsAcquireAmount = totalAcquire - actualResourceCount;

                        //Remove WMS from discarded resources list
                        for (int i = 0; i < wmsDiscardAmount; i++) discarded.remove(subRes);
                        wmsMap.put(subRes, wmsAcquireAmount);
                    }

                    for(Resource subRes : wmsMap.keySet())
                        if(wmsMap.get(subRes) == 0) wmsMap.remove(subRes);
                }

                return discarded;
            }
            else
            {
                this.discardRejection = true;
                return new ArrayList<>();
            }

        }
        else return new ArrayList<>();
    }
}
