package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.model.Card;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.gamemanager.MVCModel;
import it.polimi.ingsw.server.model.gamemanager.Player;
import it.polimi.ingsw.server.model.gameplay.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static java.lang.Math.floor;

/**
 * The type Game mode.
 */
public abstract class GameMode {

    private MVCModel model;
    private TransactionController transactionController;


    /**
     * Instantiates a new Game mode.
     */
    public GameMode() {
        model = new MVCModel();
        this.transactionController = new TransactionController(this);
    }

    /**
     * Returns the model reference for this controller
     *
     * @return model object
     */
    public MVCModel getModel() {
        return model;
    }

    /**
     * Returns the transaction controller reference for this controller
     *
     * @return transaction controller object
     */
    public TransactionController getTransactionController() { return transactionController;}

    /**
     * Sets game.
     */
    public abstract void setupGame();

    /**
     * Calculates score for each player and update the model scoreboard.
     */
    public abstract void calculateScores();

    /**
     * Try advance and manage exceptions.
     *
     * @param player the player
     * @param amount the amount
     */
    public abstract void tryAdvance(Player player, int amount);

    /**
     * Vatican report.
     *
     * @param playerActivatingVR the player activating vatican Report
     * @param index              the index of vatican report between 1 and 3
     */
    public abstract void vaticanReport(int playerActivatingVR,int index);

    /**
     * Add player to session.
     *
     * @param player the player
     */
    public void addPlayerToSession(Player player){
        try {
            model.getGameSession().addPlayer(player);
        } catch (PlayerCountOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    /**
     * Prepare dev card grid.
     */
    protected void prepareDevCardGrid(){

        Deck<DevelopmentCard> devCards = CardRepository.getInstance().getAllDevelopmentCards();
        DevelopmentCardGrid grid = model.getGameSession().getDevelopmentCardGrid();
        ArrayList<Triplet<Deck<DevelopmentCard>, BannerColor, Integer>> deckList= new ArrayList<>();

        //create empty decks array
        //NB: Decks' order is given by BannerColor.values' order, makes sure to leave it as it is
        for(int level=1; level<=3; level++){
            for(BannerColor color : BannerColor.values()){
                deckList.add(new Triplet<>(new Deck<>(), color, level));
            }
        }

        //fill decks with cards and shuffle
        devCards.shuffle();
        for(DevelopmentCard devCard : devCards){
            for(Triplet<Deck<DevelopmentCard>, BannerColor, Integer> deckType : deckList){
                if(devCard.getBannerColor()==deckType.getSecond() && devCard.getCardLevel()==deckType.getThird()){
                    deckType.getFirst().addElement(devCard);
                }
            }
        }

        int deckNumber = 0;
        for(int level=1; level<=3; level++){
            int colorOrder = 1;
            for(BannerColor color : BannerColor.values()){
                grid.placeCardDeck(deckList.get(deckNumber).getFirst(), level-1, colorOrder-1);
                colorOrder++;
                deckNumber++;
            }
        }
    }

    /**
     * Distribute leader cards to each player.
     */
    protected void dealLeaderCard(){
        Deck<LeaderCard> leaderCardDeck= CardRepository.getInstance().getAllLeaderCards();

        leaderCardDeck.shuffle();

        for(int i=0;i<model.getGameSession().getPlayerList().size();i++){
            LeaderCardSpace leaderCardSpace=model.getGameSession().getPlayerByIndex(i).getBoard().getLeaderCardSpace();
            leaderCardSpace.addCard(leaderCardDeck.removeElement());
            leaderCardSpace.addCard(leaderCardDeck.removeElement());
            leaderCardSpace.addCard(leaderCardDeck.removeElement());
            leaderCardSpace.addCard(leaderCardDeck.removeElement());
        }
    }

    /**
     * Prepare the personal boards of the players by setting the maximum capacity of the layers in the warehouse depot
     */
    protected void preparePersonalBoards() {
        //strongbox is already set

        //set the maximum capacity of the different layers in the warehousedepot
        for(int i=0; i<model.getGameSession().getPlayerList().size();i++){
            model.getGameSession().getPlayerByIndex(i).getBoard().getWarehouseDepot().SetLayerMaxAmount();
        }

        //the leader card space contains already the 4 cards from which the player has to choose
        //the development card space is set by the constructor that initializes
        //faith track already set(without the initial resources to ask to the player)
    }

    /**
     * Method to invoke once asked to the player which of his cards wants to choose
     *
     * @param playerIndex index of the player that chooses the cards
     * @param leaderCard1 first chosen leader card
     * @param leaderCard2 second chosen leader card
     */
    protected void chooseLeaderCards(int playerIndex, LeaderCard leaderCard1, LeaderCard leaderCard2){
        LeaderCardSpace leaderCardSpace=model.getGameSession().getPlayerByIndex(playerIndex).getBoard().getLeaderCardSpace();
        leaderCardSpace.setCardStatus(leaderCard1,LeaderCardStatus.CHOSEN);
        leaderCardSpace.setCardStatus(leaderCard2,LeaderCardStatus.CHOSEN);

    }


    /**
     * Count all victory points for player int.
     *
     * @param playerIndex the player index, from 0 to n-1
     * @return the victory points achieved by the player
     */
    protected int countAllVictoryPointsForPlayer(int playerIndex){
        int totalPoints;
        Player player = model.getGameSession().getPlayerByIndex(playerIndex);

        //resources count
        double resourceNumber = 0;
        for(Resource res : Resource.values()){
            if( res != Resource.FAITH && res != Resource.GENERIC &&  res != Resource.WHITE){
                resourceNumber = resourceNumber + player.getBoard().getTotalStoredAmount(res);
            }
        }
        totalPoints = (int) floor((resourceNumber/5));


        //papal points
        totalPoints = totalPoints + player.getBoard().getFaithTrack().getTotalPapalPoints();

        //development cards points
        totalPoints = totalPoints + player.getBoard().getDevelopmentCardSpace().getTotalStackVictoryPoints();


        //leader cards points
        for (Card card : player.getBoard().getLeaderCardSpace().getActiveCards()){
            totalPoints = totalPoints + card.getVictoryPoints();
        }

        return totalPoints;
    }

    /**
     * Discard leader card for faith points.
     *
     * @param leaderCard the leader card
     * @throws ImpossibleLeaderCardActionException the impossible leader card action
     * @throws FaithTrackOutOfBoundsException      the faith track out of bounds exception
     */
    protected void discardLeaderCardForFaithPoints(LeaderCard leaderCard) throws ImpossibleLeaderCardActionException, FaithTrackOutOfBoundsException{

        //Select Chosen - Active Cards
        Player activePlayer = model.getGameSession().getActivePlayer();
        Set<LeaderCard> discernibleCards= activePlayer.getBoard().getLeaderCardSpace().getChosenCards();
        for (LeaderCard card : activePlayer.getBoard().getLeaderCardSpace().getActiveCards()){
            discernibleCards.remove(card);
        }

        //If the player doesn't own the leaderCard
        if(!discernibleCards.contains(leaderCard)){
            throw new ImpossibleLeaderCardActionException();
        }

        //Change card status and advance in faith track
        activePlayer.getBoard().getLeaderCardSpace().setCardStatus(leaderCard, LeaderCardStatus.DISCARDED);
        tryAdvance(activePlayer,1);
    }


    /**
     * Activate leader card.
     *
     * @param leaderCard the leader card
     * @throws ImpossibleLeaderCardActionException the impossible leader card action
     * @throws InsufficientResourcesException      the insufficient resources exception
     */
    protected void activateLeaderCard(LeaderCard leaderCard) throws ImpossibleLeaderCardActionException, InsufficientResourcesException {

        //Select Chosen - Active Cards
        Player activePlayer = model.getGameSession().getActivePlayer();
        Set<LeaderCard> discernibleCards= activePlayer.getBoard().getLeaderCardSpace().getChosenCards();
        for (LeaderCard card : activePlayer.getBoard().getLeaderCardSpace().getActiveCards()){
            discernibleCards.remove(card);
        }

        //If the player doesn't own the leaderCard
        if(!discernibleCards.contains(leaderCard)){
            throw new ImpossibleLeaderCardActionException();
        }

        //Check if player has rights to activate leaderCard
        if(leaderCard.getRequiredResources().size() > 0){
            for(ResourceStack res : leaderCard.getRequiredResources()){
                if(activePlayer.getBoard().getTotalStoredAmount(res.getResourceType()) < res.getAmount()){
                    throw new InsufficientResourcesException();
                }
            }
        }

        if(leaderCard.getBannerRequirements().size() > 0){
            Deck<DevelopmentCard> deck1 = activePlayer.getBoard().getDevelopmentCardSpace().getDevelopmentCardDeck(1);
            Deck<DevelopmentCard> deck2 = activePlayer.getBoard().getDevelopmentCardSpace().getDevelopmentCardDeck(2);
            Deck<DevelopmentCard> deck3 = activePlayer.getBoard().getDevelopmentCardSpace().getDevelopmentCardDeck(3);

            for(Triplet<BannerColor, Integer, Integer> trip: leaderCard.getBannerRequirements()){
                int count = 0;
                BannerColor color = trip.getFirst();
                int level = trip.getSecond();
                int amount = trip.getThird();

                for(DevelopmentCard card : deck1){
                    if(card.getBannerColor() == color && card.getCardLevel() >= level){
                        count++;
                    }
                }
                for(DevelopmentCard card : deck2){
                    if(card.getBannerColor() == color && card.getCardLevel() >= level){
                        count++;
                    }
                }
                for(DevelopmentCard card : deck3){
                    if(card.getBannerColor() == color && card.getCardLevel() >= level){
                        count++;
                    }
                }
                if(count < amount){
                    throw new InsufficientResourcesException();
                }
            }

        }

        //Activation
        activePlayer.getBoard().getLeaderCardSpace().setCardStatus(leaderCard, LeaderCardStatus.IN_USE);

    }

    /**
     * Returns a list of all the development cards purchasable by the active player
     *
     * @return list of cads purchasable by the active player
     */
    @Deprecated
    protected List<DevelopmentCard> getAllPurchasableCards(){

        boolean lv1=false;
        boolean lv2=false;
        boolean lv3=false;
        DevelopmentCardSpace d=model.getGameSession().getActivePlayer().getBoard().getDevelopmentCardSpace();

        //check the level of the last card in each space in the development card space
        for(int i = 1; i<= GameConstants.DEV_CARD_NUMBER_OF_SPACES; i++) {
            if(d.getDevelopmentCardDeck(i).getSize()!=0) {
                switch (d.getHighestCard(i).getCardLevel()) {
                    case 1:
                        lv2 = true;
                        break;
                    case 2:
                        lv3 = true;
                        break;
                    default:
                        break;
                }
            }
            else{
                lv1=true;
            }
        }

        //get the list of the purchasable development cards by level
        List<DevelopmentCard> developmentCardList=model.getGameSession().getDevelopmentCardGrid().getPurchasableCardsByLevel(lv1,lv2,lv3);

        //eliminate cards for which you do not have enough resources
        for(DevelopmentCard developmentCard:developmentCardList){
            for(ResourceStack resourceStack:developmentCard.getRequiredResources()){
                if(model.getGameSession().getActivePlayer().getBoard().getTotalStoredAmount(resourceStack.getResourceType())<resourceStack.getAmount())
                    developmentCardList.remove(developmentCard);
            }
        }

        return developmentCardList;
    }

    /**
     * Given a row in the market, returns all the resources taken from that row without the white and red ones
     *
     * @param row                row chosen by the player
     * @param whiteMarbleChoice  the white marble choice
     * @param resourcesToDiscard the resources to discard
     * @throws ResourceNotInsertableException the resource not insertable exception
     */
    protected void selectMarketRow(int row, HashMap<Resource, Integer> whiteMarbleChoice, List<Resource> resourcesToDiscard) throws ResourceNotInsertableException {
        List<Resource> resourceList=model.getGameSession().getMarketBoard().getRow(row);


        //remove resources that have to be discarded
        for(Resource r:resourcesToDiscard){
            resourceList.remove(r);
        }

        //eliminate white and red marbles
        handleRedAndWhiteMarbles(resourceList,whiteMarbleChoice);

        //moved here so that the market can be updated only if the action is a success
        model.getGameSession().getMarketBoard().insertHorizontal(row);

    }

    /**
     * Given a column in the market, returns all the resources taken from that column that can not fit in the deposits
     *
     * @param column             column of the market chosen by the player
     * @param whiteMarbleChoice  the white marble choice
     * @param resourcesToDiscard the resources to discard
     * @throws ResourceNotInsertableException the resource is not insertable exception
     */
    protected void selectMarketColumn(int column,HashMap<Resource, Integer> whiteMarbleChoice, List<Resource> resourcesToDiscard) throws ResourceNotInsertableException {
        List<Resource> resourceList=model.getGameSession().getMarketBoard().getColumn(column);

        //remove resources that have to be discarded
        for(Resource r:resourcesToDiscard){
            resourceList.remove(r);
        }

        //eliminate white and red marbles
        handleRedAndWhiteMarbles(resourceList,whiteMarbleChoice);
        //moved here so that the market can be updated only if the action is a success
        model.getGameSession().getMarketBoard().insertVertical(column);

    }

    /**
     * Handle the white and red marbles taken at the  market, add faith points for the red ones and eliminate the white
     * ones if no leader card with a special power( of white marble substitution) is active
     *
     * @param resourceList      list of resources taken at the market, with the red and white ones
     * @param whiteMarbleChoice the white marble choice
     * @throws ResourceNotInsertableException the resource not insertable exception
     */
    protected void handleRedAndWhiteMarbles(List<Resource> resourceList,HashMap<Resource, Integer> whiteMarbleChoice) throws ResourceNotInsertableException {
        int numActiveLeaderCardWhiteMarble=0;
        Resource substitute=null;
        Resource substitute2=null;
        HashMap<Resource,Integer> copywhiteMarbleChoice=new HashMap<>();
        if(whiteMarbleChoice!=null){ copywhiteMarbleChoice=new HashMap<>(whiteMarbleChoice);}

        //check the number of active leader cards with the power "white_marble_substitution"
        // and insert in in the local variables substitute and substitute2 the substitutes for the white marbles
        for(LeaderCard leaderCard:model.getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards()){
            if(leaderCard.getPower().getSpecialAbilityType()==SpecialAbility.WHITE_MARBLE_SUBSTITUTION){
                numActiveLeaderCardWhiteMarble++;
                if(numActiveLeaderCardWhiteMarble==1)substitute= ((AbilityPower) leaderCard.getPower()).getResourceType();
                if(numActiveLeaderCardWhiteMarble==2)substitute2= ((AbilityPower) leaderCard.getPower()).getResourceType();
            }
        }


        //for each resource in the resourceList...
        int countFaithToRemove=0;
        int countWhiteToRemove=0;
        int countSubstitute1toadd=0;
        int countSubstitute2toadd=0;
        for(Resource r: resourceList){

            //if it is faith, remove it and add the corresponding faith points
            if(r==Resource.FAITH){
                countFaithToRemove++;
                tryAdvance(model.getGameSession().getActivePlayer(),1);
            }


            //if it is white, remove it if there are no active leader cards with the power 'white_marble_substitution'
            //replace it with substitute if there is 1 active leader card with the power 'white_marble_substitution'
            //replace it witht the correct number of 'substitute' and the correct number of 'substitute2' if there are
            // 2 active leader cards with the power 'white_marble_substitution'
            else if(r==Resource.WHITE){
                if(numActiveLeaderCardWhiteMarble==0){countWhiteToRemove++;}
                else if(numActiveLeaderCardWhiteMarble==1){
                    if((!copywhiteMarbleChoice.isEmpty())&&(countSubstitute1toadd<copywhiteMarbleChoice.get(substitute))){countWhiteToRemove++;countSubstitute1toadd++;}
                    else{countWhiteToRemove++;}
                }
                else {
                    countWhiteToRemove++;
                    if(copywhiteMarbleChoice.get(substitute)>0){
                        copywhiteMarbleChoice.put(substitute,copywhiteMarbleChoice.get(substitute)-1);
                        countSubstitute1toadd++;
                    }
                    else {
                        copywhiteMarbleChoice.put(substitute2,copywhiteMarbleChoice.get(substitute2)-1);
                        countSubstitute2toadd++;
                    }
                }
            }
        }
        for(int i=0;i<countFaithToRemove;i++){resourceList.remove(Resource.FAITH);}
        for(int i=0;i<countWhiteToRemove;i++){resourceList.remove(Resource.WHITE);}
        for(int i=0;i<countSubstitute1toadd;i++){resourceList.add(substitute);}
        for(int i=0;i<countSubstitute2toadd;i++){resourceList.add(substitute2);}

        int activeplayerindex=model.getGameSession().getPlayerList().indexOf(model.getGameSession().getActivePlayer());
        //insert the resources in the deposits
        try{insertResourcesInDeposits(activeplayerindex,resourceList);}
        catch(ResourceNotInsertableException e){
            for(int i=0;i<countFaithToRemove;i++)tryAdvance(model.getGameSession().getActivePlayer(),-1);
            throw new ResourceNotInsertableException();
        }
    }

    /**
     * Insert a list of resources in the different deposits(only WAREHOUSEDEPOT+ADDITIONAL STORAGES), if they can not
     * fit the state is changed to MARKET_DISCARD_RESOURCE
     *
     * @param playerindex  the playerindex
     * @param resourceListParameter the resources to insert in the deposits(no white/red ones)
     * @throws ResourceNotInsertableException the resource not insertable exception
     */
    protected void insertResourcesInDeposits(int playerindex,List<Resource> resourceListParameter) throws ResourceNotInsertableException {
        WarehouseDepot warehouseDepot=model.getGameSession().getPlayerByIndex(playerindex).getBoard().getWarehouseDepot();
        List<Resource> resourcesInExcess=new ArrayList<>();
        List<Resource> resourcesInsertedWD=new ArrayList<>();
        List<Pair<LeaderCard,Resource>> resourcesInsertedInLeaderCards=new ArrayList<>();
        List<Resource> resourceList=new ArrayList<>(resourceListParameter);
        Set<Pair<LeaderCard,ResourceStack>> activeStorageCards=model.getGameSession().getPlayerByIndex(playerindex).getBoard().getLeaderCardSpace().getActiveStorageCards();

        //reorder the list of resources to insert, so that the resources that have active leaderCard of the same type
        //are placed at the end of that list
        if(!activeStorageCards.isEmpty()){
            for(Resource r: resourceListParameter){
                for(Pair<LeaderCard,ResourceStack> pair:activeStorageCards){
                    if(pair.getSecond().getResourceType()==r){
                        resourceList.remove(r);
                        resourceList.add(r);
                    }
                }
            }
        }

        //for each resource, place it in the warehouse depot if possible, if not add the resource to resourceInExcess
        for(Resource r:resourceList){
            try{warehouseDepot.InsertResource(r);resourcesInsertedWD.add(r);}
            catch(ResourceNotInsertableException e){resourcesInExcess.add(r);}
        }

        //check if there are places in the additional storages of the active leader cards
        int numberofResourcesInsertedInLeaderCards=0;
        if(!activeStorageCards.isEmpty()){
            for(Resource r: resourcesInExcess){
                for(Pair<LeaderCard,ResourceStack> pair:activeStorageCards){
                    if((pair.getSecond().getResourceType()==r)&&(pair.getSecond().getAmount()<pair.getSecond().getMaxSize())){
                        try{
                            model.getGameSession().getPlayerByIndex(playerindex).getBoard().getLeaderCardSpace().setResourcesIntoCard(pair.getFirst(),1);
                            Pair<LeaderCard,Resource> leaderCardResourcePair=new Pair<>(pair.getFirst(), r);
                            resourcesInsertedInLeaderCards.add(leaderCardResourcePair);
                        }
                        catch(FullResourceStackException e){e.printStackTrace();}
                        numberofResourcesInsertedInLeaderCards++;
                    }
                }
            }
        }
        //Delete some resources, just to know the number of resources that is not possible to insert in the leaderCards
        int resourcesInExcessSize=resourcesInExcess.size()-numberofResourcesInsertedInLeaderCards;

        //SINCE WE ALREADY DISCARDED THE RESOURCES THAT COULD NOT FIT IN THE DEPOSITS, RESOURCES-IN-EXCESS SHOULD BE EMPTY
        //if it's not empty, throw an exception and the TurnState should catch it and set the state to REJECTED
        if(resourcesInExcessSize>0){
            //remove the resources just inserted into the WD because the transaction is 'rejected'
            for(Resource r:resourcesInsertedWD){
                for(int i=1;i<=GameConstants.WAREHOUSE_DEPOT_NUMBER_OF_LAYERS;i++){
                    if(warehouseDepot.getLayerType(i)==r){
                        try{warehouseDepot.setLayerAmount(i,warehouseDepot.getLayerAmount(i)-1);}
                        catch(FullResourceStackException e){
                            //this exception should never be thrown because we have already inserted these resources that we are removing
                            e.printStackTrace();
                        }
                    }
                }
            }

            //remove the resources just inserted into the leader card storage because the transaction is 'rejected'
            for(Pair<LeaderCard,Resource> pair: resourcesInsertedInLeaderCards){
                for(Pair<LeaderCard,ResourceStack> p:activeStorageCards){
                    if(p.getFirst()==pair.getFirst()){
                        ResourceStack resourceStack=p.getSecond();
                        try{resourceStack.setAmount(resourceStack.getAmount()-1);}
                        catch(FullResourceStackException e){
                            //this exception should never be thrown because we have already inserted these resources that we are removing
                            e.printStackTrace();
                        }
                        p.setSecond(resourceStack);
                    }
                }
            }


            throw new ResourceNotInsertableException();

        }

    }

    /**
     * Only needed in multiplayer mode, but defined here to avoid instanceof usage
     *
     * @param player           the player who want to receive his initial resources
     * @param initialResources the initial resources
     */
    protected abstract void distributeInitialResources(int player, List<Resource> initialResources);

}
