package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.gamemanager.Player;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.util.Deck;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.util.CardRepository;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.floor;
import static org.junit.jupiter.api.Assertions.*;

class GameModeTest {
    private SinglePlayer gameModeSinglePlayer;
    private MultiPlayer gameModeMultiPlayer;

    @BeforeEach
    void setUp() {
        CardRepository.getInstance().loadAllData();
        gameModeSinglePlayer = new SinglePlayer();
        gameModeMultiPlayer = new MultiPlayer();
    }

    @Test
    void getWinner() {
    }

    //vins
    @Test
    void addPlayerToSession() {
        for(Integer i=0; i<4; i++){
            gameModeMultiPlayer.addPlayerToSession(new Player(String.valueOf(i)));
        }
        for(Integer i=0; i<4; i++){
            assertEquals(gameModeMultiPlayer.getModel().getGameSession().getPlayerByIndex(i).getUsername(), String.valueOf(i));
        }
    }

    //vins
    @Test
    void prepareDevCardGrid() {
        gameModeSinglePlayer.prepareDevCardGrid();
        assert true;
    }

    //vins
    @Test
    void dealLeaderCard() {
        Player p = new Player("A");
        gameModeSinglePlayer.addPlayerToSession(p);
        gameModeSinglePlayer.dealLeaderCard();
        System.out.println(gameModeSinglePlayer.getModel().getGameSession().getPlayerByIndex(0).getBoard().getLeaderCardSpace().getAllCards().size());
        assert true;
    }

    //elisa
    @Test
    void preparePersonalBoard() {
        Player p = new Player("A");
        Player p2 = new Player("B");

        gameModeMultiPlayer.addPlayerToSession(p);
        gameModeMultiPlayer.addPlayerToSession(p2);

        gameModeMultiPlayer.preparePersonalBoards();
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getWarehouseDepot().getAmountByResource(Resource.COIN), 0);
        assertEquals(gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getWarehouseDepot().getAmountByResource(Resource.COIN), 0);
    }

    //elisa OK
    @Test
    void chooseLeaderCards() throws FullResourceStackException {
        Player p = new Player("A");
        Player p2 = new Player("B");
        gameModeMultiPlayer.addPlayerToSession(p);
        gameModeMultiPlayer.addPlayerToSession(p2);
        gameModeMultiPlayer.preparePersonalBoards();

        gameModeMultiPlayer.dealLeaderCard();
        Set<LeaderCard> leaderCards=gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().getAllCards();
        LeaderCard l1=leaderCards.stream().collect(Collectors.toList()).get(0);
        LeaderCard l2=leaderCards.stream().collect(Collectors.toList()).get(1);

        gameModeMultiPlayer.chooseLeaderCards(0,l1,l2);
        assertTrue(gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().getChosenCards().contains(l1));
    }

    //vins
    @RepeatedTest(50)
    void countAllVictoryPointsForPlayerSINGLEPLAYER() {
        //Initial Settings for testing
        int faithTrackFinalPosition = 10;
        int strongBoxResources = 20;
        int warehouseResources = 2;
        int numberOfCards = 3;

        gameModeSinglePlayer.addPlayerToSession(new Player("gioc"));
        Player player = gameModeSinglePlayer.getModel().getGameSession().getPlayerByIndex(0);
        Deck<LeaderCard> leaderCardDeck = CardRepository.getInstance().getAllLeaderCards().copy();
        Deck<DevelopmentCard> devCardDeck = CardRepository.getInstance().getAllDevelopmentCards().copy();

        //Distribute lead n dev cards
        Deck<LeaderCard> playerLeaderCards = new Deck<>();
        Deck<DevelopmentCard> playerDevCards = new Deck<>();
        for(int i=1; i<= numberOfCards; i++){
            LeaderCard actualLeaderCard = leaderCardDeck.removeElement();
            DevelopmentCard actualDevCard = devCardDeck.removeElement();

            playerLeaderCards.addElement(actualLeaderCard);
            playerDevCards.addElement(actualDevCard);

            player.getBoard().getDevelopmentCardSpace().pushCard(i, actualDevCard);
            player.getBoard().getLeaderCardSpace().addCard(actualLeaderCard);
            player.getBoard().getLeaderCardSpace().setCardStatus(actualLeaderCard, LeaderCardStatus.IN_USE);
        }

        //advance in faith track ALONE (player activates all Vatican reports during his moves)
        gameModeSinglePlayer.tryAdvance(player,10);


        //add some resources into various deposits
        player.getBoard().getStrongBox().addResourceAmount(Resource.COIN, strongBoxResources);
        for(int i=0; i<warehouseResources; i++){
            try {
                player.getBoard().getWarehouseDepot().InsertResource(Resource.COIN);
            } catch (ResourceNotInsertableException e) {
                assert false;
            }
        }

        //Manually count inserted points
        int insertedPoints = 0;
        for(LeaderCard lcard : playerLeaderCards){
            insertedPoints = insertedPoints + lcard.getVictoryPoints();
        }
        for(DevelopmentCard dcard : playerDevCards){
            insertedPoints = insertedPoints + dcard.getVictoryPoints();
        }
        insertedPoints = insertedPoints + (int) floor((strongBoxResources + warehouseResources)/5);
        //add manually faithtrack points
        insertedPoints = insertedPoints + 4 + 2;

        //Count final victory points
        int countedPoints = 0;
        countedPoints = gameModeSinglePlayer.countAllVictoryPointsForPlayer(0);

        //check if points are equal
        assertEquals(countedPoints, insertedPoints);
    }

    //vins
    @RepeatedTest(50)
    void countAllVictoryPointsForPlayerMULTIPLAYER() {
        //Initial Settings for testing
        int faithTrackFinalPosition = 20;
        int strongBoxResources = 20;
        int warehouseResources = 2;
        int numberOfCards = 3;
        Deck<LeaderCard> leaderCardDeck = CardRepository.getInstance().getAllLeaderCards().copy();
        Deck<DevelopmentCard> devCardDeck = CardRepository.getInstance().getAllDevelopmentCards().copy();

        for(int pl=0; pl<1; pl++){
            gameModeSinglePlayer.addPlayerToSession(new Player(String.valueOf(pl)));
            Player player = gameModeSinglePlayer.getModel().getGameSession().getPlayerByIndex(pl);

            //Distribute lead n dev cards
            Deck<LeaderCard> playerLeaderCards = new Deck<>();
            Deck<DevelopmentCard> playerDevCards = new Deck<>();
            for(int i=1; i<= numberOfCards; i++){
                LeaderCard actualLeaderCard = leaderCardDeck.removeElement();
                DevelopmentCard actualDevCard = devCardDeck.removeElement();

                playerLeaderCards.addElement(actualLeaderCard);
                playerDevCards.addElement(actualDevCard);

                player.getBoard().getDevelopmentCardSpace().pushCard(i, actualDevCard);
                player.getBoard().getLeaderCardSpace().addCard(actualLeaderCard);
                player.getBoard().getLeaderCardSpace().setCardStatus(actualLeaderCard, LeaderCardStatus.IN_USE);
            }

            //advance in faith track ALONE (player activates all Vatican reports during his moves)
            gameModeSinglePlayer.tryAdvance(player,10);


            //add some resources into various deposits
            player.getBoard().getStrongBox().addResourceAmount(Resource.COIN, strongBoxResources);
            for(int i=0; i<warehouseResources; i++){
                try {
                    player.getBoard().getWarehouseDepot().InsertResource(Resource.COIN);
                } catch (ResourceNotInsertableException e) {
                    assert false;
                }
            }

            //Manually count inserted points
            int insertedPoints = 0;
            for(LeaderCard lcard : playerLeaderCards){
                insertedPoints = insertedPoints + lcard.getVictoryPoints();
            }
            for(DevelopmentCard dcard : playerDevCards){
                insertedPoints = insertedPoints + dcard.getVictoryPoints();
            }
            insertedPoints = insertedPoints + (int) floor((strongBoxResources + warehouseResources)/5);
            //add manually faithtrack points
            insertedPoints = insertedPoints + 4 + 2;

            //Count final victory points
            int countedPoints = 0;
            countedPoints = gameModeSinglePlayer.countAllVictoryPointsForPlayer(pl);

            //check if points are equal
            assertEquals(countedPoints, insertedPoints);
        }


    }

    //vins
    @Test
    void discardLeaderCard() {
        gameModeSinglePlayer.addPlayerToSession(new Player("gioc"));
        Player player = gameModeSinglePlayer.getModel().getGameSession().getPlayerByIndex(0);
        gameModeSinglePlayer.getModel().getGameSession().startSession();
        Deck<LeaderCard> leaderCardDeck = CardRepository.getInstance().getAllLeaderCards().copy();
        Deck<DevelopmentCard> devCardDeck = CardRepository.getInstance().getAllDevelopmentCards().copy();

        //Distribute lead n dev cards
        Deck<LeaderCard> playerLeaderCards = new Deck<>();
        Deck<DevelopmentCard> playerDevCards = new Deck<>();
        for(int i=1; i<=3; i++){
            LeaderCard actualLeaderCard = leaderCardDeck.removeElement();
            DevelopmentCard actualDevCard = devCardDeck.removeElement();

            playerLeaderCards.addElement(actualLeaderCard);
            playerDevCards.addElement(actualDevCard);

            player.getBoard().getDevelopmentCardSpace().pushCard(i, actualDevCard);
            player.getBoard().getLeaderCardSpace().addCard(actualLeaderCard);
            player.getBoard().getLeaderCardSpace().setCardStatus(actualLeaderCard, LeaderCardStatus.CHOSEN);
        }

        for(LeaderCard lead : playerLeaderCards){
            Assertions.assertThrows(ImpossibleLeaderCardActionException.class, () -> {
                gameModeSinglePlayer.discardLeaderCardForFaithPoints(leaderCardDeck.removeElement());
            });

            try {
                gameModeSinglePlayer.discardLeaderCardForFaithPoints(lead);
            } catch (ImpossibleLeaderCardActionException | FaithTrackOutOfBoundsException impossibleLeaderCardAction) {
                assert false;
            }
        }

        assertEquals(player.getBoard().getLeaderCardSpace().getChosenCards().size(), 0);

    }

    //vins
    @Test
    void activateLeaderCard() {
        gameModeSinglePlayer.addPlayerToSession(new Player("gioc"));
        Player player = gameModeSinglePlayer.getModel().getGameSession().getPlayerByIndex(0);
        gameModeSinglePlayer.getModel().getGameSession().startSession();
        gameModeSinglePlayer.prepareDevCardGrid();
        Deck<LeaderCard> leaderCardDeck = CardRepository.getInstance().getAllLeaderCards().copy();
        Deck<DevelopmentCard> devCardDeck = CardRepository.getInstance().getAllDevelopmentCards().copy();
        //Right Resource Requirements leader card
        LeaderCard chosenResCard = leaderCardDeck.getElement(7);

        //Right Banner Requirements leader card
        LeaderCard chosenBanCard = leaderCardDeck.getElement(12);

        //Not owned leader card
        LeaderCard NotOwnedCard = leaderCardDeck.getElement(11);

        //Owned card, not owned resources
        LeaderCard NotOwnedResourcesCard = leaderCardDeck.getElement(9);

        //give cards to the player
        player.getBoard().getLeaderCardSpace().addCard(chosenBanCard);
        player.getBoard().getLeaderCardSpace().setCardStatus(chosenBanCard,LeaderCardStatus.CHOSEN);
        player.getBoard().getLeaderCardSpace().addCard(chosenResCard);
        player.getBoard().getLeaderCardSpace().setCardStatus(chosenResCard,LeaderCardStatus.CHOSEN);
        player.getBoard().getLeaderCardSpace().addCard(NotOwnedResourcesCard);
        player.getBoard().getLeaderCardSpace().setCardStatus(NotOwnedResourcesCard,LeaderCardStatus.CHOSEN);
        //adding required resources
        player.getBoard().getStrongBox().addResourceAmount(Resource.COIN, 3);
        for(int i=0; i<2; i++){
            try {
                player.getBoard().getWarehouseDepot().InsertResource(Resource.COIN);
            } catch (ResourceNotInsertableException e) {
                assert false;
            }
        }

        //pick banner requiremements
        player.getBoard().getDevelopmentCardSpace().pushCard(1,gameModeSinglePlayer.getModel().getGameSession().getDevelopmentCardGrid().pickCard(0,2));
        player.getBoard().getDevelopmentCardSpace().pushCard(2,gameModeSinglePlayer.getModel().getGameSession().getDevelopmentCardGrid().pickCard(0,3));

        try {
            gameModeSinglePlayer.activateLeaderCard(chosenBanCard);
            gameModeSinglePlayer.activateLeaderCard(chosenResCard);
        } catch (ImpossibleLeaderCardActionException | InsufficientResourcesException impossibleLeaderCardAction) {
            assert false;
        }
        Assertions.assertThrows(ImpossibleLeaderCardActionException.class, () -> {
            gameModeSinglePlayer.activateLeaderCard(NotOwnedCard);
        });
        Assertions.assertThrows(InsufficientResourcesException.class, () -> {
            gameModeSinglePlayer.activateLeaderCard(NotOwnedResourcesCard);
        });

    }

    //elisa
    @Test
    void getAllPurchasableCards() {
        Player p = new Player("A");
        Player p2 = new Player("B");
        gameModeMultiPlayer.addPlayerToSession(p);
        gameModeMultiPlayer.addPlayerToSession(p2);
        gameModeMultiPlayer.preparePersonalBoards();
        gameModeMultiPlayer.setupGame();
        //fill with some resources the strongbox of the active player to see if he can buy some dev cards
        gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getStrongBox().setResourceAmount(Resource.COIN,10);
        gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getStrongBox().setResourceAmount(Resource.STONE,10);
        gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getStrongBox().setResourceAmount(Resource.SHIELD,10);
        gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getStrongBox().setResourceAmount(Resource.SERVANT,10);

        for(DevelopmentCard d:gameModeMultiPlayer.getAllPurchasableCards()){
            assertEquals(d.getCardLevel(),1);
        }
    }

    //elisa
    @Test
    void selectMarket_COL_Row_ANDHandleWhiteAndRedMarblesANDInsertResourcesInDeposits() throws ResourceNotInsertableException {
        Player p = new Player("A");
        Player p2 = new Player("B");
        gameModeMultiPlayer.addPlayerToSession(p);
        gameModeMultiPlayer.addPlayerToSession(p2);
        gameModeMultiPlayer.preparePersonalBoards();
        gameModeMultiPlayer.setupGame();


        HashMap<Resource, Integer> whitesubstitutes = new HashMap<>();
        List<LeaderCard> leaderCardList = new ArrayList<>(gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().getAllCards());
        int temp=0;
        for (LeaderCard l : leaderCardList) {
            if((temp%2)==0){gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().setCardStatus(l, LeaderCardStatus.IN_USE);}
            else{gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().setCardStatus(l, LeaderCardStatus.DISCARDED);}
            temp++;
        }

        long numberOfLeaderCardsWhiteActive = gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards().stream().filter((x) -> (x.getPower().getSpecialAbilityType() == SpecialAbility.WHITE_MARBLE_SUBSTITUTION)).count();
        //System.out.println("numberofActiveWhiteSubstitutionCards: " + numberOfLeaderCardsWhiteActive);
        if (numberOfLeaderCardsWhiteActive == 1) {
            Resource type1 = ((AbilityPower) gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards().stream().filter((x) -> (x.getPower().getSpecialAbilityType() == SpecialAbility.WHITE_MARBLE_SUBSTITUTION)).collect(Collectors.toList()).get(0).getPower()).getResourceType();
            whitesubstitutes.put(type1, 1);//System.out.println("SUBSTITUTE IS: "+type1);
        }
        else if (numberOfLeaderCardsWhiteActive == 2) {
            Resource type1 = ((AbilityPower) gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards().stream().filter((x) -> (x.getPower().getSpecialAbilityType() == SpecialAbility.WHITE_MARBLE_SUBSTITUTION)).collect(Collectors.toList()).get(0).getPower()).getResourceType();
            whitesubstitutes.put(type1, 1);//System.out.println("SUBSTITUTE1 IS: "+type1);
            Resource type2 = ((AbilityPower) gameModeMultiPlayer.getModel().getGameSession().getActivePlayer().getBoard().getLeaderCardSpace().getActiveCards().stream().filter((x) -> (x.getPower().getSpecialAbilityType() == SpecialAbility.WHITE_MARBLE_SUBSTITUTION)).collect(Collectors.toList()).get(1).getPower()).getResourceType();
            whitesubstitutes.put(type2, 3);//System.out.println("SUBSTITUTE2 IS: "+type2);
        }
        List<Resource> resource1= new ArrayList<>();
        //remove a coin so that if all the resources are of 4 different types, and i try to insert them all, the coin will be discarded
        resource1.add(Resource.COIN);
        resource1.add(Resource.SERVANT);
        resource1.add(Resource.SHIELD);
        resource1.add(Resource.STONE);

        gameModeMultiPlayer.selectMarketColumn(1,whitesubstitutes,resource1);


        assert true;
    }



}