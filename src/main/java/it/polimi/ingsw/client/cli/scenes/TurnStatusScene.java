package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.client.exceptions.CLIWriterException;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.common.util.LeaderCardStatus;
import it.polimi.ingsw.common.util.Pair;
import it.polimi.ingsw.common.util.ResourceStack;

import java.util.*;

/**
 *CLI scene that show the situation of the active player
 */
public class TurnStatusScene extends TextScene {
    private static final String background =
            "                     ╦┌┬┐|┌─┐  ┬ ┬┌─┐┬ ┬┬─┐  ┌┬┐┬ ┬┬─┐┌┐┌  │\n" +
            "                     ║ │  └─┐  └┬┘│ ││ │├┬┘   │ │ │├┬┘│││  │\n" +
            "                     ╩ ┴  └─┘   ┴ └─┘└─┘┴└─   ┴ └─┘┴└─┘└┘  o\n" +
            "\n" +
            "    ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "    █                                                                    █\n" +
            "    █ Your lead.cards: ╔════════════╗╔════════════╗ Your Resources:      █\n" +
            "    █                  ║pts:        ║║pts:        ║ -coins:              █\n" +
            "    █                  ║            ║║            ║ -shields:            █\n" +
            "    █                  ║            ║║            ║ -servants:           █\n" +
            "    █                  ║            ║║            ║ -stones:             █\n" +
            "    █ Your dev.cards:  ╚════════════╝╚════════════╝                      █\n" +
            "    █ ╔════════════╗╔════════════╗╔════════════╗                         █\n" +
            "    █ ║Lvl:        ║║Lvl:        ║║Lvl:        ║  Choose your move,type: █\n" +
            "    █ ║In:         ║║In:         ║║In:         ║  1->market              █\n" +
            "    █ ║            ║║            ║║            ║  2->buy dev.cards       █\n" +
            "    █ ║            ║║            ║║            ║  3->activate production █\n" +
            "    █ ║            ║║            ║║            ║                         █\n" +
            "    █ ║Out:        ║║Out:        ║║Out:        ║                         █\n" +
            "    █ ║            ║║            ║║            ║                         █\n" +
            "    █ ║            ║║            ║║            ║                         █\n" +
            "    █ ║            ║║            ║║            ║                         █\n" +
            "    █ ╚════════════╝╚════════════╝╚════════════╝                         █\n" +
            "    █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█";

    private static final int leadCardStateRow = 6;
    private static final int[] leaCardStateColumns = {28, 42};
    private static final int leadCardPointsRow = 7;
    private static final int[] leaCardPointsColumns = {29, 43};
    private static final int leadCardAbilityRow = 9;
    private static final int[] leaCardAbilityColumns = {25, 39};

    private static final int resourcesColumn = 64;
    private static final int[] resourcesRows = {7, 8, 9, 10};

    private static final int devCardLvlRow = 13;
    private static final int[] devCardLvlColumn = {12, 26, 40};
    private static final int devCardINRow = 15;
    private static final int[] devCardINColumn = {8, 22, 36};
    private static final int devCardOUTRow = 19;
    private static final int[] devCardOUTColumn = {8, 22, 36};

    private TextScene sceneToDraw;
    private Scanner input;

    @Override
    public void draw(CompressedModel compressedModel) {
        TextScene sceneToDraw = findRightScene(compressedModel);
        checkRejectedMove(compressedModel);
        if(sceneToDraw != this){
            sceneToDraw.draw(compressedModel);
        }
        else{
            String[] backgroundArray = toArrayFromString(background);

            //LEADER CARD SETTER
            ArrayList<Pair<LeaderCard, LeaderCardStatus>> leaderCards = new ArrayList<>();
            List<Integer> iter = Optional.ofNullable(compressedModel.getaPHiddenChosenLeaderCards()).orElse(Collections.emptyList());
            for(int cardID : iter){
                leaderCards.add(new Pair<>(CardRepository.getInstance().getLeaderCardByID(cardID), LeaderCardStatus.CHOSEN));
            }

            List<Integer> iter2 = Optional.ofNullable(compressedModel.getAllPlayersActiveLeaderCards().get(compressedModel.getaP())).orElse(Collections.emptyList());
            for(int cardID : iter2){
                leaderCards.add(new Pair<>(CardRepository.getInstance().getLeaderCardByID(cardID), LeaderCardStatus.IN_USE));
            }

            for(int i=0;i<leaderCards.size(); i++){
                if(leaderCards.get(i).getSecond() == LeaderCardStatus.IN_USE){
                    backgroundArray = substituteLineBackground(backgroundArray, "IN USE", leadCardStateRow, leaCardStateColumns[i]);
                }
                else{
                    backgroundArray = substituteLineBackground(backgroundArray, "CHOSEN", leadCardStateRow, leaCardStateColumns[i]);
                }
                backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(leaderCards.get(i).getFirst().getVictoryPoints()),leadCardPointsRow, leaCardPointsColumns[i]);
                backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(leaderCards.get(i).getFirst().getPower().printAbility()),leadCardAbilityRow, leaCardAbilityColumns[i]);
            }

            //RESOURCES COUNTER SETTER
            for(ResourceStack resources : compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP())){
                switch (resources.getResourceType()){
                    case COIN:
                        backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(resources.getAmount()),resourcesRows[0], resourcesColumn);
                        break;
                    case SHIELD:
                        backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(resources.getAmount()),resourcesRows[1], resourcesColumn);
                        break;
                    case SERVANT:
                        backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(resources.getAmount()),resourcesRows[2], resourcesColumn);
                        break;
                    case STONE:
                        backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(resources.getAmount()),resourcesRows[3], resourcesColumn);
                        break;
                    default:
                        throw new CLIWriterException();
                }
            }

            //DEV CARDS SETTER
            int deckCount =0;
            for( Stack<Integer> deck: compressedModel.getDevCardSpace().get(compressedModel.getaP())){
                if(deck.size() == 0) break;
                DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(deck.peek());
                backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(developmentCard.getCardLevel()),devCardLvlRow, devCardLvlColumn[deckCount]);
                int resourceLineCount = 0;
                for( ResourceStack resource : developmentCard.getProductionPower().getRequirements()){
                    String stringToWrite = resource.getResourceType().toString()+ "  " + String.valueOf(resource.getAmount());
                    backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(stringToWrite), devCardINRow+resourceLineCount, devCardINColumn[deckCount]);
                    resourceLineCount++;
                }
                resourceLineCount = 0;
                for( ResourceStack resource : developmentCard.getProductionPower().getReward()){
                    String stringToWrite = resource.getResourceType().toString()+ "  " + String.valueOf(resource.getAmount());
                    backgroundArray = substituteLineBackground(backgroundArray,String.valueOf(stringToWrite), devCardOUTRow+resourceLineCount, devCardOUTColumn[deckCount]);
                    resourceLineCount++;
                }
                deckCount++;
            }

            System.out.println(toStringFromArray(backgroundArray));
        }


    }

    @Override
    public void askInput(CompressedModel compressedModel) {
        String chosen;
        TextScene sceneToDraw = findRightScene(compressedModel);

        if(sceneToDraw != this){
            sceneToDraw.askInput(compressedModel);
        }
        else{
            System.out.println("Enter here your choice: 1,2 or 3");
            chosen = validateInput();
            executeInput(chosen, compressedModel);

        }

    }

    /**
     * Analize the compressed model and show the right scene to the player
     * @param compressedModel the actual compressed model
     * @return the right scene
     */
    private TextScene findRightScene(CompressedModel compressedModel){
        switch(compressedModel.getTurnState()){
            case CHOOSE_ACTION:
                return this;

            case MARKET:
                return new MarketScene();

            case DEV_CARD_PURCHASE:
                return new DevCardPurchaseScene();

            case PRODUCTION_PHASE:
                return new ProductionScene();

            default:
                throw new CLIWriterException();
        }
    }

    /**
     * Validate the input of the player
     *
     * @return validated choice
     */
    private String validateInput(){
        input = new Scanner(System.in);
        boolean correctInput = false;
        String choice;
        do {
            choice = input.nextLine();
            if(choice.equals("1") || choice.equals("2") || choice.equals("3")){
              correctInput = true;
            }
            else{
                System.out.println("SYNTAX ERROR! \n" + "Enter here your choice: 1,2 or 3");
            }
        }while(!correctInput);
        return choice;
    }

    /**
     * Update the socket of this serversideProtocol for reconnection
     *
     * @param input the input of the player
     * @param compressedModel the updated compressed model
     */
    private void executeInput(String input, CompressedModel compressedModel ){
        TextScene scene;
        switch (input){
            case "1":
                scene = new MarketScene();
                break;
            case "2":
                scene = new DevCardPurchaseScene();
                break;
            case "3":
                scene = new ProductionScene();
                break;
            default:
                scene= new MarketScene();
                throw new CLIWriterException();
        }

        scene.draw(compressedModel);
        scene.askInput(compressedModel);

        //If the player requests to go back to the previous scene
        if(scene.getGoBackRequest())
        {
            this.draw(compressedModel);
            this.askInput(compressedModel);
        }

    }
}
