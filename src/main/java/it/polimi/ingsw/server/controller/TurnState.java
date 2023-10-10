package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.gamemanager.GameSession;
import it.polimi.ingsw.server.model.gameplay.DevelopmentCardSpace;
import it.polimi.ingsw.server.model.gameplay.PersonalBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Associates a controller action to the state of the player's turn
 * (this class is symmetrical to model\TurnState)
 * @see it.polimi.ingsw.common.model.TurnState
 */
public enum TurnState
{

    /**
     * Sets up the player's initial choice of resources and leader cards
     */
    SETUP_PLAYER_CHOICES((event, gameMode) ->{
        GameSession gameSession = gameMode.getModel().getGameSession();
        PersonalBoard personalBoard = gameSession.getActivePlayer().getBoard();

        //Setup initial resources
        List<Resource> requestedResources = event.getInputResources() != null ? event.getInputResources().stream().map(Pair::getFirst).collect(Collectors.toList()) : new ArrayList<>();
        int playerIndex = gameSession.getPlayerIndex(gameSession.getActivePlayer());
        List<LeaderCard> chosenLeaderCards = personalBoard.getLeaderCardSpace().getAllCards().stream()
                .filter(card -> event.getLeaderCards().contains(card.getCardID())).collect(Collectors.toList());

        //If the PlayerEvent format is wrong
        if(requestedResources.size() != GameConstants.INITIAL_RESOURCES_AMOUNT_FOR_PLAYER[playerIndex] ||
                chosenLeaderCards.size() != GameConstants.LEADER_CARDS_CHOICE_AMOUNT)
        {
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
            return;
        }

        //Will only do anything in multiplayer mode
        gameMode.distributeInitialResources(playerIndex, requestedResources);
        gameMode.chooseLeaderCards(playerIndex, chosenLeaderCards.get(0), chosenLeaderCards.get(1));
        gameMode.getModel().getGameSession().setPlayerChoosePhaseAsDone(playerIndex);
        gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
    }),
    /**
     * Handles a possible initial leader action
     */
    INITIAL_LEADER_ACTION((event, gameMode) ->{
        PersonalBoard personalBoard = gameMode.getModel().getGameSession().getActivePlayer().getBoard();

        //The player chose not to activate or discard any leader card
        if(event.getLeaderCards() == null || event.getLeaderCards().size() == 0)
        {
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
            return;
        }

        //Check what to activate and what to discard
        List<LeaderCard> cardsToActivate = null, cardsToDiscard = null;
        if(event.getHasDiscarded())
        {
            if(event.getDiscardedList() == null || event.getDiscardedList().size() == 0)
            {
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
                return;
            }
            cardsToDiscard = event.getDiscardedList().stream().map(CardRepository.getInstance()::getLeaderCardByID).collect(Collectors.toList());
            cardsToActivate = event.getLeaderCards().stream().filter(id -> !event.getDiscardedList().contains(id))
                    .map(CardRepository.getInstance()::getLeaderCardByID).collect(Collectors.toList());
        }
        else
        {
            cardsToActivate = event.getLeaderCards().stream().map(CardRepository.getInstance()::getLeaderCardByID).collect(Collectors.toList());
        }

        //Check if all cards in requested actions are owned by the player
        List<LeaderCard> allSelected = new ArrayList<>();
        allSelected.addAll(cardsToActivate);
        if(cardsToDiscard != null)
            allSelected.addAll(cardsToDiscard);

        List<LeaderCard> ownedSelected = allSelected.stream()
                .filter(card -> personalBoard.getLeaderCardSpace().getChosenCards().contains(card)).collect(Collectors.toList());

        if(ownedSelected.size() != allSelected.size())
        {
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
            return;
        }

        //Try discarding if necessary
        if(event.getHasDiscarded())
        {
            try {
                for (LeaderCard card : cardsToDiscard)
                    gameMode.discardLeaderCardForFaithPoints(card);
            } catch (ImpossibleLeaderCardActionException | FaithTrackOutOfBoundsException e) {
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
                return;
            }
        }

        //Activate the cards or, if that is not allowed, reject
        try
        {
            for(LeaderCard card : cardsToActivate)
                gameMode.activateLeaderCard(card);
        }
        catch (InsufficientResourcesException | ImpossibleLeaderCardActionException e)
        {
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
            return;
        }

        gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
    }),
    /**
     * Here, the player has chosen an action (market, dev card purchase or production)
     */
    CHOOSE_ACTION((event, gameMode) ->{
        //Nothing can be done here
    }),
    /**
     * The player has Disconnected from the server
     */
    DISCONNECTION((event, gameMode) ->{
        gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
    }),
    /**
     * The player has requested a specific action on the market board
     */
    MARKET((event, gameMode) ->{
        //If the list is null create an empty one
        List<Resource> resourcesToDiscard = (event.getOutputResources() == null ? new ArrayList<>() : event.getOutputResources());


        //Check the row and col values are realistic
        if(event.getRow() >= GameConstants.MARKET_ROWS_COUNT || event.getCol() >= GameConstants.MARKET_COLS_COUNT ||
                /* One of the two ought to be < 0 and the other between 0 and their respective Max value */
                (event.getCol() >= 0 && event.getRow() >= 0) || (event.getCol() < 0 && event.getRow() < 0))
        {
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
            return;
        }

        //Did the player select the row...
        if(event.getRow() >= 0)
        {
            try{gameMode.selectMarketRow(event.getRow(), event.getWhiteMarbleSubstitution(), resourcesToDiscard);
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);}
            catch(ResourceNotInsertableException e){gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);}
        }
        //Or the column
        else if (event.getCol() >= 0)
        {
            try{gameMode.selectMarketColumn(event.getCol(), event.getWhiteMarbleSubstitution(), resourcesToDiscard);
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);}
            catch(ResourceNotInsertableException e){gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);}
        }
    }),
    /**
     * The player has requested to purchase a development card,
     * the package to use contains the DevCardID, row, col, slotIntheDevCardSpace(saved in event.index)
     * if the transaction fails, tbe PlayerMoveResponse is set to Rejected
     */
    DEV_CARD_PURCHASE((event, gameMode) ->{
        GameSession gameSession=gameMode.getModel().getGameSession();
        PersonalBoard personalBoard=gameSession.getActivePlayer().getBoard();
        //find the card in the repository with the same id as the one coming from the view, the card should exists otherwise the transaction is rejected
        DevelopmentCard card=gameMode.getModel().getGameSession().getDevelopmentCardGrid().pickCard(event.getRow(), event.getCol());

        //check if he can place the card
        boolean isSpaceFound = false;
        switch (card.getCardLevel()){
            case 1:
                for(int i = 1; i<=3; i++){
                    if(personalBoard.getDevelopmentCardSpace().getDevelopmentCardDeck(i).getSize() ==0 ){
                        isSpaceFound = true;
                    }
                }
                break;
            case 2:
                for(int i = 1; i<=3; i++){
                    if(personalBoard.getDevelopmentCardSpace().getHighestCard(i)!= null && personalBoard.getDevelopmentCardSpace().getHighestCard(i).getCardLevel() == 1){
                        isSpaceFound = true;
                    }
                }
                break;
            case 3:
                for(int i = 1; i<=3; i++){
                    if(personalBoard.getDevelopmentCardSpace().getHighestCard(i)!= null && personalBoard.getDevelopmentCardSpace().getHighestCard(i).getCardLevel() == 2){
                        isSpaceFound = true;
                    }
                }
                break;
            default:
                isSpaceFound = false;
        }
        if(!isSpaceFound){
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
            return;
        }
        //start transaction
        gameMode.getTransactionController().startTransaction(card,personalBoard,event.getIndex());

        //execute transaction
        try{
            gameMode.getTransactionController().executeTransaction();
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
        } catch (TransactionRequirementsNotMetException e) {
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
            //in case the transaction is rejected reposition the card where it was previously
            Deck<DevelopmentCard> targetDeck = gameSession.getDevelopmentCardGrid().getDeck(event.getRow(), event.getCol());

            //Check if the card was removed during the transaction (in that case reposition it)
            if(targetDeck != null && targetDeck.getSize() > 0 && targetDeck.peekElement() != card)
                gameSession.getDevelopmentCardGrid().getDeck(event.getRow(), event.getCol()).addElement(card);

            //Check if the card was added to the player's development card space (in that case remove it)
            DevelopmentCard playerSpaceCard = gameSession.getActivePlayer().getBoard().getDevelopmentCardSpace().getHighestCard(event.getIndex());

            if(playerSpaceCard != null && playerSpaceCard == card)
                gameSession.getActivePlayer().getBoard().getDevelopmentCardSpace().getDevelopmentCardDeck(event.getIndex()).removeElement();
        }

    }),


    /**
     * The player has requested to activate a production power
     */
    PRODUCTION_PHASE((event, gameMode) ->{
        GameSession gameSession = gameMode.getModel().getGameSession();
        PersonalBoard personalBoard = gameSession.getActivePlayer().getBoard();
        boolean isGenericProduction = false, isLeaderCardProduction = false, isDevCardProduction = false;

        //If the leader cards list is null make it into an empty one
        if(event.getLeaderCards() == null) event.setLeaderCards(new ArrayList<>());

        //determine which type of production is requested
        if((event.getDevCard() == -1) && (event.getLeaderCards().isEmpty()) && (event.getInputResources().size()==2) && (event.getOutputResources().size() == 1)) {
            isGenericProduction = true;
        }
        else if((event.getLeaderCards().isEmpty()) && (event.getDevCard() >= 0)){
            isDevCardProduction = true;
        }
        else if(event.getLeaderCards().size() == 1){
            isLeaderCardProduction = true;
        }

        //if the production selected is the generic one
        if(isGenericProduction){

            //try to start transaction, if the generic one has already been activated once, reject transaction
            //then add generic input and output if possible
            //then try to execute the transaction
            try {
                ProductionPower baseProductionPower = gameSession.getActivePlayer().getBoard().getProductionPowerRegistry().getActivatedProductionPowers().get(0);
                gameMode.getTransactionController().startTransaction(baseProductionPower,personalBoard);

                Resource genericInput1, genericInput2, genericOutput;
                StorageType src1, src2;

                genericInput1 = event.getInputResources().get(0).getFirst();
                src1 = event.getInputResources().get(0).getSecond();
                genericInput2 = event.getInputResources().get(1).getFirst();
                src2 = event.getInputResources().get(1).getSecond();
                genericOutput = event.getOutputResources().get(0);

                gameMode.getTransactionController().addGenericSource(genericInput1,src1);
                gameMode.getTransactionController().addGenericSource(genericInput2,src2);
                gameMode.getTransactionController().addGenericOutput(genericOutput);
                gameMode.getTransactionController().executeTransaction();
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
                personalBoard.getProductionPowerRegistry().addIDtoAlreadyUsed(-1);
            }
            catch (ProductionPowerAlreadyUsedException |TransactionRequirementsNotMetException | InsufficientResourcesException |ResourceNotInsertableException e)
            {gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);}

        }
        //if the production selected is a production of a DevCard
        else if(isDevCardProduction){
            DevelopmentCardSpace space = personalBoard.getDevelopmentCardSpace();
            List<DevelopmentCard> ownedDevelopmentCards = Stream.concat(space.getDevelopmentCardDeck(1).stream(),
                    Stream.concat(space.getDevelopmentCardDeck(2).stream(), space.getDevelopmentCardDeck(3).stream())).collect(Collectors.toList());

            ProductionPower power = ownedDevelopmentCards.stream()
                    .filter((x)->(x.getCardID() == event.getDevCard())).map(DevelopmentCard::getProductionPower).findFirst().get();

            //if the power has not been already activated in this turn, try to complete the transaction
            //if the requirements of the transaction are not met, the transaction is REJECTED
            //if the production selected is a production of a LeaderCard
            try {
                gameMode.getTransactionController().startTransaction(power, personalBoard);
                gameMode.getTransactionController().executeTransaction();
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
                personalBoard.getProductionPowerRegistry().addIDtoAlreadyUsed(event.getDevCard());
            }
            catch (ProductionPowerAlreadyUsedException|TransactionRequirementsNotMetException e) {
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
            }
        }
        else if(isLeaderCardProduction){
            List<LeaderCard> inactiveLeaderCards = new ArrayList<>(personalBoard.getLeaderCardSpace().getActiveCards());

            ProductionPower power = inactiveLeaderCards.stream()
                    .filter((x)->(x.getCardID() == event.getLeaderCards().get(0))).map((x) -> (((ProductionPower) x.getPower()))).findFirst().get();

            //add a generic resource as output
            //if the power has not been already activated in this turn, try to complete the transaction
            //if the requirements of the transaction are not met, the transaction is REJECTED
            try {
                gameMode.getTransactionController().startTransaction(power, personalBoard);
                gameMode.getTransactionController().addGenericOutput(event.getOutputResources().get(0));
                gameMode.getTransactionController().executeTransaction();
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
                personalBoard.getProductionPowerRegistry().addIDtoAlreadyUsed(event.getLeaderCards().get(0));
            }
            catch (ResourceNotInsertableException | ProductionPowerAlreadyUsedException | TransactionRequirementsNotMetException e) {
                gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
            }
        }

        //in this case the package sent is wrong since it does not fit any of the productions, there is an error
        else{
            gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.REJECTED);
        }
    }),
    /**
     * The player has chosen to finalize all transactions (production powers)
     */
    CLOSE_TRANSACTIONS((event, gameMode) ->{
        GameSession gameSession = gameMode.getModel().getGameSession();
        PersonalBoard personalBoard = gameSession.getActivePlayer().getBoard();

        //to clear the "usedProductionPower" for the next turn and to add the produced resources to the strongbox
        gameMode.getTransactionController().endTurn();
        gameMode.getModel().getGameSession().endMove(PlayerMoveResponse.SUCCESS);
        personalBoard.getProductionPowerRegistry().clearAlreadyUsed();
    }),
    /**
     * Handles a possible final leader action
     */
    FINAL_LEADER_ACTIONS(INITIAL_LEADER_ACTION.stateFlow);

    /* CLASS IMPLEMENTATION */

    //Holds the part of code to be executed in that state upon receiving a message from a player's view
    private BiConsumer<PlayerEvent, GameMode> stateFlow;

    /**
     * Private constructor for the TurnState
     * @param stateFlow the stateFlow
     */
    TurnState(BiConsumer<PlayerEvent, GameMode> stateFlow)
    {
        this.stateFlow = stateFlow;
    }

    /**
     * Used to execute the action associated with the turn
     * @param event the playerEvent received by the active player
     * @param gameMode the gameMode
     */
    public void accept(PlayerEvent event, GameMode gameMode)
    {
        this.stateFlow.accept(event, gameMode);
    }

    /**
     * Converts the TurnState (feature rich controller side) to a simple TurnState (on the model side)
     * @return Same value of the enum but without any functionality (usable on the model side)
     */
    public it.polimi.ingsw.common.model.TurnState getEquivalentModelState()
    {
        return it.polimi.ingsw.common.model.TurnState.valueOf(this.name());
    }

}
