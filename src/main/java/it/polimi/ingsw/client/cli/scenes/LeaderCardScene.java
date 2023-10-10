package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.client.exceptions.CompressedModelInconsistencyException;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CLI scene used to let the user do a initial/final leaderAction
 */
public class LeaderCardScene extends TextScene {
    private static final String background =
            "            ╔═╗┌─┐┌┬┐┬┬  ┬┌─┐┌┬┐┌─┐  ┬  ┌─┐┌─┐┌┬┐┌─┐┬─┐  ┌─┐┌─┐┬─┐┌┬┐\n" +
            "            ╠═╣│   │ │└┐┌┘├─┤ │ ├┤   │  ├┤ ├─┤ ││├┤ ├┬┘  │  ├─┤├┬┘ ││\n" +
            "            ╩ ╩└─┘ ┴ ┴ └┘ ┴ ┴ ┴ └─┘  ┴─┘└─┘┴ ┴─┴┘└─┘┴└─  └─┘┴ ┴┴└──┴┘\n" +
            "     ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "     █                                                                    █\n" +
            "     █        1                 2                                         █\n" +
            "     █ ╔══════════════╗  ╔══════════════╗      ACTIONS:                   █\n" +
            "     █ ║pts:          ║  ║pts:          ║       A -> activate card        █\n" +
            "     █ ║type:         ║  ║type:         ║       D -> discard card         █\n" +
            "     █ ║              ║  ║              ║                                 █\n" +
            "     █ ║              ║  ║              ║                                 █\n" +
            "     █ ║              ║  ║              ║    Type action-number to do     █\n" +
            "     █ ║Requirements: ║  ║Requirements: ║    the action.                  █\n" +
            "     █ ║              ║  ║              ║                                 █\n" +
            "     █ ║              ║  ║              ║                                 █\n" +
            "     █ ║              ║  ║              ║                                 █\n" +
            "     █ ╚══════════════╝  ╚══════════════╝                                 █\n" +
            "     █ state:            state:                                           █\n" +
            "     █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█";


    //Victory points coordinates
    private final int CARD_VP_ROW = 7;
    private final int CARD1_VP_LINE_POS = 14;
    private final int CARD2_VP_LINE_POS = 32;

    //Type coordinates
    private final int CARD_TYPE_ROW = 8;
    private final int CARD1_TYPE_LINE_POS = 14;
    private final int CARD2_TYPE_LINE_POS = 32;
    private final int CARD_POWER_ROW = 9;

    //Card state coordinates
    private final int CARD_STATUS_ROW = 17;
    private final int CARD1_STATUS_LINE_POS = 14;
    private final int CARD2_STATUS_LINE_POS = 32;

    //Card requirements
    private final int CARD_REQ_START_ROW = 13;
    private final int CARD1_REQ_START_LINE_POS = 9;
    private final int CARD2_REQ_START_LINE_POS = 27;

    //Buffers for received model values
    private List<LeaderCard> chosenLeaderCards;
    private List<LeaderCardStatus> cardStatuses;

    private Scanner scanner;

    /** Creates a new LeaderCardScene
     */
    public LeaderCardScene()
    {
        super();
        scanner = new Scanner(System.in);
    }

    /** Used to get corresponding cards from repository
     * @param compressedModel Compressed model from server
     * @throws CompressedModelInconsistencyException when there are more than 2 chosen cards
     */
    private void procureCards(CompressedModel compressedModel)
    {
        List<Integer> activeCardIDs = compressedModel.getAllPlayersActiveLeaderCards().get(/* active player's */compressedModel.getaP());
        List<Integer> hiddenCardIDs = compressedModel.getaPHiddenChosenLeaderCards();

        chosenLeaderCards = new ArrayList<>();
        cardStatuses = new ArrayList<>();

        //Add cards along with their status
        for(Integer id : activeCardIDs)
        {
            chosenLeaderCards.add(CardRepository.getInstance().getLeaderCardByID(id));
            cardStatuses.add(LeaderCardStatus.IN_USE);
        }

        for(Integer id : hiddenCardIDs)
        {
            chosenLeaderCards.add(CardRepository.getInstance().getLeaderCardByID(id));
            cardStatuses.add(LeaderCardStatus.CHOSEN);
        }

        //There should never be more than 2 cards in the chosen deck
        if(chosenLeaderCards.size() > 2) throw new CompressedModelInconsistencyException();
    }


    /** Returns an intelligible string corresponding to the card status
     * @param status associated to the Leader Card
     * @return Intelligible string corresponding to the LeaderCardStatus
     */
    private String getCardStatusString(LeaderCardStatus status)
    {
        switch (status)
        {
            case CHOSEN: return "Inactive";
            case IN_USE: return "Active";
            case DISCARDED: return "Discarded";
            default: return "UNEXPECTED";
        }
    }



    /** Checks whether or not the player's inventory or dev card space contains all requirements
     * @param model A reference to the latest compressed model
     * @param card Target card to be activated
     * @return Result of the evaluation
     */
    private boolean areRequirementsFulfilled(CompressedModel model, LeaderCard card)
    {
        List<ResourceStack> playerResources = Arrays.stream(model.getCumulatedPlayerStorage().get(model.getaP()))
                .collect(Collectors.toList());

        //Check for all required resources
        for(ResourceStack requirement : card.getRequiredResources())
        {
            boolean requirementSatisfied = false;

            for(ResourceStack owned : playerResources)
            {
                if(owned.getResourceType() == requirement.getResourceType()
                && owned.getAmount() >= requirement.getAmount())
                    requirementSatisfied = true;
            }

            if(!requirementSatisfied) return false;
        }

        List<Stack<Integer>> rawDevCards = model.getDevCardSpace().get(model.getaP());
        List<DevelopmentCard> devCards = new ArrayList<>();
        //Merge all decks into one list
        for(Stack<Integer> deck : rawDevCards)
        {
            devCards.addAll(deck.stream()
                    .map(CardRepository.getInstance()::getDevCardByID)
                    .collect(Collectors.toList()));
        }

        //Check for all required banners
        for(Triplet<BannerColor, Integer, Integer> bannerOnCard : card.getBannerRequirements())
        {
            int amount = 0;

            //Add one to amount for every matching card
            for(DevelopmentCard devCard : devCards)
            {
                if(devCard.getBannerColor() == bannerOnCard.getFirst()
                && devCard.getCardLevel() == bannerOnCard.getSecond())
                    amount++;
            }

            if(amount < bannerOnCard.getThird()) return false;
        }

        return true;
    }

    @Override
    public void draw(CompressedModel compressedModel) {
        String[] printBuffer = toArrayFromString(background);
        procureCards(compressedModel);

        boolean isCard1Available = chosenLeaderCards.size() > 0;
        boolean isCard2Available = chosenLeaderCards.size() > 1;

        if(isCard1Available)
        {
            LeaderCard card1 = chosenLeaderCards.get(0);
            LeaderCardStatus status1 = cardStatuses.get(0);

            Integer vp1 = card1.getVictoryPoints();
            String abilityDesc1 = card1.getTypeString();

            printBuffer = substituteLineBackground(printBuffer, vp1.toString(), CARD_VP_ROW, CARD1_VP_LINE_POS);
            printBuffer = substituteLineBackground(printBuffer, abilityDesc1, CARD_TYPE_ROW, CARD1_TYPE_LINE_POS);
            printBuffer = substituteLineBackground(printBuffer, getCardStatusString(status1), CARD_STATUS_ROW, CARD1_STATUS_LINE_POS);
            String[] dividestring = card1.getPowerString().split("\n");
            for (int k = 0; k < dividestring.length; k++) {
                printBuffer = substituteLineBackground(printBuffer, dividestring[k], CARD_POWER_ROW+k, CARD1_REQ_START_LINE_POS);
            }
            dividestring = card1.getRequirementString().split("\n");
            for (int k = 0; k < dividestring.length; k++) {
                printBuffer = substituteLineBackground(printBuffer, dividestring[k], CARD_REQ_START_ROW+k, CARD1_REQ_START_LINE_POS);
            }


            if(isCard2Available)
            {
                LeaderCard card2 = chosenLeaderCards.get(1);
                LeaderCardStatus status2 = cardStatuses.get(1);

                Integer vp2 = card2.getVictoryPoints();
                String abilityDesc2 = card2.getTypeString();

                printBuffer = substituteLineBackground(printBuffer, vp2.toString(), CARD_VP_ROW, CARD2_VP_LINE_POS);
                printBuffer = substituteLineBackground(printBuffer, abilityDesc2, CARD_TYPE_ROW, CARD2_TYPE_LINE_POS);
                printBuffer = substituteLineBackground(printBuffer, getCardStatusString(status2), CARD_STATUS_ROW, CARD2_STATUS_LINE_POS);
                dividestring = card2.getPowerString().split("\n");
                for (int k = 0; k < dividestring.length; k++) {
                    printBuffer = substituteLineBackground(printBuffer, dividestring[k], CARD_POWER_ROW+k, CARD2_REQ_START_LINE_POS);
                }
                dividestring = card2.getRequirementString().split("\n");
                for (int k = 0; k < dividestring.length; k++) {
                    printBuffer = substituteLineBackground(printBuffer, dividestring[k], CARD_REQ_START_ROW+k, CARD2_REQ_START_LINE_POS);
                }
            }
        }

        System.out.println(toStringFromArray(printBuffer));
        System.out.println(); //Empty line space
    }

    @Override
    public void askInput(CompressedModel compressedModel) {
        //Check if the previous action was rejected
        checkRejectedMove(compressedModel);

        //Check for each card space if the card requirements are met
        boolean areCard1ReqFulfilled = !chosenLeaderCards.isEmpty() && cardStatuses.get(0) != LeaderCardStatus.IN_USE && areRequirementsFulfilled(compressedModel, chosenLeaderCards.get(0));
        boolean areCard2ReqFulfilled = chosenLeaderCards.size() > 1 && cardStatuses.get(1) != LeaderCardStatus.IN_USE && areRequirementsFulfilled(compressedModel, chosenLeaderCards.get(1));

        int fulfilledCount = 0;
        if(areCard1ReqFulfilled && cardStatuses.get(0) != LeaderCardStatus.IN_USE) fulfilledCount++;
        if(areCard2ReqFulfilled && cardStatuses.get(1) != LeaderCardStatus.IN_USE) fulfilledCount++;

        //No cards can be activated or discarded
        if(chosenLeaderCards.size() == 0)
            System.out.println(GameConstants.SCENE_LEADER_CARD_NO_ACTION_AVAILABLE);
        //Cards can only be discarded
        else if (fulfilledCount == 0)
            System.out.println(GameConstants.SCENE_LEADER_CARD_NO_ACTION_ACTIVATION);
        //One or all cards can be activated
        else
        {
            System.out.print(GameConstants.SCENE_LEADER_CARD_REQUIREMENTS_MET_PART1 + (areCard1ReqFulfilled ? "1" : "2"));
            if(fulfilledCount > 1)
                System.out.println(GameConstants.SCENE_LEADER_CARD_REQUIREMENTS_MET_PART2 + "2");
            else
                System.out.println();

            System.out.println(GameConstants.SCENE_LEADER_CARD_REQUIREMENTS_MET_PART3);
        }

        String inputBuffer;
        boolean actionDone = false;

        //When input is empty, the scene is brought forward
        while(!actionDone) {
            inputBuffer = scanner.nextLine();
            if((inputBuffer.isBlank())||(inputBuffer.equals("\n"))){
                PlayerWorkSpace.getInstance().registerLeaderAction(null, false);
                actionDone = true;
                break;
            }

            char actionType;
            int cardNum;

            //Format check
            try
            {
                actionType = Character.toUpperCase(inputBuffer.charAt(0));
                if(inputBuffer.charAt(1) != '-') continue;
                cardNum = Integer.valueOf(inputBuffer.substring(2));
                if((actionType != 'A' && actionType != 'D') || (cardNum < 1 ) || ( cardNum > 2))
                {
                    System.out.println(GameConstants.SCENE_LEADER_CARD_SYNTAX_ERROR);
                    actionDone = false;
                    continue;
                }
            }
            catch (IndexOutOfBoundsException | NumberFormatException exception)
            {
                System.out.println(GameConstants.SCENE_LEADER_CARD_SYNTAX_ERROR);
                actionDone = false;
                continue;
            }

            //Check if selected card num is valid
            if(cardStatuses.size() < cardNum)
            {
                System.out.println(GameConstants.SCENE_LEADER_CARD_NO_EXISTS);
                continue;
            }
            else if(cardStatuses.get(cardNum - 1) == LeaderCardStatus.IN_USE)
            {
                if(actionType == 'D') {
                    String toPrint = GameConstants.SCENE_LEADER_CARD_ACTIVE_IMPOSSIBLE_TO_DISCARD.replace("X", ((Integer)cardNum).toString());
                    System.out.println(toPrint);
                }
                else System.out.println(GameConstants.SCENE_LEADER_CARD_ALREADY_ACTIVATED.replace("X", ((Integer)cardNum).toString()));
                continue;
            }
            else if(cardStatuses.get(cardNum - 1) == LeaderCardStatus.DISCARDED)
            {
                System.out.println(GameConstants.SCENE_LEADER_CARD_ALREADY_DISCARDED);
                continue;
            }

            //Command interpretation
            //Activate
            if(actionType == 'A')
            {
                boolean canBeActivated = cardNum == 1 ? areCard1ReqFulfilled : areCard2ReqFulfilled;
                LeaderCard cardToActivate = chosenLeaderCards.get(cardNum - 1);

                if(!canBeActivated)
                {
                    System.out.println(GameConstants.SCENE_LEADER_CARD_NOT_ENOUGH_RESOURCES);
                    continue;
                }
                else
                {
                    String toPrint = GameConstants.SCENE_LEADER_CARD_ACTIVATE.replace("X", ((Integer) cardNum).toString());
                    System.out.println(toPrint);
                    cardStatuses.set(cardNum - 1, LeaderCardStatus.IN_USE);
                    PlayerWorkSpace.getInstance().registerLeaderAction(cardToActivate, false);
                    actionDone = true;
                }
            }
            //Discard
            else
            {

                LeaderCard cardToDiscard = chosenLeaderCards.get(cardNum-1);
                String toPrint = GameConstants.SCENE_LEADER_CARD_DISCARD.replace("X", ((Integer) cardNum).toString());
                System.out.println(toPrint);
                cardStatuses.set(cardNum-1, LeaderCardStatus.DISCARDED);
                PlayerWorkSpace.getInstance().registerLeaderAction(cardToDiscard, true);
                actionDone = true;
            }

        }
    }

}
