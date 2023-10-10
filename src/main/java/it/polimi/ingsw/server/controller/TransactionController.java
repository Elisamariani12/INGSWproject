package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.gamemanager.DevelopmentCardRequest;
import it.polimi.ingsw.server.model.gamemanager.ProductionPowerRequest;
import it.polimi.ingsw.server.model.gamemanager.Transaction;
import it.polimi.ingsw.server.model.gameplay.PersonalBoard;
import it.polimi.ingsw.server.model.gameplay.ProductionPowerRegistry;
import it.polimi.ingsw.server.model.gameplay.StrongBox;
import it.polimi.ingsw.server.model.gameplay.WarehouseDepot;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Controller for an orderly flow of a Transaction
 */
public class TransactionController
{
    //Transaction currently being processed
    private Transaction currentTransaction;
    //Personal board of the player executing the transaction
    private PersonalBoard personalBoard;
    //Storage used to avoid interference between production power outputs and player's available storage during the turn
    private Map<Resource, Integer> temporaryStorage;
    //Lists all production powers used during the turn
    private List<ProductionPower> usedProductionPowers;
    //Game session instance
    private GameMode gameMode;
    //Generic Output choices
    private Resource genericOutputType;
    //Slot where the development card will be placed
    private int cardSlot;
    //Registers generic inputs as to avoid conflicts with regular resources
    private List<Pair<Resource, StorageType>> genericInputs;
    //Previous request resource
    private Resource previousGenericResource;
    //Previous request storage type
    private StorageType previousGenericStorageType;

    /* --- Used to execute the transaction --- */
    //Will be used as a checklist for all required input resources
    List<ResourceStack> shoppingList;
    //Will save the origin of each selected Resource amount
    List<Pair<ResourceStack, StorageType>> shoppingCart;

    /**
     * Creates a new Transaction Controller
     * @param gameMode th gameMode
     */
    public TransactionController(GameMode gameMode)
    {
        temporaryStorage = new HashMap<>();
        usedProductionPowers = new ArrayList<>();
        this.gameMode = gameMode;
        genericInputs = new ArrayList<>();
        shoppingCart = new ArrayList<>();
    }

    /**
     * Starts a new transaction of type DevelopmentCardRequest for the purchase of a Development Card
     * @param card DevelopmentCard to be bought
     * @param board Player's personal board
     * @param cardSlot the selected cardSlot
     */
    public void startTransaction(DevelopmentCard card, PersonalBoard board, int cardSlot)
    {
        this.personalBoard = board;
        currentTransaction = new DevelopmentCardRequest(card);
        this.cardSlot = cardSlot;
    }

    /**
     * Starts a new transaction of type ProductionPowerRequest for the activation of a production power
     * @param power Production Power to be activated
     * @param board Player's personal board
     * @exception ProductionPowerAlreadyUsedException If the production power has already been used during the turn
     */
    public void startTransaction(ProductionPower power, PersonalBoard board) throws ProductionPowerAlreadyUsedException
    {
        this.personalBoard = board;
        if(usedProductionPowers.contains(power)) throw new ProductionPowerAlreadyUsedException();
        else currentTransaction = new ProductionPowerRequest(power);

        previousGenericResource = null;
        previousGenericStorageType = null;
    }

    /**
     * Selects a Resource and a storage origin to be used for a generic resource slot
     * @param resource Resource type
     * @param source Origin of the used resource
     * @exception TransactionRequirementsNotMetException If the transaction does not contain generic inputs
     * @exception InsufficientResourcesException If the resource is not available in the specified storage source
     */
    public void addGenericSource(Resource resource, StorageType source) throws TransactionRequirementsNotMetException, InsufficientResourcesException
    {
        ProductionPowerRequest request = (ProductionPowerRequest) currentTransaction;

        //Check if the requirements contain generics
        if(!request.getProductionPower().getRequirements().stream().anyMatch((rs) -> rs.getResourceType() == Resource.GENERIC))
            throw new TransactionRequirementsNotMetException();

        boolean isResourceAvailable;
        boolean areBothResourcesFromTheSamePlace = (previousGenericResource == resource && previousGenericStorageType == source);
        int amountToCheck = areBothResourcesFromTheSamePlace ? 2 : 1;

        switch (source)
        {
            case WAREHOUSE_DEPOT:
                isResourceAvailable = personalBoard.getWarehouseDepot().getAmountByResource(resource) >= amountToCheck;
                break;
            case STRONG_BOX:
                isResourceAvailable = personalBoard.getStrongBox().getResourceAmount(resource) >= amountToCheck;
                break;
            case LEADER_CARD:
                //Precaution: if the storage is available, it will become true
                isResourceAvailable = false;
                for(Pair<LeaderCard, ResourceStack> pair : personalBoard.getLeaderCardSpace().getActiveStorageCards())
                {
                    if(pair.getSecond().getResourceType() == resource && pair.getSecond().getAmount() >= amountToCheck)
                    {
                        isResourceAvailable = true;
                        break;
                    }
                }
                break;
            default:
                isResourceAvailable = false;
                break;
        }

        if(isResourceAvailable)
            genericInputs.add(new Pair<>(resource, source));
        else
            throw new InsufficientResourcesException("Resource " + resource.name() + " not available in " + source.name());
    }

    /**
     * Removes a source previously added to be used for a generic resource slot
     * @param resource Resource type
     * @param source Origin of the used resource
     * @exception ResourceNotFoundInPaymentBufferException If the generic source was not specified in the first place
     */
    public void removeGenericSource(Resource resource, StorageType source) throws ResourceNotFoundInPaymentBufferException
    {
        for(Pair<Resource, StorageType> pair : genericInputs)
        {
            if(pair.getFirst() == resource && pair.getSecond() == source)
                genericInputs.remove(pair);
        }

    }

    /**
     * Selects a Resource type to be gained from the generic resource slot
     * @param resource Resource type
     * @exception ResourceNotInsertableException If the transaction does not contain any generic output
     */
    public void addGenericOutput(Resource resource) throws ResourceNotInsertableException
    {
        ProductionPowerRequest request = (ProductionPowerRequest) currentTransaction;

        //Check if the transaction output contains generic outputs
        if(!request.getProductionPower().getReward().stream().anyMatch((rs) -> rs.getResourceType() == Resource.GENERIC))
            throw new ResourceNotInsertableException();

        this.genericOutputType = resource;
    }

    /**
     * Returns a list of production powers already used during the turn
     * @return List of production powers used during the turn
     */
    public List<ProductionPower> getUsedProductionPowers()
    {
        return usedProductionPowers;
    }

    /**
     * Finalizes the transaction, checking all requirements are met
     * @exception TransactionRequirementsNotMetException If the transaction is executed improperly
     */
    public void executeTransaction() throws TransactionRequirementsNotMetException
    {
        shoppingCart.clear();

        transactionPreProcessing();

        /* ----- Add generic inputs to the list ----- */
        handleGenericInputs();

        if(shoppingList.stream().filter(rs -> rs.getResourceType() == Resource.GENERIC).count() > 0) throw new TransactionRequirementsNotMetException();

        /* ----- Construct list of input resources (non transaction-specific) ----- */
        checkWarehouseDepotAvailability();
        checkLeaderStorageAvailability();
        checkStrongBoxAvailability();

        //Check if it's enough
        if(shoppingList.stream().anyMatch(rs -> rs.getAmount() > 0))
            throw new TransactionRequirementsNotMetException();

        //Add payment sources to the transaction model
        for(Pair<ResourceStack, StorageType> paymentStack : shoppingCart)
        {
            //Payment sources are counted one at a time
            for(int i = 0; i < paymentStack.getFirst().getAmount(); i++)
                currentTransaction.addPaymentSource(paymentStack.getFirst().getResourceType(), paymentStack.getSecond());
        }

        //If it is, execute the transaction
        if(!currentTransaction.isTransactionValid())
            throw new TransactionRequirementsNotMetException();

        /* ###### Resource removal from player's inventory ###### */
        removeResourcesFromWarehouseDepot();
        removeResourcesFromLeaderStorage();
        removeResourcesFromStrongBox();

        /* ----- Post Processing ----- */
        transactionPostProcessing();

        shoppingCart.clear();

    }

    /**
     * To be called at the end of a turn where production powers are activated
     * empties usedProductionPowers list and temporary storage
     */
    public void endTurn()
    {
        //Empty the used production powers list
        usedProductionPowers.clear();

        //Empty the temporary storage
        for(Resource resource : temporaryStorage.keySet())
        {
            if(resource == Resource.FAITH) {
                gameMode.tryAdvance(gameMode.getModel().getGameSession().getActivePlayer(), temporaryStorage.get(Resource.FAITH));
                continue;
            }

            personalBoard.getStrongBox().addResourceAmount(resource, temporaryStorage.get(resource));
        }
        temporaryStorage.clear();

        genericInputs.clear();
    }

    /* ########## Methods to be used internally ######### */

    /**
     * Common operations to be executed at the beginning of a transaction's execution
     */
    private void transactionPreProcessing()
    {
        /* ----- Pre-Processing (Transaction-specific) ----- */
        if(currentTransaction instanceof DevelopmentCardRequest)
        {
            DevelopmentCardRequest request = (DevelopmentCardRequest) currentTransaction;
            //Clone the requirements to make a check list
            shoppingList = request.getDevelopmentCard().getRequiredResources().stream()
                    .map(rs -> rs.copy()).collect(Collectors.toList());

            applyAllDiscounts();
        }
        else if (currentTransaction instanceof ProductionPowerRequest)
        {
            ProductionPowerRequest request = (ProductionPowerRequest) currentTransaction;

            shoppingList = request.getProductionPower().getRequirements().stream()
                    .map(rs -> rs.copy()).collect(Collectors.toList());
        }
    }

    /**
     * Applies discounts to the required resources
     */
    private void applyAllDiscounts()
    {
        DevelopmentCardRequest request = (DevelopmentCardRequest) currentTransaction;

        //Register all discounts
        ArrayList<Resource> discounts = new ArrayList<>();

        for(LeaderCard card : personalBoard.getLeaderCardSpace().getActiveCards())
            if(card.getPower().getSpecialAbilityType() == SpecialAbility.DISCOUNT)
            {
                AbilityPower discountPower = (AbilityPower)card.getPower();
                discounts.add(discountPower.getResourceType());
            }

        //Pass discounts to the transaction
        if(discounts.size() == 1)
            request.registerDiscount(discounts.get(0));
        else if (discounts.size() == 2)
            request.registerDiscount(discounts.get(0), discounts.get(1));
        else if (discounts.size() != 0) throw new ArrayIndexOutOfBoundsException("You shouldn't be able to have three discounts!!!");

        //Remove any discounted amount from the shopping list
        shoppingList.stream().forEach(rs -> {
            for(Resource discounted : discounts)
                if(rs.getResourceType() == discounted && rs.getAmount() > 0)
                {
                    try {
                        rs.setAmount(rs.getAmount() - 1);
                    } catch (FullResourceStackException exception) {
                        exception.printStackTrace();
                    }
                }
        });
        //Check if any stacks are empty after discount
        for(int i = 0; i < shoppingList.size(); i++)
            if(shoppingList.get(i).getAmount() == 0)
                shoppingList.remove(i);
    }

    /**
     * Used to register generic inputs and include them in the shopping list
     */
    private void handleGenericInputs()
    {

        //For each resource type, add to the shopping list
        for(Pair<Resource, StorageType> pair : genericInputs)
        {
            Resource genericInputResourceType = pair.getFirst();
            StorageType genericInputStorageSource = pair.getSecond();

            //Find a match in the shoppingCart
            Optional<Pair<ResourceStack, StorageType>> match = shoppingCart.stream()
                    .filter(cartPair -> cartPair.getFirst().getResourceType() == genericInputResourceType && cartPair.getSecond() == genericInputStorageSource).findFirst();

            //If there is a match, add one
            if(match.isPresent())
            {
                int prevAmount = match.get().getFirst().getAmount();
                try{
                    match.get().getFirst().setAmount(prevAmount + 1);
                } catch (FullResourceStackException e) {
                    e.printStackTrace();
                }
            }
            //Else create a new entry in the shopping cart
            else
            {
                try {
                    ResourceStack newStack = new ResourceStack(genericInputResourceType, 1, Integer.MAX_VALUE);
                    shoppingCart.add(new Pair<>(newStack, genericInputStorageSource));
                } catch (FullResourceStackException e) {
                    e.printStackTrace();
                }
            }

            //Remove generic from Shopping list
            for(ResourceStack rs : shoppingList)
            {
                if(rs.getResourceType() == Resource.GENERIC)
                {
                    try
                    {
                        rs.setAmount(rs.getAmount() - Math.min(rs.getAmount(), 1));
                    }
                    catch (FullResourceStackException e) {e.printStackTrace();}
                }
            }

            //Remove empty resource stacks
            shoppingList = shoppingList.stream().filter(rs -> rs.getAmount() > 0).collect(Collectors.toList());

        }

    }

    /**
     * Check what amount of required resources can be procured from the warehouse depot
     */
    private void checkWarehouseDepotAvailability()
    {
        //Add from Warehouse depot first
        for(ResourceStack item : shoppingList)
        {

            int availableAmount = personalBoard.getWarehouseDepot().getAmountByResource(item.getResourceType());
            availableAmount = Math.min(availableAmount, /* required amount */item.getAmount());

            if(availableAmount <= 0) continue;

            try
            {
                item.setAmount(item.getAmount() - availableAmount);
            }
            catch (FullResourceStackException exception)
            {
                exception.printStackTrace();
            }

            //Save to the shopping cart
            ResourceStack selectedSource = null;
            try {
                selectedSource = new ResourceStack(item.getResourceType(), availableAmount, Integer.MAX_VALUE);
            } catch (FullResourceStackException exception) {
                exception.printStackTrace();
            }
            shoppingCart.add(new Pair<>(selectedSource, StorageType.WAREHOUSE_DEPOT));

        }
    }

    /**
     * Check what amount of required resources can be procured from the active leader cards with storage capabilities
     */
    private void checkLeaderStorageAvailability()
    {
        List<ResourceStack> leaderStorageOptions;
        //Select all resource stacks from active leader cards capable of storage
        leaderStorageOptions = personalBoard.getLeaderCardSpace().getActiveStorageCards().stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());

        for(ResourceStack miniStorage : leaderStorageOptions)
        {
            Predicate<ResourceStack> correspondence = rs -> rs.getResourceType() == miniStorage.getResourceType();
            //Check if the available leader storage is compatible with the shopping list
            Optional<ResourceStack> found = shoppingList.stream().filter(correspondence).findFirst();

            //If not, check the next leader storage
            if(found.isEmpty()) continue;
            //else

            int availableAmount = Math.min(miniStorage.getAmount(), found.get().getAmount());

            if (found.get().getAmount() == 0 || availableAmount <= 0) continue;

            try { found.get().setAmount(found.get().getAmount() - availableAmount);}
            catch (FullResourceStackException exception) { exception.printStackTrace(); }
            //Save to the shopping cart
            ResourceStack selectedSource = null;
            try { selectedSource = new ResourceStack(miniStorage.getResourceType(), availableAmount, Integer.MAX_VALUE); }
            catch (FullResourceStackException exception) { exception.printStackTrace(); }
            shoppingCart.add(new Pair<>(selectedSource, StorageType.LEADER_CARD));
        }
    }

    /**
     * Check what amount of required resources can be procured from the strongbox
     */
    private void checkStrongBoxAvailability()
    {
        //Add from StrongBox
        for(ResourceStack item : shoppingList)
        {
            StrongBox strongBox = personalBoard.getStrongBox();
            int availableAmount = Math.min(item.getAmount(), strongBox.getResourceAmount(item.getResourceType()));

            if(availableAmount <= 0) continue;

            try
            {
                item.setAmount(item.getAmount() - availableAmount);
            }
            catch (FullResourceStackException exception)
            {
                exception.printStackTrace();
            }

            ResourceStack selectedSource = null;
            try {
                selectedSource = new ResourceStack(item.getResourceType(), availableAmount, Integer.MAX_VALUE);
            } catch (FullResourceStackException exception) {
                exception.printStackTrace();
            }
            shoppingCart.add(new Pair<>(selectedSource, StorageType.STRONG_BOX));
        }
    }

    /**
     * Removes resources from the player's warehouse depot
     */
    private void removeResourcesFromWarehouseDepot()
    {
        WarehouseDepot warehouseDepot = personalBoard.getWarehouseDepot();
        List<ResourceStack> toRemoveFromWarehouse = shoppingCart.stream()
                .filter(pair -> pair.getSecond() == StorageType.WAREHOUSE_DEPOT)
                .map(Pair::getFirst)
                .collect(Collectors.toList());

        for(ResourceStack stack : toRemoveFromWarehouse)
        {
            int layerIndex;
            //Find which layer holds the required resource
            for(layerIndex = 1; layerIndex <= 3; layerIndex++)
                if(warehouseDepot.getLayerType(layerIndex) == stack.getResourceType()) break;

            //Remove it from the layer
            try
            {
                warehouseDepot.setLayerAmount(layerIndex, warehouseDepot.getLayerAmount(layerIndex) - stack.getAmount());
            } catch (FullResourceStackException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Removes resources from the player's leader cards
     */
    private void removeResourcesFromLeaderStorage()
    {
        //Actual leader card stacks
        List<ResourceStack> leaderCardStacks = personalBoard.getLeaderCardSpace().getActiveStorageCards().stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());
        //Reference stacks containing the amounts that need to be removed
        List<ResourceStack> toRemoveFromLeaderCards = shoppingCart.stream()
                .filter(pair -> pair.getSecond() == StorageType.LEADER_CARD)
                .map(Pair::getFirst)
                .collect(Collectors.toList());

        for(ResourceStack referenceStack : toRemoveFromLeaderCards)
        {
            //Very specific rare case
            if(leaderCardStacks.size() > 1 && leaderCardStacks.get(0).getResourceType() == leaderCardStacks.get(1).getResourceType())
            {

                int firstAmount = Math.min(leaderCardStacks.get(0).getAmount(), referenceStack.getAmount());
                try {
                    //Remove the available amount from the first
                    leaderCardStacks.get(0).setAmount(leaderCardStacks.get(0).getAmount() - firstAmount);
                    int secondAmount = referenceStack.getAmount() - firstAmount;
                    //Remove any remaining amounts from the second
                    if(secondAmount > 0)
                        leaderCardStacks.get(1).setAmount(leaderCardStacks.get(1).getAmount() - secondAmount);
                } catch (FullResourceStackException exception) {
                    exception.printStackTrace();
                }
            }
            else
            {
                //Find
                Optional<ResourceStack> stackToRemoveFrom = leaderCardStacks.stream()
                        .filter(stack -> stack.getResourceType() == referenceStack.getResourceType())
                        .findFirst();

                //Remove
                try { stackToRemoveFrom.get().setAmount(stackToRemoveFrom.get().getAmount() - referenceStack.getAmount()); }
                catch (FullResourceStackException exception) { exception.printStackTrace(); }
            }
        }
    }

    /**
     * Removes resources from the player's strongbox
     */
    private void removeResourcesFromStrongBox()
    {
        StrongBox strongBox = personalBoard.getStrongBox();
        List<ResourceStack> toRemoveFromStrongBox = shoppingCart.stream()
                .filter(pair -> pair.getSecond() == StorageType.STRONG_BOX)
                .map(Pair::getFirst)
                .collect(Collectors.toList());

        for(ResourceStack stack : toRemoveFromStrongBox)
        {
            try { strongBox.removeResourceAmount(stack.getResourceType(), stack.getAmount()); }
            catch(InsufficientResourcesException exception) { exception.printStackTrace(); }
        }
    }

    /**
     * Common operations to be executed at the end of a transaction's execution
     */
    private void transactionPostProcessing()
    {
        if(currentTransaction instanceof DevelopmentCardRequest)
        {
            DevelopmentCardRequest request = (DevelopmentCardRequest) currentTransaction;

            //Get the player's production power registry
            ProductionPowerRegistry prodPowerReg = personalBoard.getProductionPowerRegistry();
            DevelopmentCard coveredCard = personalBoard.getDevelopmentCardSpace().getHighestCard(cardSlot);

            //Unregister the card that will be covered
            if(coveredCard != null)
                prodPowerReg.removeActivatedProductionPower(coveredCard.getProductionPower());

            //Payment done, now let's place the card
            personalBoard.getDevelopmentCardSpace().pushCard(cardSlot, request.getDevelopmentCard());
            prodPowerReg.registerActivatedProductionPower(request.getDevelopmentCard().getProductionPower());

            //If the player buys the 7th card, this is the last round for the session
            if(personalBoard.getDevelopmentCardSpace().getTotalCardAmount() >= GameConstants.DEV_CARD_MAX_NUMBER_TO_WIN)
                gameMode.getModel().getGameSession().setIsLastRound();

        }
        else if (currentTransaction instanceof ProductionPowerRequest)
        {
            ProductionPowerRequest request = (ProductionPowerRequest) currentTransaction;

            //Put generic outputs in the temporary storage
            Optional<ResourceStack> genericOutStack = request.getProductionPower().getReward().stream()
                    .filter(rs -> rs.getResourceType() == Resource.GENERIC)
                    .findFirst();

            int genericOutAmount = genericOutStack.isPresent() ? genericOutStack.get().getAmount() : 0;

            //If there's any generic output
            if(genericOutAmount > 0)
            {
                Integer prevTemporaryAmount = temporaryStorage.get(genericOutputType);
                temporaryStorage.put(genericOutputType, prevTemporaryAmount == null ? genericOutAmount : prevTemporaryAmount + genericOutAmount);
            }

            //Retrieve non-generic rewards
            for(ResourceStack referenceOutput : request.getProductionPower().getReward())
            {
                if(referenceOutput.getResourceType() == Resource.GENERIC) continue;
                //Add output to the temporary storage
                Integer prevAmount = temporaryStorage.get(referenceOutput.getResourceType());
                temporaryStorage.put(referenceOutput.getResourceType(), prevAmount == null ? referenceOutput.getAmount() : referenceOutput.getAmount() + prevAmount);
            }

            usedProductionPowers.add(request.getProductionPower());

            genericInputs.clear();
            genericOutputType = null;

        }
    }

}
