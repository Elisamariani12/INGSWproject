package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.client.exceptions.CLIWriterException;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CLI scene that lets the user do a Market move
 */
public class MarketScene extends TextScene {
    private static final String background =
            "                             ╔╦╗┌─┐┬─┐┬┌─┌─┐┌┬┐\n" +
            "                             ║║║├─┤├┬┘├┴┐├┤  │\n" +
            "                             ╩ ╩┴ ┴┴└─┴ ┴└─┘ ┴\n" +
            "\n" +
            "     ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "     █      4     5     6     7              YOUR STORAGES:               █\n" +
            "     █   ╔═════╦═════╦═════╦═════╗                                        █\n" +
            "     █ 1 ║     ║     ║     ║     ║  Warehouse depot:    StrongBox:        █\n" +
            "     █   ║     ║     ║     ║     ║  -coins:             -coins:           █\n" +
            "     █   ╠═════╬═════╬═════╬═════╣  -servants:          -servants:        █\n" +
            "     █ 2 ║     ║     ║     ║     ║  -shields:           -shields:         █\n" +
            "     █   ║     ║     ║     ║     ║  -stones:            -stones:          █\n" +
            "     █   ╠═════╬═════╬═════╬═════╣                                        █\n" +
            "     █ 3 ║     ║     ║     ║     ║  Leader cards resources:               █\n" +
            "     █   ║     ║     ║     ║     ║  -inserted:                            █\n" +
            "     █   ╚═════╩═════╩═════╩═════╝  -insertable:                          █\n" +
            "     █                                                                    █\n" +
            "     █  s: stone    y: servant      Type:                                 █\n" +
            "     █  c: coin     x: shield       number next to the row/column         █\n" +
            "     █  f: faith                                                          █\n" +
            "     █                                                                    █\n" +
            "     █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█";

    private static final int[] marblesRows = {7,10,13};
    private static final int[] marblesColumns = {13,19,25,31};

    private static final int[] wareHouseANDStrongBoxRows = {8,9,10,11};
    private static final int wareHouseColumns = 48;
    private static final int strongBoxColumns = 68;

    private static final int[] leaderCardResourcesRows ={14,15};
    private static final int leaderCardResourcesColumns = 50;

    private static final int GO_BACK_VALUE = -12;

    private Scanner input;


    @Override
    public void draw(CompressedModel compressedModel) {
        boolean hasDiscount = false;
        String[] backgroundArray;

        backgroundArray = toArrayFromString(background);

        //FILL MARKET
        ArrayList<Resource> whiteMarbleSubstituition = retrieveWhiteMarbleSubstitution(compressedModel.getAllPlayersActiveLeaderCards().get(compressedModel.getaP()));
        if(whiteMarbleSubstituition != null){
            hasDiscount = true;
        }

        backgroundArray = fillMarket(compressedModel.getMarketState(), hasDiscount, whiteMarbleSubstituition, backgroundArray);

        //FILL STORAGES
        //warehouse depot
        //Zero-empty correction
        for( int row : wareHouseANDStrongBoxRows ){
           backgroundArray = substituteLineBackground(backgroundArray, String.valueOf(0), row, wareHouseColumns);
        }

        List<ResourceStack> iter1 = Optional.ofNullable(compressedModel.getaPWD()).orElse(Collections.emptyList());
        for( ResourceStack wdRes: iter1){
            backgroundArray = fillWHandSB(backgroundArray, wdRes, true);
        }

        //strongbox
        List<ResourceStack> iter2 = Optional.ofNullable(compressedModel.getaPSB()).orElse(Collections.emptyList());
        for( ResourceStack sbRes: iter2 ){
            backgroundArray = fillWHandSB(backgroundArray, sbRes, false);
        }

        //leader card storage
        StringBuilder inserted = new StringBuilder();
        StringBuilder insertable = new StringBuilder();

        List<ResourceStack> iter3 = Optional.ofNullable(compressedModel.getaPLeaderStorage()).orElse(Collections.emptyList());
        for(ResourceStack leadCardResources : iter3 ){
            inserted.append(leadCardResources.getResourceType().toString()).append(" : ").append(leadCardResources.getAmount()).append("  ");
            insertable.append(leadCardResources.getResourceType().toString()).append(" : ").append(leadCardResources.getMaxSize() - leadCardResources.getAmount()).append("  ");
        }
        backgroundArray = substituteLineBackground(backgroundArray, inserted.toString(), leaderCardResourcesRows[0], leaderCardResourcesColumns);
        backgroundArray = substituteLineBackground(backgroundArray, insertable.toString(), leaderCardResourcesRows[1], leaderCardResourcesColumns);

        System.out.println(toStringFromArray(backgroundArray));

    }


    @Override
    public void askInput(CompressedModel compressedModel) {
        //Input from the user
        int rowColSelection;
        //Necessary elements to fill PlayerEvent
        int row;
        int column;
        boolean hasDiscarded;
        ArrayList<Resource> pickedResources;
        ArrayList<Resource> discardedResources;
        HashMap<Resource,Integer> whiteMarbleResource;
        int faithTrack = 0;

        //User's row n col market selection
        input = new Scanner(System.in);
        rowColSelection = selectMarketElements(input);
        if(rowColSelection == GO_BACK_VALUE)
        {
            if(compressedModel.getPlayerMoveResponse()!=PlayerMoveResponse.REJECTED){registerGoBackRequest();return;}
            else{System.out.println("Since you already did a market action (rejected) you can not go back now");askInput(compressedModel);return;}
        }

        //Pick corresponding resources and ask for WhiteMarble ability
        pickedResources = marketPicker(compressedModel, rowColSelection);
        faithTrack = faithTrackCounter(pickedResources);
        pickedResources = faithTrackRemover(pickedResources);

        whiteMarbleResource = whiteMarbleResolver(compressedModel, pickedResources);
        pickedResources = whiteMarbleExecutor(pickedResources, whiteMarbleResource);

        //ask which resources the player wants to discard and MODIFY also whiteMarbleResource map
        discardedResources = discardAction(pickedResources, whiteMarbleResource);

        hasDiscarded = discardedResources.size() > 0;

        //compose PlayerEvent elements
        if(rowColSelection < 4){
            row = rowColSelection - 1;
            column = -1;
        }
        else{
            row = -1;
            column = rowColSelection - 4;
        }
        PlayerWorkSpace.getInstance().registerMarketAction(row, column, whiteMarbleResource, hasDiscarded, discardedResources);

    }


    //----------------------
    // ASK_INPUT HELPERS----
    //----------------------

    /**
     * Remove faith resource from to-discard list
     * @param pickedResources the pickedResources
     * @return the picked resources excluding the faith resource
     */
    private ArrayList<Resource> faithTrackRemover(ArrayList<Resource> pickedResources) {
        ArrayList<Resource> newPick = new ArrayList<>();

        for(Resource res: pickedResources){
            if (res != Resource.FAITH){
                newPick.add(res);
            }
        }
        return newPick;
    }

    /**
     * Count faith picked resources
     *
     * @param pickedResources the picked resources
     * @return the count of positions to advance in the faithTrack
     */
    private int faithTrackCounter(ArrayList<Resource> pickedResources) {
        int count = 0;
        for(Resource res : pickedResources){
            if(res == Resource.FAITH){
                count++;
            }
        }
        return count;
    }


    /**
     * Asks, for each picked resource, if the player wants to store or throw it
     * @param pickedResources the picked resources
     * @return the discarded resources
     */
    private ArrayList<Resource> discardAction(ArrayList<Resource> pickedResources, HashMap<Resource,Integer> whiteMarbleResource) {
        boolean isInputCorrect = false;
        ArrayList<Resource> discardedResources = new ArrayList<>();
        for (Resource res : pickedResources){
            isInputCorrect = false;
            do{
                System.out.println("Do you want to keep this resource : " + res.toString() + " ? Type Y or N");
                input = new Scanner(System.in);
                String response = input.nextLine();
                if(response.equals("Y") || response.equals("y") || response.equals("yes")){
                    isInputCorrect = true;
                }
                else if(response.equals("N") || response.equals("n") || response.equals("no")){
                    isInputCorrect = true;
                    if(whiteMarbleResource != null && whiteMarbleResource.containsKey(res) && whiteMarbleResource.get(res)>0){
                        whiteMarbleResource.put(res, whiteMarbleResource.get(res)-1);
                    }
                    else{
                        discardedResources.add(res);
                    }
                }
                else{
                    System.out.println("SYNTAX ERROR! \n");
                    isInputCorrect = false;
                }
            }while(!isInputCorrect);
        }

        return discardedResources;
    }

    /**
     * Retrieves infos about player's substitution leader Cards and about what to do with it
     * @param compressedModel the compress model received
     * @param resources that might be transformed thanks to the white marble substitution ability
     * @return substituted resources
     */
    private HashMap<Resource,Integer> whiteMarbleResolver(CompressedModel compressedModel, ArrayList<Resource> resources ) {
        HashMap<Resource,Integer> substitution = new HashMap<>();
        //Collect player's substitution ability
        ArrayList<Resource> whiteMarbleSubstitution = retrieveWhiteMarbleSubstitution(compressedModel.getAllPlayersActiveLeaderCards().get(compressedModel.getaP()));
        long countOfWhiteMarbles = resources.stream().filter(x -> x== Resource.WHITE).count();
        boolean isInputCorrect = false;
        //If the player has no WhiteMarbleSub power
        if(whiteMarbleSubstitution == null){
            return null;
        }

        //If the player has one leader card with WhiteMarbleSub power
        else if(whiteMarbleSubstitution.size() == 1){
            substitution.put(whiteMarbleSubstitution.get(0), ((int) countOfWhiteMarbles));
            return substitution;
        }

        //If the player has two leader card with WhiteMarbleSub power
        else if(whiteMarbleSubstitution.size() == 2){
            int chosenSubst;
            int numberToSub;
            do{
                System.out.println("Select WhiteMarbleSubstitution: " + "type 1 for "+ whiteMarbleSubstitution.get(0) + " or 2 for " + whiteMarbleSubstitution.get(1) +"or 3 for both\n");
                chosenSubst =input.nextInt();
                //if he choose just one card
                if(chosenSubst > 0 && chosenSubst<3){
                    isInputCorrect = true;
                    substitution.put(whiteMarbleSubstitution.get(chosenSubst-1), ((int) countOfWhiteMarbles));
                }
                //if he choose to use both his cards
                if(chosenSubst == 3){
                    System.out.println("Select number of white murbles that has to substitute with " + whiteMarbleSubstitution.get(0) + " others will be " + whiteMarbleSubstitution.get(1)+ "\n");
                    numberToSub = input.nextInt();
                    if(numberToSub<= countOfWhiteMarbles){
                        isInputCorrect = true;
                        substitution.put(whiteMarbleSubstitution.get(0), ((int) numberToSub));
                        substitution.put(whiteMarbleSubstitution.get(1), ((int) (countOfWhiteMarbles - numberToSub)));
                    }
                }
                else{
                    System.out.println("SYNTAX ERROR! \n");
                }
            }while(!isInputCorrect);
            return substitution;
        }

        throw new CLIWriterException();
    }


    /**
     * Substitute white marbles with chosen resources
     * @param resources the chosen resources
     * @param whiteMarbleMap a map containing white marble substitution abilities
     * @return the updated chosen resource list
     */
    private ArrayList<Resource> whiteMarbleExecutor(ArrayList<Resource> resources, Map<Resource, Integer> whiteMarbleMap){
        //If player has no substitution ability
        if(whiteMarbleMap == null){
            return  resources.stream().filter(x -> x != Resource.WHITE).collect(Collectors
                    .toCollection(ArrayList::new));
        }
        //Else
        ArrayList<Pair<Resource, Integer>> whiteMarbleList = new ArrayList<>();
        whiteMarbleMap.forEach((x,y) -> whiteMarbleList.add(new Pair<>(x, y)));
        //If player has chosen just one ability
        if( whiteMarbleList.size() == 1){
            return resources.stream()
                    .map(res -> res == Resource.WHITE ? whiteMarbleList.get(0).getFirst() : res)
                    .collect(Collectors
                            .toCollection(ArrayList::new));
        }
        //If player has chosen two ability and their weights
        else{
            ArrayList<Resource> modifiedChosenRes = new ArrayList<>();
            int firstMarbleCounter = whiteMarbleList.get(0).getSecond();
            for(Resource res : resources){
                if(res == Resource.WHITE){
                    if(firstMarbleCounter > 0){
                        modifiedChosenRes.add(whiteMarbleList.get(0).getFirst());
                        firstMarbleCounter--;
                    }
                    else{
                        modifiedChosenRes.add(whiteMarbleList.get(1).getFirst());
                    }
                }
                else{
                    modifiedChosenRes.add(res);
                }
            }
            return modifiedChosenRes;
        }
    }

    /**
     * Picks resources from the market
     * @param compressedModel the received compressModel
     * @param rowColSelection that is the row or the cols selected
     * @return the picked resources
     */
    private ArrayList<Resource> marketPicker(CompressedModel compressedModel, int rowColSelection) {
        Resource[][] market = compressedModel.getMarketState();
        ArrayList<Resource> pickedResources = new ArrayList<>();
        if(rowColSelection < 4){
            pickedResources.addAll(Arrays.asList(market[rowColSelection - 1]).subList(0, 4));
        }
        else{
            for(int i=0; i<3; i++){
                pickedResources.add(market[i][rowColSelection-4]);
            }
        }
        return pickedResources;
    }

    /**
     * Ask which row/col the player wants to pick from the market
     * @param input of the row or col
     * @return selected row or col
     */
    private int selectMarketElements(Scanner input) {
        boolean isInputCorrect = false;
        String strBuffer;
        int selected = -1;
        do{
            System.out.println("Insert column or row as a NUMBER, ex: 1");
            System.out.println(GameConstants.GO_BACK_MESSAGE);
            strBuffer = input.nextLine();

            if(strBuffer.equalsIgnoreCase("BACK")) return GO_BACK_VALUE;
            else if (StringUtils.isDigitString(strBuffer))
            {
                selected = Integer.valueOf(strBuffer).intValue();
                if(selected >0 && selected < 8){
                    isInputCorrect = true;
                }
                else{
                    System.out.println("\nSYNTAX ERROR! \n");
                }
            }
            else if (!strBuffer.isBlank())
            {
                System.out.println("\nSYNTAX ERROR! \n");
            }

        }while(!isInputCorrect);
        return selected;
    }


    //-----------
    // DRAW HELPERS----
    //-----------

    /**
     * FIll resources in the market with White_Marbles substitutions
     * @param marketState the market actual state
     * @param hasDiscount tells if the player has any discount
     * @param substitutions for each white marble substitution, the reward
     * @param background the initial background that has to be modified
     * @return final backgroun
     */
    private String[] fillMarket(Resource[][] marketState, boolean hasDiscount, ArrayList<Resource> substitutions, String[] background) {
        int rowsCount = 0;
        int colCount = 0;
        for(Resource[] row : marketState){
            colCount = 0;
            for(Resource resource : row){
                if(resource == Resource.WHITE){
                    if(hasDiscount && substitutions.size() == 1){
                        background = fillMarketHelper(substitutions.get(0), background, marblesRows[rowsCount], marblesColumns[colCount]);
                    }
                    else if(hasDiscount && substitutions.size() == 2){
                        background = fillMarketHelper(substitutions.get(0), background, marblesRows[rowsCount], marblesColumns[colCount]);
                        background = fillMarketHelper(substitutions.get(1), background, marblesRows[rowsCount] +1, marblesColumns[colCount]);
                    }
                }
                else{
                    background = fillMarketHelper(resource, background, marblesRows[rowsCount], marblesColumns[colCount]);
                }
                colCount++;
            }
            rowsCount++;
        }
        return background;
    }


    /**
     * FillMarket helper method used to resource to char conversion
     * @param resource the resource that has to be inserted
     * @param background initial background
     * @param row the row where the resource needs to be inserted
     * @param col the column where the resource needs to be inserted
     * @return the final background
     */
    private String[] fillMarketHelper(Resource resource, String[] background,int row, int col){
        switch (resource){
            case SHIELD:
                return substituteLineBackground(background, "X", row, col);

            case STONE:
                return substituteLineBackground(background, "S", row, col);

            case COIN:
                return substituteLineBackground(background, "C", row, col);

            case SERVANT:
                return substituteLineBackground(background, "Y", row, col);

            case FAITH:
                return substituteLineBackground(background, "F", row, col);

            default:
                throw new CLIWriterException();
        }
    }

    /**
     * check if there are white marble substitutions from the active leaderCards, if yes return them
     * @param activeCards the active leader Cards owned by the player
     * @return the white marble substitution ability owned by the player
     */
    private ArrayList<Resource> retrieveWhiteMarbleSubstitution(List<Integer> activeCards){
        ArrayList<Resource> substituteResources = new ArrayList<>();
        for(Integer cardID: activeCards){
            LeaderCard card = CardRepository.getInstance().getLeaderCardByID(cardID);
            if(card.getPower().getSpecialAbilityType() == SpecialAbility.WHITE_MARBLE_SUBSTITUTION){
                AbilityPower discountPower = (AbilityPower) card.getPower();
                substituteResources.add(discountPower.getResourceType());
            }
        }
        if(substituteResources.size()>0){
            return substituteResources;
        }
        return null;
    }

    /**
     * FIll deposits
     * @param background the initial background
     * @param res the resource stack that needs to be inserted in the deposits
     * @param isWH has warehouse depot filled? If false Strong box needs to be filled
     * @return final background string
     */
    private String[] fillWHandSB(String[] background, ResourceStack res, boolean isWH){
        if(res == null || res.getResourceType() == null){
            return background;
        }
        switch (res.getResourceType()){
            case COIN:
                if(isWH){
                   return substituteLineBackground(background, String.valueOf(res.getAmount()), wareHouseANDStrongBoxRows[0], wareHouseColumns);
                }
                else{
                    return substituteLineBackground(background, String.valueOf(res.getAmount()), wareHouseANDStrongBoxRows[0], strongBoxColumns);
                }

            case SERVANT:
                if(isWH){
                    return substituteLineBackground(background, String.valueOf(res.getAmount()), wareHouseANDStrongBoxRows[1], wareHouseColumns);
                }
                else{
                    return substituteLineBackground(background, String.valueOf(res.getAmount()), wareHouseANDStrongBoxRows[1], strongBoxColumns);
                }
            case SHIELD:
                if(isWH){
                    return substituteLineBackground(background, String.valueOf(res.getAmount()), wareHouseANDStrongBoxRows[2], wareHouseColumns);
                }
                else{
                    return substituteLineBackground(background, String.valueOf(res.getAmount()), wareHouseANDStrongBoxRows[2], strongBoxColumns);
                }

            case STONE:
                if(isWH){
                    return substituteLineBackground(background, String.valueOf(res.getAmount()), wareHouseANDStrongBoxRows[3], wareHouseColumns);
                }
                else{
                    return substituteLineBackground(background, String.valueOf(res.getAmount()), wareHouseANDStrongBoxRows[3], strongBoxColumns);
                }
            default:
                throw new CLIWriterException();
        }
    }
}
