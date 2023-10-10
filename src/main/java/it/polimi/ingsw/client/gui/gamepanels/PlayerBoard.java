package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.components.GameButton;
import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.client.util.ImageUtils;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.exception.FullResourceStackException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.*;

/**
 * The type Player board. YourPlayerBoard and OthersPlayerBoard are inherited from it
 */
public abstract class PlayerBoard extends JPanel
{

    //Image sources
    private Image playerSpace;
    private Image leaderCardBack;
    private Image red_cross_Img;

    //Elements memory
    private String username;
    private boolean myplayerIsDisconnected;
    private List<ResourceStack> resourcesInLeaderCardStorage;
    private ResourceStack wd_layer1;    //largest one
    private ResourceStack wd_layer2;
    private ResourceStack wd_layer3;
    private List<ResourceStack> strongbox;
    private int coin_sb;
    private int shield_sb;
    private int stone_sb;
    private int servant_sb;
    private List<Stack<Integer>> devCardsStacks;
    private int myPosition;
    private int lorenzoPosition;
    private boolean isSinglePlayer;
    private boolean[] myPopeTiles;
    private ActionTokenType lastPickedActionToken;
    private List<Integer> allMyActiveLeaderCards;
    private List<Integer> allMyInactiveLeaderCards;


    /* ---- POSITIONING CONSTANTS ----- */
    private static final int PLAYER_SPACE_POS_X = 0;
    private static final int PLAYER_SPACE_POS_Y = 0;
    private static final int PLAYER_SPACE_SIZE_X = 900;
    private static final int LEADER_CARDS_POS_X = 980;
    private static final int[] LEADER_CARD_POS_Y = {0, 270};
    private static final int LEADER_CARD_SIZE_X = 170;
    private static final int LEADER_CARD_RESOURCE_X=1008;
    private static final int[] LEADER_CARD_RESOURCE_Y = {190, 460};
    private static final int LEADER_CARD_RESOURCE_SIZE=50;

    private static final int DEV_CARD_SIZE_X = 150;
    private static final int[] DEV_CARD_POS_X= {352, 523, 702};
    private static final int DEV_CARD_POS_Y= 290;
    private static final int DEV_CARD_SHIFT = 45;

    private static final int RED_CROSS_SIZE_X=50;
    private static final int RED_CROSS_SIZE_Y=57;
    private static final int[] RED_CROSS_POSITIONS_X={29,75,118,118,118,162,206,250,294,338,338,338,382,426,470,515,559,559,559,603,647,691,735,779,823};
    private static final int[] RED_CROSS_POSITIONS_Y={117,117,117,73,29,29,29,29,29,29,73,117,117,117,117,117,117,73,29,29,29,29,29,29,29};

    private static final int SB_RESOURCES_SIZE=30;
    private static final int SB_RESOURCES_X1=30;
    private static final int SB_RESOURCES_X2=106;
    private static final int SB_RESOURCES_Y1=500;
    private static final int SB_RESOURCES_Y2=540;

    private static final int WD_RESOURCES_SIZE=35;
    private static final int WD_RESOURCES_X1=67;
    private static final int WD_RESOURCES_X2=81;
    private static final int WD_RESOURCES_X3=100;
    private static final int WD_RESOURCES_Y1=390;
    private static final int WD_RESOURCES_Y2=333;
    private static final int WD_RESOURCES_Y3=275;

    private static final int POPETILE_SIZE=60;
    private static final int[] POPETILE_X={223,444,708};
    private static final int[] POPETILE_Y={94,50,94};

    private static final int ACTION_TOKEN_POS_X=930;
    private static final int ACTION_TOKEN_POS_Y=540;
    private static final int ACTION_TOKEN_SIZE=75;


    //Components
    private JButton activeInactivePlayerButton;
    private JButton button_coin_strongBox;
    private JButton button_stone_strongBox;
    private JButton button_shield_strongBox;
    private JButton button_servant_strongBox;

    /**
     * Instantiates a new Player board.
     *
     * @param username the username of the player that owns this board.
     * @param gui      the gui
     */
    public PlayerBoard(String username, GUI gui) {
        super();
        this.username = username;
        this.setLayout(null);
        this.lorenzoPosition=0;
        this.strongbox=new ArrayList<>();
        this.lastPickedActionToken=null;
        this.myplayerIsDisconnected=false;
        this.allMyActiveLeaderCards=new ArrayList<>();
        this.allMyInactiveLeaderCards=new ArrayList<>();
        this.resourcesInLeaderCardStorage=new ArrayList<>();
        loadResourceBackground();

        //for the strongBox...
        initializeStrongBoxButtons();

        //for the warehouse depot
        wd_layer1=new ResourceStack();wd_layer2=new ResourceStack();wd_layer3=new ResourceStack();
        try {
            wd_layer1.setMaxSize(GameConstants.WAREHOUSE_DEPOT_CAPACITY_OF_LAYERS[0]);
            wd_layer2.setMaxSize(GameConstants.WAREHOUSE_DEPOT_CAPACITY_OF_LAYERS[1]);
            wd_layer3.setMaxSize(GameConstants.WAREHOUSE_DEPOT_CAPACITY_OF_LAYERS[2]);

        } catch (FullResourceStackException e) {e.printStackTrace();}


        //starting with all the pope tiles to 'false'
        myPopeTiles= new boolean[]{false,false,false};

    }

    /**
     * Load resource background.
     */
    protected void loadResourceBackground(){
        ImageRepository imgRepo = ImageRepository.getInstance();
        playerSpace = imgRepo.getPersonalBoardImage(true);
        leaderCardBack = imgRepo.getLeaderCardBackImage();
        red_cross_Img=imgRepo.getRedCrossImage();
    }

    //---------------------------------------- FILLERS (& DRAWERS) FOR LOCAL ATTRIBUTES ------------------------------------------

    /**
     * Pope tiles filler in faith track.
     *
     * @param compressedModel the compressed model that contains this info
     */
    protected void popeTilesFiller(CompressedModel compressedModel){
        myPopeTiles=compressedModel.getPopeFavours().get(compressedModel.getPlayerNames().indexOf(username));
    };

    /**
     * Last picked action token filler.
     *
     * @param compressedModel the compressed model that contains this info
     */
    protected  void lastPickedActionTokenFiller(CompressedModel compressedModel){
        if(compressedModel.getPickedActionToken()!=null)lastPickedActionToken=compressedModel.getPickedActionToken();
    }

    /**
     * Faith track filler.
     *
     * @param compressedModel the compressed model that contains this info
     */
    protected void faithTrackFiller(CompressedModel compressedModel){
        myPosition=compressedModel.getPositionsOfFaithTrack().get(compressedModel.getPlayerNames().indexOf(username));

        if((compressedModel.getPlayerNames().size()==1)&&(compressedModel.getHasSessionStarted())) {
            isSinglePlayer=true;
            lorenzoPosition=compressedModel.getPositionsOfFaithTrack().get(1);
        }
    }

    /**
     * Strong box filler.
     *
     * @param compressedModel the compressed model that contains this info
     */
    protected void strongBoxFiller(CompressedModel compressedModel){
        int playerIndex =  compressedModel.getPlayerNames().indexOf(username);
        for (ResourceStack r : compressedModel.getSelectedPlayerSB(playerIndex)) {
            if (r.getResourceType() == Resource.STONE) {
                stone_sb = r.getAmount();
            } else if (r.getResourceType() == Resource.SHIELD) {
                shield_sb = r.getAmount();
            } else if (r.getResourceType() == Resource.SERVANT) {
                servant_sb = r.getAmount();
            } else if (r.getResourceType() == Resource.COIN) {
                coin_sb = r.getAmount();
            }
        }

        //needed in productions, in your playerboard
        strongbox=compressedModel.getSelectedPlayerSB(playerIndex);

        initializeStrongBoxButtons();
    }


    /**
     * Ware house depot filler.
     *
     * @param compressedModel the compressed model that contains this info
     */
    protected void wareHouseDepotFiller(CompressedModel compressedModel){
        int playerIndex =  compressedModel.getPlayerNames().indexOf(username);
        boolean layer1filled=false;
        boolean layer2filled=false;
        boolean layer3filled=false;
        ResourceStack maxAmountLayer=new ResourceStack();
        List<ResourceStack> copyOfResourcesInWD= new ArrayList<>(compressedModel.getSelectedPlayerWD(playerIndex));;
        if(!copyOfResourcesInWD.isEmpty()) {
            //find what is the most filled resourceStack and put it in the largest layer
            for (ResourceStack r : copyOfResourcesInWD) {
                if (r.getAmount() > maxAmountLayer.getAmount()) maxAmountLayer = r;
            }
            wd_layer1 = maxAmountLayer;
            maxAmountLayer = new ResourceStack();
            copyOfResourcesInWD.remove(wd_layer1);

            if (!copyOfResourcesInWD.isEmpty()) {
                for (ResourceStack r : copyOfResourcesInWD) {
                    if (r.getAmount() > maxAmountLayer.getAmount()) maxAmountLayer = r;
                }
                wd_layer2 = maxAmountLayer;
                maxAmountLayer = new ResourceStack();
                copyOfResourcesInWD.remove(wd_layer2);

                if (!copyOfResourcesInWD.isEmpty()) {
                    wd_layer3 = copyOfResourcesInWD.get(0);
                }
            }
        }

    }

    /**
     * Change the backGround of the panel if the player disconnects
     *
     * @param compressedModel the compressed model that contains this info
     */
    private void backgroundChangerFiller(CompressedModel compressedModel) {
        if(compressedModel.getDisconnectedPlayers().contains(username)){playerSpace=ImageRepository.getInstance().getPersonalBoardImage(false);myplayerIsDisconnected=true;}
        else {playerSpace=ImageRepository.getInstance().getPersonalBoardImage(true);myplayerIsDisconnected=false;}
    }


    /**
     * Leader cards filler and drawer.
     *
     * @param compressedModel the compressed model that contains this info
     */
    protected void leaderCardsFillerAndDrawer(CompressedModel compressedModel){
        ImageRepository imgRepo = ImageRepository.getInstance();
        int leaderCardSizeY = Math.round((float)LEADER_CARD_SIZE_X / ImageUtils.getAspectRatio(leaderCardBack));

        //UPDATES THE ATTRIBUTE ALLMYACTIVELEADERCARDS ONLY IF HE IS THE ACTIVE PLAYER, this attribute is needed to enable the buttons activate/discard/activate prod
        if(compressedModel.getaP() == compressedModel.getPlayerNames().indexOf(username) && compressedModel.getTurnState() != TurnState.SETUP_PLAYER_CHOICES) {
            this.allMyActiveLeaderCards = compressedModel.getAllPlayersActiveLeaderCards().get(compressedModel.getaP());
            this.allMyInactiveLeaderCards=compressedModel.getaPHiddenChosenLeaderCards();
        }

        //fill in the resources in the leadercard storage
        resourcesInLeaderCardFiller(compressedModel);

        //draw resources in leader card storage
        if(!resourcesInLeaderCardStorage.isEmpty()) {
            for (int i = 0; i < resourcesInLeaderCardStorage.size(); i++) {
                for(int counterResourcesInOneCard=0;counterResourcesInOneCard<resourcesInLeaderCardStorage.get(i).getAmount();counterResourcesInOneCard++){
                    GameButton resourceButton=new GameButton(ImageRepository.getInstance().getResourceImage(resourcesInLeaderCardStorage.get(i).getResourceType()),LEADER_CARD_RESOURCE_SIZE,LEADER_CARD_RESOURCE_SIZE);
                    boolean findCard=false;
                    if(allMyActiveLeaderCards!=null) {
                        if (allMyActiveLeaderCards.size() > 1) {
                            LeaderCard leaderCard2 = CardRepository.getInstance().getLeaderCardByID(allMyActiveLeaderCards.get(1));
                            if (leaderCard2.getPower().getSpecialAbilityType().equals(SpecialAbility.STORAGE)) {
                                if (((AbilityPower) leaderCard2.getPower()).getResourceType() == resourcesInLeaderCardStorage.get(i).getResourceType()) {
                                    resourceButton.setBounds(LEADER_CARD_RESOURCE_X + counterResourcesInOneCard * 66, LEADER_CARD_RESOURCE_Y[1], LEADER_CARD_RESOURCE_SIZE, LEADER_CARD_RESOURCE_SIZE);
                                    findCard = true;
                                }
                            }
                        }
                        if ((allMyActiveLeaderCards.size() > 0) && (!findCard)) {
                            resourceButton.setBounds(LEADER_CARD_RESOURCE_X + counterResourcesInOneCard * 66, LEADER_CARD_RESOURCE_Y[0], LEADER_CARD_RESOURCE_SIZE, LEADER_CARD_RESOURCE_SIZE);
                        }
                    }
                    for(MouseListener mouseListener: resourceButton.getMouseListeners()) resourceButton.removeMouseListener(resourceButton);
                    resourceButton.setVisible(true);
                    add(resourceButton);
                    repaint();
                }
            }
        }

        drawLeaderCards(compressedModel);

    }

    protected void resourcesInLeaderCardFiller(CompressedModel compressedModel){
        int playerIndex =  compressedModel.getPlayerNames().indexOf(username);
        resourcesInLeaderCardStorage=compressedModel.getSelectedPlayerLeaderStorage(playerIndex);
    }

    /**
     * Abstract method to display bought development card, overwritten in the 2 subclasses
     *
     * @param compressedModel the received compress model
     */
    protected abstract void drawLeaderCards(CompressedModel compressedModel);

    /**
     * Dev cards filler and drawer.
     *
     * @param compressedModel the compressed model that contains this info
     */
    protected void devCardsFillerAndDrawer(CompressedModel compressedModel){
         devCardsStacks = compressedModel.getDevCardSpace().get(compressedModel.getPlayerNames().indexOf(username));

         //overwrite this method in the 2 subclasses, in 'otherplayerboard' the buttons will be deactivated (and removed mouse listeners)
         drawDevCards(compressedModel);
    }

    /**
     * Draw dev cards.
     * Overwrite this method in the 2 subclasses, in 'otherplayerboard' the buttons will be deactivated (and removed mouse listeners)
     *
     * @param compressedModel the compressed model that contains this info
     */
    protected abstract void drawDevCards(CompressedModel compressedModel);

    //---------------------------------------- PAINT COMPONENT -------------------------------------------------------------------
    @Override
    public void paintComponent(Graphics g)
    {
        int devCardSizeY = Math.round((float)DEV_CARD_SIZE_X / ImageUtils.getAspectRatio(leaderCardBack));
        //Sizes finder
        int playerSpaceSizeY = Math.round((float)PLAYER_SPACE_SIZE_X / ImageUtils.getAspectRatio(playerSpace));

        //DRAW BACKGROUND
        g.drawImage(ImageRepository.getInstance().getGenericProductionDialogBackground(),0,0,1280,720,null);



        //Draw playerSpace
        g.drawImage(playerSpace, PLAYER_SPACE_POS_X, PLAYER_SPACE_POS_Y, PLAYER_SPACE_SIZE_X, playerSpaceSizeY, null);


        if(!myplayerIsDisconnected) {
            //Draw position
            g.drawImage(red_cross_Img, RED_CROSS_POSITIONS_X[myPosition], RED_CROSS_POSITIONS_Y[myPosition], RED_CROSS_SIZE_X, RED_CROSS_SIZE_Y, null);
            if (isSinglePlayer)
                g.drawImage(ImageRepository.getInstance().getBlackCrossImage(), RED_CROSS_POSITIONS_X[lorenzoPosition] - 8, RED_CROSS_POSITIONS_Y[lorenzoPosition], RED_CROSS_SIZE_X, RED_CROSS_SIZE_Y, null);


            //DRAW STRONGBOX
            g.drawImage(ImageRepository.getInstance().getResourceImage(Resource.COIN), SB_RESOURCES_X1, SB_RESOURCES_Y1, SB_RESOURCES_SIZE, SB_RESOURCES_SIZE, null);
            g.drawImage(ImageRepository.getInstance().getResourceImage(Resource.STONE), SB_RESOURCES_X1, SB_RESOURCES_Y2, SB_RESOURCES_SIZE, SB_RESOURCES_SIZE, null);
            g.drawImage(ImageRepository.getInstance().getResourceImage(Resource.SHIELD), SB_RESOURCES_X2, SB_RESOURCES_Y1, SB_RESOURCES_SIZE, SB_RESOURCES_SIZE, null);
            g.drawImage(ImageRepository.getInstance().getResourceImage(Resource.SERVANT), SB_RESOURCES_X2, SB_RESOURCES_Y2, SB_RESOURCES_SIZE, SB_RESOURCES_SIZE, null);

            //draw warehouseDepot.... different from myBoard (resources are Jbuttons) and other players boards (resources as Images)
            for (int i = 0; i < wd_layer1.getAmount(); i++) {
                g.drawImage(ImageRepository.getInstance().getResourceImage(wd_layer1.getResourceType()), WD_RESOURCES_X1 + (i * 35), WD_RESOURCES_Y1, WD_RESOURCES_SIZE, WD_RESOURCES_SIZE, null);
            }
            for (int i = 0; i < wd_layer2.getAmount(); i++) {
                g.drawImage(ImageRepository.getInstance().getResourceImage(wd_layer2.getResourceType()), WD_RESOURCES_X2 + (i * 35), WD_RESOURCES_Y2, WD_RESOURCES_SIZE, WD_RESOURCES_SIZE, null);
            }
            if (wd_layer3.getAmount() != 0)
                g.drawImage(ImageRepository.getInstance().getResourceImage(wd_layer3.getResourceType()), WD_RESOURCES_X3, WD_RESOURCES_Y3, WD_RESOURCES_SIZE, WD_RESOURCES_SIZE, null);


            //draw the pope tiles
            for (int i = 0; i < GameConstants.FAITH_TRACK_NUMBER_OF_POPE_TILES; i++) {
                if (myPopeTiles[i]) {
                    g.drawImage(ImageRepository.getInstance().getVaticanReportSignal(i + 1, true), POPETILE_X[i], POPETILE_Y[i], POPETILE_SIZE, POPETILE_SIZE, null);
                } else {
                    g.drawImage(ImageRepository.getInstance().getVaticanReportSignal(i + 1, false), POPETILE_X[i], POPETILE_Y[i], POPETILE_SIZE, POPETILE_SIZE, null);
                }
            }

            //Draw Inactive DevCards
            int counter = 0;
            List<Stack<Integer>> devCardsStacksOrNULL = Optional.ofNullable(devCardsStacks).orElse(Collections.emptyList());
            for (Stack<Integer> stack : devCardsStacksOrNULL) {
                if (stack != null && !stack.empty() && stack.size() > 1) {
                    for (int i = 0; i < stack.size()-1; i++) {
                        Integer cardNumber = stack.elementAt(i);
                        g.drawImage(ImageRepository.getInstance().getCardImage(cardNumber), DEV_CARD_POS_X[counter], DEV_CARD_POS_Y + ((stack.size()-1 - i) * DEV_CARD_SHIFT), DEV_CARD_SIZE_X, devCardSizeY, null);
                    }
                }
                counter++;
            }

            //draw last picked action token
            if (lastPickedActionToken != null) {
                g.drawRoundRect(ACTION_TOKEN_POS_X - 10, ACTION_TOKEN_POS_Y - 5, 300, 80, 10, 10);
                g.drawImage(ImageRepository.getInstance().getActionTokenImage(lastPickedActionToken), ACTION_TOKEN_POS_X, ACTION_TOKEN_POS_Y, ACTION_TOKEN_SIZE, ACTION_TOKEN_SIZE, null);
                g.drawString("is the last picked action token", ACTION_TOKEN_POS_X + 85, ACTION_TOKEN_POS_Y + 40);
            }
        }
    }



//------------------------------------------UPDATE VIEW------------------------------------------------------------------------

    /**
     * Update view.
     *
     * @param compressedModel the compressed model
     */
    public void updateView(CompressedModel compressedModel){
        //do not show jbuttons when player is disconnected
        myplayerIsDisconnected= compressedModel.getDisconnectedPlayers().contains(username);

        resourcesInLeaderCardFiller(compressedModel);

        //It works only in single player
        if(compressedModel.getaP() == compressedModel.getPlayerNames().indexOf(username) && compressedModel.getTurnState() != TurnState.SETUP_PLAYER_CHOICES){
            lastPickedActionTokenFiller(compressedModel);
        }
        leaderCardsFillerAndDrawer(compressedModel);
        wareHouseDepotFiller(compressedModel);
        if(!myplayerIsDisconnected){
            devCardsFillerAndDrawer(compressedModel);
        }
        faithTrackFiller(compressedModel);
        popeTilesFiller(compressedModel);
        strongBoxFiller(compressedModel);
        backgroundChangerFiller(compressedModel);
    }

    //----------------------------------------------- OTHER METHODS -----------------------------------------------------------
    /**
     * Initialise StrongBox buttons creating new GameButtons and setting their value to zero
     */
    private void initializeStrongBoxButtons() {

        button_coin_strongBox=new JButton(((Integer) coin_sb).toString());
        button_servant_strongBox=new JButton(((Integer) servant_sb).toString());
        button_shield_strongBox=new JButton(((Integer) shield_sb).toString());
        button_stone_strongBox=new JButton(((Integer) stone_sb).toString());

        //set background
        button_coin_strongBox.setBackground(Color.LIGHT_GRAY);button_servant_strongBox.setBackground(Color.LIGHT_GRAY);button_shield_strongBox.setBackground(Color.LIGHT_GRAY);button_stone_strongBox.setBackground(Color.LIGHT_GRAY);

        //SET FONT
        Font myFont= new Font ("Arial",Font.BOLD,14);
        button_coin_strongBox.setFont(myFont);button_servant_strongBox.setFont(myFont);button_shield_strongBox.setFont(myFont);button_stone_strongBox.setFont(myFont);

        //set margins
        button_servant_strongBox.setMargin(new Insets(0,0,0,0));
        button_coin_strongBox.setMargin(new Insets(0,0,0,0));
        button_shield_strongBox.setMargin(new Insets(0,0,0,0));
        button_stone_strongBox.setMargin(new Insets(0,0,0,0));

        //set position + size
        button_coin_strongBox.setBounds(SB_RESOURCES_X1+32,SB_RESOURCES_Y1,45,33);
        button_stone_strongBox.setBounds(SB_RESOURCES_X1+32,SB_RESOURCES_Y2,45,33);
        button_shield_strongBox.setBounds(SB_RESOURCES_X2+32,SB_RESOURCES_Y1,45,33);
        button_servant_strongBox.setBounds(SB_RESOURCES_X2+32,SB_RESOURCES_Y2,45,33);

        if(!myplayerIsDisconnected) {
            button_coin_strongBox.setVisible(true);
            button_stone_strongBox.setVisible(true);
            button_shield_strongBox.setVisible(true);
            button_servant_strongBox.setVisible(true);
        }
        else{
            button_coin_strongBox.setVisible(false);
            button_stone_strongBox.setVisible(false);
            button_shield_strongBox.setVisible(false);
            button_servant_strongBox.setVisible(false);
        }

        //DISABLE buttons mouse listeners
        blockSBListenersOnlyIfOtherPlayer(button_stone_strongBox);
        blockSBListenersOnlyIfOtherPlayer(button_servant_strongBox);
        blockSBListenersOnlyIfOtherPlayer(button_coin_strongBox);
        blockSBListenersOnlyIfOtherPlayer(button_shield_strongBox);

        //add them to personal board
        add(button_coin_strongBox);
        add(button_servant_strongBox);
        add(button_stone_strongBox);
        add(button_shield_strongBox);

    }

    /**
     * Block strongBox listeners only if this Player board is an otherPlayerBoard.
     *
     * @param button the button
     */
    protected void blockSBListenersOnlyIfOtherPlayer(JButton button) {
        for(MouseListener mouseListener: button.getMouseListeners())button.removeMouseListener(mouseListener);
    }


    //-------------------------------------------GETTERS FOR THE SUBCLASSES---------------------------------------------

    /**
     * Gets leader card back.
     *
     * @return the leader card back
     */
    public Image getLeaderCardBack() {
        return leaderCardBack;
    }

    /**
     * Get leader card pos y int [ ].
     *
     * @return the int [ ]
     */
    public static int[] getLeaderCardPosY() {
        return LEADER_CARD_POS_Y;
    }

    /**
     * Gets leader cards pos x.
     *
     * @return the leader cards pos x
     */
    public static int getLeaderCardsPosX() {
        return LEADER_CARDS_POS_X;
    }

    /**
     * Gets leader card size x.
     *
     * @return the leader card size x
     */
    public static int getLeaderCardSizeX() {
        return LEADER_CARD_SIZE_X;
    }

    /**
     * Gets dev cards stacks.
     *
     * @return the dev cards stacks
     */
    public List<Stack<Integer>> getDevCardsStacks() {
        return devCardsStacks;
    }

    /**
     * Gets dev card size x.
     *
     * @return the dev card size x
     */
    public static int getDevCardSizeX() {
        return DEV_CARD_SIZE_X;
    }

    /**
     * Get dev card pos x int [ ].
     *
     * @return the int [ ]
     */
    public static int[] getDevCardPosX() {
        return DEV_CARD_POS_X;
    }

    /**
     * Gets dev card pos y.
     *
     * @return the dev card pos y
     */
    public static int getDevCardPosY() {
        return DEV_CARD_POS_Y;
    }

    /**
     * Gets dev card shift.
     *
     * @return the dev card shift
     */
    public static int getDevCardShift() {
        return DEV_CARD_SHIFT;
    }

    /**
     * Gets wd layer 1.
     *
     * @return the wd layer 1
     */
    public ResourceStack getWd_layer1() {
        return wd_layer1;
    }

    /**
     * Gets wd layer 2.
     *
     * @return the wd layer 2
     */
    public ResourceStack getWd_layer2() {
        return wd_layer2;
    }

    /**
     * Gets wd layer 3.
     *
     * @return the wd layer 3
     */
    public ResourceStack getWd_layer3() {
        return wd_layer3;
    }

    /**
     * Gets strongbox.
     *
     * @return the strongbox
     */
    public List<ResourceStack> getStrongbox() {
        return strongbox;
    }

    /**
     * Gets resources in leader card storage.
     *
     * @return the resources in leader card storage
     */
    public List<ResourceStack> getResourcesInLeaderCardStorage() {
        return resourcesInLeaderCardStorage;
    }

    /**
     * Returns the username of the player who has this playerBoard
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }


    /**
     * Gets leader card resource x position.
     *
     * @return the leader card resource position(X)
     */
    public static int getLeaderCardResourceX() {
        return LEADER_CARD_RESOURCE_X;
    }

    /**
     * Gets leader card resource y position.
     *
     * @return the leader card resource position(Y)
     */
    public static int[] getLeaderCardResourceY() {
        return LEADER_CARD_RESOURCE_Y;
    }

    /**
     * Gets leader card resource size.
     *
     * @return the leader card resource size
     */
    public static int getLeaderCardResourceSize() {
        return LEADER_CARD_RESOURCE_SIZE;
    }

    /**
     * Returns all the active leader cards of this playerboard
     * @return list of all the active leader cards (id)
     */
    public List<Integer> getAllMyActiveLeaderCards() {
        return allMyActiveLeaderCards;
    }

    /**
     * Returns all the inactive leader cards of this player
     * @return list of all the inactive leader cards (id)
     */
    public List<Integer> getAllMyInactiveLeaderCards() {
        return allMyInactiveLeaderCards;
    }
}
