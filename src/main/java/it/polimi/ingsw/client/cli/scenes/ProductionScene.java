package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.client.exceptions.CLIWriterException;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.exception.FullResourceStackException;

import java.util.*;

/**
 * CLI scene to let the player do a Production move
 */
public class ProductionScene extends TextScene {
    private static final String background =
            "\n" +
            "\n" +
            "                          ╔═╗┬─┐┌─┐┌┬┐┬ ┬┌─┐┌┬┐┬┌─┐┌┐┌\n" +
            "                          ╠═╝├┬┘│ │ │││ ││   │ ││ ││││\n" +
            "                          ╩  ┴└─└─┘─┴┘└─┘└─┘ ┴ ┴└─┘┘└┘\n" +
            "\n" +
            "     ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "     █ 'A' is the generic production, 'B','C' and 'D' are the development █\n" +
            "     █  cards you own,'E'+'F' are your leader cards with production power █\n" +
            "     █ ╔═════A═════╗╔═════B═════╗╔═════C═════╗╔═════D═════╗╔═════E═════╗  █\n" +
            "     █ ║           ║║In:        ║║In:        ║║In:        ║║In:        ║  █\n" +
            "     █ ║           ║║           ║║           ║║           ║║           ║  █\n" +
            "     █ ║           ║║           ║║           ║║           ║║Out:  ?+   ║  █\n" +
            "     █ ║  Generic  ║║           ║║           ║║           ║║           ║  █\n" +
            "     █ ║production ║║           ║║           ║║           ║╚═══════════╝  █\n" +
            "     █ ║           ║║Out:       ║║Out:       ║║Out:       ║╔═════F═════╗  █\n" +
            "     █ ║   In=?+?  ║║           ║║           ║║           ║║In:        ║  █\n" +
            "     █ ║   Out=?   ║║           ║║           ║║           ║║           ║  █\n" +
            "     █ ║           ║║           ║║           ║║           ║║Out:  ?+   ║  █\n" +
            "     █ ║           ║║           ║║           ║║           ║║           ║  █\n" +
            "     █ ╚═══════════╝╚═══════════╝╚═══════════╝╚═══════════╝╚═══════════╝  █\n" +
            "     █ Your deposits:                                                     █\n" +
            "     █   Warehouse depot     StrongBox         LeaderCard storage:        █\n" +
            "     █   -coins:             -coins:           -coins:                    █\n" +
            "     █   -servants:          -servants:        -servants:                 █\n" +
            "     █   -shields:           -shields:         -shields:                  █\n" +
            "     █   -stones:            -stones:          -stones:                   █\n" +
            "     █  TYPE:                                                             █\n" +
            "     █ *letter-ResourceInInput1-ResourceInInput2-ResourceInOutput1 if you █\n" +
            "     █  want to activate the generic power                                █\n" +
            "     █ *letter-ResourceInOutput if you have to choose the output resources█\n" +
            "     █ *letter if you can not decide anything in the production           █\n" +
            "     █ *END to terminate your production turn phase                       █\n" +
            "     █                                                                    █\n" +
            "     █ Type the resources with their full name (ex.'stone','coin')        █\n" +
            "     █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█";

    //generic power
    private static final int GENERIC_POWER_STATE_ROW= 9;
    private static final int GENERIC_POWER_STATE_COL= 12;

    //dev cards
    private static final int[] BOXDEVCARD_COLUMNS={22,35,48};
    private static final int BOXDEVCARD_INPUTS_ROW= 11;
    private static final int BOXDEVCARD_OUTPUT_ROW= 16;
    private static final int BOXDEVCARD_HEIGHT= 10;
    private static final int BOXDEVCARD_STATE_ROW= 9;

    //leader cards
    private static final int BOXLEADCARD_HEIGHT= 4;
    private static final int BOXLEADCARD_COL= 61;
    private static final int[] BOXLEADCARD_INPUTS_ROW= {11,17};
    private static final int[] BOXLEADCARD_OUT_ROW= {13,19};
    private static final int[] BOXLEADCARD_STATE_ROW= {9,15};


    //your resources section
    private static final int BOX_COINS_ROW =23;
    private static final int BOX_SERVANTS_ROW =24;
    private static final int BOX_SHIELDS_ROW =25;
    private static final int BOX_STONES_ROW =26;
    private static final int BOX_RESOURCES_WD_COLUMN =22;
    private static final int BOX_RESOURCES_SB_COLUMN =41;
    private static final int BOX_RESOURCES_LEADERCARD_COLUMN =60;

    private Scanner input;
    private HashMap<Character, Integer> chooseProductionMap;
    private boolean isFirstProduction = true;
    //Used for productions that have a generic input (Resource Type, Storage Source, Amount taken)
    private ArrayList<Triplet<Resource, StorageType, Integer>> choiceBuffer = new ArrayList<>();

    @Override
    public void draw(CompressedModel compressedModel) {
        String[] arrayToPrint = super.toArrayFromString(background);
        chooseProductionMap = new HashMap<>();
        boolean productionAlreadyUsed;

        mapFiller(compressedModel);

        //CHECK IF GENERIC POWER WAS ALREADY USED
        if(compressedModel.getTurnAlreadyUsedProductionCardIDs().contains(-1)){
            arrayToPrint = substituteLineBackground(arrayToPrint, "DONE", GENERIC_POWER_STATE_ROW, GENERIC_POWER_STATE_COL);
        }

        //DRAW DEV CARDS AND FILL choseProductionMap
        int deckCount =0;
        for( Stack<Integer> deck: compressedModel.getDevCardSpace().get(compressedModel.getaP())){
            try {
                DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(deck.peek());
                productionAlreadyUsed = compressedModel.getTurnAlreadyUsedProductionCardIDs().contains(developmentCard.getCardID());
                arrayToPrint = devCardRequRew(developmentCard.getProductionPower().getRequirements(), arrayToPrint, deckCount, true, !productionAlreadyUsed);
                arrayToPrint = devCardRequRew(developmentCard.getProductionPower().getReward(), arrayToPrint, deckCount, false, !productionAlreadyUsed);
                deckCount++;
            }
            catch(EmptyStackException e){
                for(int i=0;i<BOXDEVCARD_HEIGHT;i++){arrayToPrint = substituteLineBackground(arrayToPrint,"░░░░░░░░░░░", BOXDEVCARD_INPUTS_ROW+i-1, BOXDEVCARD_COLUMNS[deckCount]);}
                deckCount++;
            }
        }
        while(deckCount< GameConstants.DEV_CARD_NUMBER_OF_SPACES){
            for(int i=0;i<BOXDEVCARD_HEIGHT;i++){arrayToPrint = substituteLineBackground(arrayToPrint,"░░░░░░░░░░░", BOXDEVCARD_INPUTS_ROW+i-1, BOXDEVCARD_COLUMNS[deckCount]);}
            deckCount++;
        }

        //DRAW LEADER CARDS
        int countleadercard=0;
        for( Integer idCard: compressedModel.getAllPlayersActiveLeaderCards().get(compressedModel.getaP())) {
            LeaderCard leaderCard=CardRepository.getInstance().getLeaderCardByID(idCard);
            
            if(leaderCard.getPower().getSpecialAbilityType().equals(SpecialAbility.PRODUCTION)){
                List<ResourceStack> listInputs=((ProductionPower) leaderCard.getPower()).getRequirements();
                List<ResourceStack> listOutputs=((ProductionPower) leaderCard.getPower()).getReward();
                productionAlreadyUsed = compressedModel.getTurnAlreadyUsedProductionCardIDs().contains(leaderCard.getCardID());
                arrayToPrint = leadCardINnOUT(listInputs, arrayToPrint, countleadercard, true, !productionAlreadyUsed);
                arrayToPrint = leadCardINnOUT(listOutputs, arrayToPrint, countleadercard, false, !productionAlreadyUsed);
                countleadercard++;
            }
        }
        while(countleadercard< GameConstants.LEADER_CARDS_CHOICE_AMOUNT){
            for(int i=0;i<BOXLEADCARD_HEIGHT;i++){arrayToPrint = substituteLineBackground(arrayToPrint,"░░░░░░░░░░░",BOXLEADCARD_INPUTS_ROW[countleadercard]+i-1 , BOXLEADCARD_COL);}
            countleadercard++;
        }

        //draw your resources in WD
        int numCoin=0,numStone=0,numShield=0,numServant=0;
        for(ResourceStack resourceStack:compressedModel.getaPWD()){
            if(resourceStack.getResourceType()==Resource.COIN){arrayToPrint=substituteLineBackground(arrayToPrint, Integer.toString(resourceStack.getAmount()),BOX_COINS_ROW,BOX_RESOURCES_WD_COLUMN);numCoin=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.SERVANT){arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_SERVANTS_ROW,BOX_RESOURCES_WD_COLUMN);numServant=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.STONE){arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_STONES_ROW,BOX_RESOURCES_WD_COLUMN);numStone=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.SHIELD) {arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_SHIELDS_ROW,BOX_RESOURCES_WD_COLUMN);numShield=resourceStack.getAmount();}
        }
        if(numCoin==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_COINS_ROW,BOX_RESOURCES_WD_COLUMN);
        if(numServant==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_SERVANTS_ROW,BOX_RESOURCES_WD_COLUMN);
        if(numStone==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_STONES_ROW,BOX_RESOURCES_WD_COLUMN);
        if(numShield==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_SHIELDS_ROW,BOX_RESOURCES_WD_COLUMN);

        //DRAW RESOURCES IN SB
        numCoin=0;numServant=0;numShield=0;numStone=0;
        for(ResourceStack resourceStack:compressedModel.getaPSB()) {
            if(resourceStack.getResourceType()==Resource.COIN){arrayToPrint=substituteLineBackground(arrayToPrint, Integer.toString(resourceStack.getAmount()),BOX_COINS_ROW,BOX_RESOURCES_SB_COLUMN);numCoin=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.SERVANT){arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_SERVANTS_ROW,BOX_RESOURCES_SB_COLUMN);numServant=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.STONE){arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_STONES_ROW,BOX_RESOURCES_SB_COLUMN);numStone=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.SHIELD) {arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_SHIELDS_ROW,BOX_RESOURCES_SB_COLUMN);numShield=resourceStack.getAmount();}
        }
        if(numCoin==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_COINS_ROW,BOX_RESOURCES_SB_COLUMN);
        if(numServant==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_SERVANTS_ROW,BOX_RESOURCES_SB_COLUMN);
        if(numStone==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_STONES_ROW,BOX_RESOURCES_SB_COLUMN);
        if(numShield==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_SHIELDS_ROW,BOX_RESOURCES_SB_COLUMN);

        //DRAW RESOURCES IN LEADER STORAGE
        numCoin=0;numServant=0;numShield=0;numStone=0;
        for(ResourceStack resourceStack: compressedModel.getaPLeaderStorage()){
            if(resourceStack.getResourceType()==Resource.COIN){arrayToPrint=substituteLineBackground(arrayToPrint, Integer.toString(resourceStack.getAmount()),BOX_COINS_ROW,BOX_RESOURCES_LEADERCARD_COLUMN);numCoin=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.SERVANT){arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_SERVANTS_ROW,BOX_RESOURCES_LEADERCARD_COLUMN);numServant=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.STONE){arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_STONES_ROW,BOX_RESOURCES_LEADERCARD_COLUMN);numStone=resourceStack.getAmount();}
            else if(resourceStack.getResourceType()==Resource.SHIELD) {arrayToPrint=substituteLineBackground(arrayToPrint,String.valueOf(resourceStack.getAmount()),BOX_SHIELDS_ROW,BOX_RESOURCES_LEADERCARD_COLUMN);numShield=resourceStack.getAmount();}
        }
        if(numCoin==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_COINS_ROW,BOX_RESOURCES_LEADERCARD_COLUMN);
        if(numServant==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_SERVANTS_ROW,BOX_RESOURCES_LEADERCARD_COLUMN);
        if(numStone==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_STONES_ROW,BOX_RESOURCES_LEADERCARD_COLUMN);
        if(numShield==0)arrayToPrint=substituteLineBackground(arrayToPrint,"0",BOX_SHIELDS_ROW,BOX_RESOURCES_LEADERCARD_COLUMN);

        System.out.println(super.toStringFromArray(arrayToPrint));
    }

    @Override
    public void askInput(CompressedModel compressedModel) {
        chooseProductionMap = new HashMap<>();
        mapFiller(compressedModel);
        boolean isActionableAction = false;
        boolean hasEnoughResources = true;
        boolean playerWantToEnd = false;
        isFirstProduction = compressedModel.getTurnAlreadyUsedProductionCardIDs() != null && compressedModel.getTurnAlreadyUsedProductionCardIDs().isEmpty();
        Triplet<Character, ArrayList<Resource>, ArrayList<Resource>> semanticallyCorrectInput;
        ArrayList<Pair<Resource, StorageType>> inputResourcesWithDeposit = null;

        do {
            semanticallyCorrectInput = askSemanticallyCorrectInput();
            //Player has requested to go back
            if(semanticallyCorrectInput == null)
            {
                registerGoBackRequest();
                return;
            }
            isActionableAction = checkIfActionable(semanticallyCorrectInput);
            if(!isActionableAction){
                System.out.println("Syntax was correct, but you have already activated this power during this turn, try with another one \n");
            }
            else if(semanticallyCorrectInput.getFirst() != 'Q'){
                hasEnoughResources = checkNeededResources(semanticallyCorrectInput, compressedModel);
                if(!hasEnoughResources){
                    System.out.println("Syntax was correct, but you don't have enough resources to activate this power during this turn, try with another one \n");
                }
            }
            else if(semanticallyCorrectInput.getFirst() == 'Q') hasEnoughResources = true;
        }while (!isActionableAction || !hasEnoughResources);

        if(semanticallyCorrectInput.getFirst() == 'A')
        {
            choiceBuffer.clear();
            inputResourcesWithDeposit = askPlayerWhereToRetrieveResources(semanticallyCorrectInput, compressedModel);
        }

        fillPlayerEvent(semanticallyCorrectInput, inputResourcesWithDeposit);

    }



    //-----------
    // DRAW  & ASK_INPUT HELPERS----
    //-----------
    /**
     * Fill a map that contains the actionable powers
     * @param compressedModel the received compressed model
     */
    private void mapFiller(CompressedModel compressedModel){
        boolean productionAlreadyUsed;

        if(!compressedModel.getTurnAlreadyUsedProductionCardIDs().contains(-1)){
            fillMap(-1,-1,false);
        }

        int deckCount =0;
        for( Stack<Integer> deck: compressedModel.getDevCardSpace().get(compressedModel.getaP())){
            try {
                DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(deck.peek());
                productionAlreadyUsed = compressedModel.getTurnAlreadyUsedProductionCardIDs().contains(developmentCard.getCardID());
                if(!productionAlreadyUsed){
                    fillMap(developmentCard.getCardID(), deckCount,true);
                }
                deckCount++;
            }
            catch(EmptyStackException e){
                deckCount++;
            }
        }

        int countleadercard=0;
        for( Integer idCard: compressedModel.getAllPlayersActiveLeaderCards().get(compressedModel.getaP())) {
            LeaderCard leaderCard=CardRepository.getInstance().getLeaderCardByID(idCard);

            if(leaderCard.getPower().getSpecialAbilityType().equals(SpecialAbility.PRODUCTION)){
                productionAlreadyUsed = compressedModel.getTurnAlreadyUsedProductionCardIDs().contains(leaderCard.getCardID());
                if(!productionAlreadyUsed){
                    fillMap(leaderCard.getCardID(), countleadercard,false);
                }
                countleadercard++;
            }
        }

    }

    /**
     * When the player end his production move a player event is sent, this method fill the player event
     * @param semanticallyCorrectInput the input already checked for its syntax
     * @param inputResourcesWithDeposit the chosen production resources with their deposits
     */
    private void fillPlayerEvent(Triplet<Character, ArrayList<Resource>, ArrayList<Resource>> semanticallyCorrectInput, ArrayList<Pair<Resource, StorageType>> inputResourcesWithDeposit) {

        switch (semanticallyCorrectInput.getFirst()){
            case 'A':
                PlayerWorkSpace.getInstance().genericProductionEvent(inputResourcesWithDeposit, semanticallyCorrectInput.getThird().get(0));
                break;
            case 'B':
            case 'C':
            case 'D':
                PlayerWorkSpace.getInstance().devCardProductionEvent(chooseProductionMap.get(semanticallyCorrectInput.getFirst()));
                break;
            case 'E':
            case 'F':
                PlayerWorkSpace.getInstance().leadCardProductionEvent(chooseProductionMap.get(semanticallyCorrectInput.getFirst()), semanticallyCorrectInput.getThird());
                break;
            case 'Q':
                PlayerWorkSpace.getInstance().endProductionEvent();
                break;
            default:
                throw new CLIWriterException();
        }
    }

    /**
     * When there are generic input resources user has to select where he wants to pick these
     *
     * @param semanticallyCorrectInput the input already checked for its syntax
     * @param compressedModel the actual compressed model
     * @return the list of selected resources with their deposits reference
     */
    private ArrayList<Pair<Resource, StorageType>> askPlayerWhereToRetrieveResources(Triplet<Character, ArrayList<Resource>, ArrayList<Resource>> semanticallyCorrectInput, CompressedModel compressedModel) {
        ArrayList<Pair<Resource, StorageType>> inputResourcesWithDeposit = new ArrayList<>();
        inputResourcesWithDeposit.add(new Pair<>(semanticallyCorrectInput.getSecond().get(0), null));
        inputResourcesWithDeposit.add(new Pair<>(semanticallyCorrectInput.getSecond().get(1), null));
        boolean correctDeposit;
        for(Pair<Resource, StorageType> storageTypePair : inputResourcesWithDeposit){
            int inputStorage = 0;
            correctDeposit = false;
            while(!correctDeposit){
                System.out.println("Select from where you want to pick: " + storageTypePair.getFirst() + " type 1 for WarehouseDepot, 2 for StrongBox, 3 for LeaderCard Space");
                boolean isInputCorrect=false;
                while(!isInputCorrect) {
                    try {
                        inputStorage = input.nextInt();
                        isInputCorrect=true;
                    }
                    catch(InputMismatchException e){
                        System.out.println("The entered value was not recognised, try again...");
                        input = new Scanner(System.in);
                        isInputCorrect=false;
                    }
                }

                StorageType storage = (inputStorage == 1) ? StorageType.WAREHOUSE_DEPOT : ((inputStorage == 2) ? StorageType.STRONG_BOX : StorageType.LEADER_CARD);

                //Check if the player has taken the same resource from the same source
                Optional<Integer> takenAmount = choiceBuffer.stream()
                        .filter(element -> element.getFirst() == storageTypePair.getFirst())
                        .filter(element -> element.getSecond() == storage)
                        .map(Triplet::getThird).findFirst();

                if(takenAmount.isEmpty())
                    choiceBuffer.add(new Triplet<>(storageTypePair.getFirst(), storage, 1));
                else
                {
                    choiceBuffer.stream()
                            .filter(element -> element.getFirst() == storageTypePair.getFirst())
                            .filter(element -> element.getSecond() == storage)
                            .limit(1)
                            .forEach(element -> element.setThird(element.getThird() + 1));
                }

                //Once he has selected the deposit, must be checked if enough resources are present in it
                correctDeposit = checkIfContainedInDeposit(storage, storageTypePair.getFirst(), compressedModel);


            }
            //RETURN DEPOSIT
            updateStorageType(inputStorage, storageTypePair);
        }
        return inputResourcesWithDeposit;
    }

    /**
     * Updates the storageType with the input selected by the user
     * @param inputStorage to know which storage has chosen the player
     * @param storageTypePair the pair to be set
     */
    private void updateStorageType(int inputStorage, Pair<Resource, StorageType> storageTypePair){
        switch (inputStorage){
            case 1:
                storageTypePair.setSecond(StorageType.WAREHOUSE_DEPOT);
                break;
            case 2:
                storageTypePair.setSecond(StorageType.STRONG_BOX);
                break;
            case 3:
                storageTypePair.setSecond(StorageType.LEADER_CARD);
        }
    }

    /**
     * Checks if selected deposit has enough resorces
     * @param inputStorage input storage type
     * @param resource the resource that has to be checked
     * @param compressedModel the updated compressed model
     * @return is this resource contained?
     */
    private boolean checkIfContainedInDeposit(StorageType inputStorage, Resource resource, CompressedModel compressedModel) {
        int amount = choiceBuffer.stream()
                .filter(elem -> elem.getFirst() == resource)
                .filter(elem -> elem.getSecond() == inputStorage)
                .map(Triplet::getThird)
                .findFirst().get();

        switch (inputStorage){
            case WAREHOUSE_DEPOT:
                for(ResourceStack resourceStack: compressedModel.getaPWD()){
                    if(resourceStack.getResourceType() == resource && resourceStack.getAmount()>= amount){
                        return true;
                    }
                }
                System.out.println("Not Enough resources in this deposit");
                return false;

            case STRONG_BOX:
                for(ResourceStack resourceStack: compressedModel.getaPSB()){
                    if(resourceStack.getResourceType() == resource && resourceStack.getAmount()>= amount){
                        return true;
                    }
                }
                System.out.println("Not Enough resources in this deposit");
                return false;

            case LEADER_CARD:
                for(ResourceStack resourceStack: compressedModel.getaPLeaderStorage()){
                    if(resourceStack.getResourceType() == resource && resourceStack.getAmount()>= amount){
                        return true;
                    }
                }
                System.out.println("Not Enough resources in this deposit");
                return false;

            default:
                System.out.println("SYNTAX ERROR");
                return false;
        }
    }


    /**
     * Return total amount of the selected resource owned by active player
     * @param res the resource that has to be counted
     * @param compressedModel the updated compressed model
     * @return the number of the input resource owned by the player
     */
    private int getTotalAmountOfResources(Resource res, CompressedModel compressedModel){
        int numberOfRes;
        try{numberOfRes= Arrays.stream(compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP())).filter((x)->(x.getResourceType()== res)).findFirst().get().getAmount();}
        catch(NoSuchElementException e){numberOfRes=0;}

        return numberOfRes;
    }


    /**
     * Check if the player has enough resources to activate the selected power
     * @param semanticallyCorrectInput the input already corrected
     * @param compressedModel the updated compressed model
     * @return if it's possible to buy that selected card
     */
    private boolean checkNeededResources(Triplet<Character, ArrayList<Resource>, ArrayList<Resource>> semanticallyCorrectInput, CompressedModel compressedModel) {
        List<ResourceStack> listInputs = null;
        //If Generic Power is requested
        if(semanticallyCorrectInput.getFirst() == 'A'){
            for(Resource genericRes : semanticallyCorrectInput.getSecond()){
                //There are two input resources, those can be different or of the same type, it create a single or a double ResourceStack based on this
                if(listInputs == null){
                    listInputs = new ArrayList<>();
                    try { listInputs.add(new ResourceStack(genericRes, 1,2)); } catch (FullResourceStackException e) { e.printStackTrace(); }
                }
                else{
                    if(genericRes == listInputs.get(0).getResourceType()){
                        try { listInputs.get(0).setAmount(2); } catch (FullResourceStackException e) { e.printStackTrace(); }
                    }
                    else{
                        try { listInputs.add(new ResourceStack(genericRes, 1,2)); } catch (FullResourceStackException e) { e.printStackTrace(); }
                    }
                }
            }
        }
        //If DevCard power is requested
        else if(semanticallyCorrectInput.getFirst() == 'B' || semanticallyCorrectInput.getFirst() == 'C' || semanticallyCorrectInput.getFirst() == 'D' ){
            DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(chooseProductionMap.get(semanticallyCorrectInput.getFirst()));
            listInputs=developmentCard.getProductionPower().getRequirements();
        }
        //If LeaderCard power is requested
        else{
            LeaderCard leaderCard=CardRepository.getInstance().getLeaderCardByID(chooseProductionMap.get(semanticallyCorrectInput.getFirst()));
            listInputs=((ProductionPower) leaderCard.getPower()).getRequirements();
        }

        //Check if the player has enough resources
        for(ResourceStack resourceStack : listInputs){
            if(resourceStack.getAmount() > getTotalAmountOfResources(resourceStack.getResourceType(), compressedModel)) return false;
        }
        return true;
    }


    /**
     * Check if the player is trying to activate an already selected power
     * @param semanticallyCorrectInput the input already corrected
     * @return if the selected production is actionable or not
     */
    private boolean checkIfActionable(Triplet<Character, ArrayList<Resource>, ArrayList<Resource>> semanticallyCorrectInput) {
        if (semanticallyCorrectInput.getFirst() == 'Q') {
            return true;
        }
        return chooseProductionMap.containsKey(semanticallyCorrectInput.getFirst());
    }


    /**
     * Transform userInput in a machine-readable input and check for semantically errors
     * @return checked user input
     */
    private Triplet<Character, ArrayList<Resource>, ArrayList<Resource>> askSemanticallyCorrectInput() {
        String[] correctResourceInput = {"COIN", "SERVANT", "SHIELD", "STONE"};
        boolean isInputCorrect = false;
        ArrayList<Resource> inResources = new ArrayList<>();
        ArrayList<Resource> outResources = new ArrayList<>();
        String userInput;
        Triplet<Character, ArrayList<Resource>, ArrayList<Resource>> output = null;
        input = new Scanner(System.in);
        do{
            System.out.println("Write your choice here \n");

            if(isFirstProduction)
                System.out.println(GameConstants.GO_BACK_MESSAGE);
            userInput = input.nextLine().toUpperCase();

            if(userInput.equals("BACK"))
            {
                if(isFirstProduction) return null;
                else
                {
                    System.out.println(GameConstants.PRODUCTION_SCENE_CANT_GO_BACK);
                    continue;
                }
            }
            else if(!userInput.contains("-")){
                if(userInput.equals("END")){
                    isInputCorrect = true;
                    output = new Triplet<>('Q',inResources, outResources);
                }
                else if(userInput.equals("B") || userInput.equals("C") || userInput.equals("D")){
                    isInputCorrect = true;
                    output = new Triplet<>(userInput.charAt(0),inResources, outResources);
                }
            }
            else{
                String[] splits = userInput.split("-");
                if(splits.length == 4 && splits[0].equals("A") && Arrays.asList(correctResourceInput).contains(splits[1]) && Arrays.asList(correctResourceInput).contains(splits[2]) && Arrays.asList(correctResourceInput).contains(splits[3])){
                    isInputCorrect = true;
                    inResources.add(stringToResource(splits[1]));
                    inResources.add(stringToResource(splits[2]));
                    outResources.add(stringToResource(splits[3]));
                    output = new Triplet<>(splits[0].charAt(0),inResources, outResources);
                }
                else if((splits[0].equals("E") || splits[0].equals("F")) && Arrays.asList(correctResourceInput).contains(splits[1])){
                    isInputCorrect = true;
                    outResources.add(stringToResource(splits[1]));
                    output = new Triplet<>(splits[0].charAt(0),inResources, outResources);
                }
            }
            if(!isInputCorrect){
                System.out.println("SYNTAX ERROR \n");
            }
        }
        while(!isInputCorrect);
        return output;
    }


    /**
     * Transform a string containing a resource name in a Resource type object
     * @param res the string resource
     * @return the linked Resource type
     */
    private Resource stringToResource(String res) {
        String buffer = res.toLowerCase();
        switch (buffer){
            case "coin":
                return Resource.COIN;
            case "servant":
                return Resource.SERVANT;
            case "shield":
                return Resource.SHIELD;
            case "stone":
                return Resource.STONE;
            default:
                return null;
        }
    }


    /**
     * Background-Filler helper for leaderCards
     * @param list list of leaderCards
     * @param arrayToPrint the arrayToPrint that needs to be modified
     * @param countLeadCard the leaderCardCount
     * @param isInput input or output resources
     * @param isActivable is the leaderCard already activable?
     * @return the new arrayToPrint
     */
    private String[] leadCardINnOUT(List<ResourceStack> list, String[] arrayToPrint, int countLeadCard, boolean isInput, boolean isActivable){
        int countresources=0;
        for(ResourceStack r:list){
            int quantity=r.getAmount();
            Resource resource=r.getResourceType();
            String toprint=resource.toString()+" "+ quantity;
            if(!resource.equals(Resource.GENERIC)) {
                arrayToPrint = substituteLineBackground(arrayToPrint, toprint, isInput ? BOXLEADCARD_INPUTS_ROW[countLeadCard] + countresources : BOXLEADCARD_OUT_ROW[countLeadCard] + countresources, BOXLEADCARD_COL);
                countresources++;
            }
        }
        if(isInput && !isActivable){
            arrayToPrint = substituteLineBackground(arrayToPrint, "DONE", BOXLEADCARD_STATE_ROW[countLeadCard], BOXLEADCARD_COL+3);
        }
        return arrayToPrint;
    }


    /**
     * Background-Filler helper for devCards
     * @param list the list of devCards
     * @param arrayToPrint the input arrayToPrint
     * @param deckCount the number of deck
     * @param isInput are the input resource?
     * @param isActivable is the card to draw actionable?
     * @return the new arrayToPrint
     */
    private String[] devCardRequRew(List<ResourceStack> list, String[] arrayToPrint, int deckCount , boolean isInput, boolean isActivable){
        int resourceLineCount = 0;
        for( ResourceStack resource : list){
            String stringToWrite = resource.getResourceType().toString()+ "  " + resource.getAmount();
            arrayToPrint = substituteLineBackground(arrayToPrint, stringToWrite, isInput ? BOXDEVCARD_INPUTS_ROW+resourceLineCount : BOXDEVCARD_OUTPUT_ROW+resourceLineCount , BOXDEVCARD_COLUMNS[deckCount]);
            resourceLineCount++;
        }
        if(isInput && !isActivable){
            arrayToPrint = substituteLineBackground(arrayToPrint, "DONE", BOXDEVCARD_STATE_ROW, BOXDEVCARD_COLUMNS[deckCount]+3);
        }
        return  arrayToPrint;
    }


    /**
     * choseProductionMap filler helper
     * @param cardID the ID of the card that needs to be drawn
     * @param count the count
     * @param isDevCard is the card a devCard?
     */
    private void fillMap(int cardID, int count, boolean isDevCard){
        switch (count){
            case -1:
                chooseProductionMap.put('A', -1);
                break;
            case 0:
                chooseProductionMap.put(isDevCard ? 'B' : 'E',cardID);
                break;
            case 1:
                chooseProductionMap.put(isDevCard ? 'C' : 'F',cardID);
                break;
            case 2:
                chooseProductionMap.put('D',cardID);
                break;
        }
    }


}
