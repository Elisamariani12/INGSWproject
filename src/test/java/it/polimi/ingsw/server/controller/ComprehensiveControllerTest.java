package it.polimi.ingsw.server.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.model.TurnState;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.model.gamemanager.GameSession;
import it.polimi.ingsw.server.model.gameplay.*;
import it.polimi.ingsw.server.view.VirtualView;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MockSocket extends Socket
{
    private String inputBuffer;

    public MockSocket()
    {
    }

    public MockSocket(String host, int port)
    {

    }

    @Override
    public void connect(SocketAddress endpoint){}

    @Override
    public void close(){}

    @Override
    public synchronized InputStream getInputStream()
    {
        while(inputBuffer == null || inputBuffer.isBlank())
        {
            try{this.wait();}
            catch (InterruptedException e) {e.printStackTrace();}
        }
        String buffer = inputBuffer;
        inputBuffer = null;

        return new ByteArrayInputStream(inputBuffer.getBytes());
    }

    @Override
    public synchronized OutputStream getOutputStream()
    {
        return System.out;
    }

    public synchronized void send(String text)
    {
        this.inputBuffer = text;
        this.notifyAll();
    }
}

/** Tests the controller infrastructure using a mock controller */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComprehensiveControllerTest
{
    private static Gson gson;
    private static Controller controller;
    private static VirtualView virtualView;
    private static MockSocket fakeSocket;
    private static GameSession gameSession;

    private static LeaderCard leaderCard_4, leaderCard_1, leaderCard_11, leaderCard_8;
    private static DevelopmentCard devCard_32, devCard_43, devCard_18, devCard_60;

    @BeforeAll
    public static void InitializeGameSession()
    {
        gson = new Gson();

        CardRepository.getInstance().loadAllData();

        virtualView = new VirtualView();
        controller = new Controller("Player", 1);
        controller.setTestMode(true);
        fakeSocket = new MockSocket();
        controller.submitConnection("Player");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                virtualView.submitConnection("Player", fakeSocket);
            }
        });

        leaderCard_1 = CardRepository.getInstance().getLeaderCardByID(1);
        leaderCard_4 = CardRepository.getInstance().getLeaderCardByID(4);
        leaderCard_8 = CardRepository.getInstance().getLeaderCardByID(8);
        leaderCard_11 = CardRepository.getInstance().getLeaderCardByID(11);

        devCard_18 = CardRepository.getInstance().getDevCardByID(18);
        devCard_43 = CardRepository.getInstance().getDevCardByID(43);
        devCard_32 = CardRepository.getInstance().getDevCardByID(32);
        devCard_60 = CardRepository.getInstance().getDevCardByID(60);

        Field gameModeField = null;
        Field marketField = null;
        Field extMarketField = null;
        Resource[][] marketState = {{Resource.WHITE, Resource.STONE, Resource.WHITE, Resource.WHITE},
                                    {Resource.COIN, Resource.SHIELD, Resource.SERVANT, Resource.FAITH},
                                    {Resource.STONE, Resource.WHITE, Resource.SHIELD, Resource.SERVANT}};

        /* Reflection is needed to access private fields that contain references (not serializable) */
        //Get inaccessible fields
        try
        {
            gameModeField = Controller.class.getDeclaredField("gameMode");
            gameModeField.setAccessible(true);

            marketField = MarketBoard.class.getDeclaredField("marketState");
            marketField.setAccessible(true);
            extMarketField = MarketBoard.class.getDeclaredField("externalMarble");
            extMarketField.setAccessible(true);
        }
        catch (NoSuchFieldException exception){ exception.printStackTrace();}
        //Set values
        try
        {
            GameMode gameMode = (GameMode) gameModeField.get(controller);
            marketField.set(gameMode.getModel().getGameSession().getMarketBoard(), marketState);
            extMarketField.set(gameMode.getModel().getGameSession().getMarketBoard(), Resource.COIN);
            gameMode.getModel().addObserver(virtualView);
            gameSession = gameMode.getModel().getGameSession();
        }
        catch (Exception e) {e.printStackTrace();}

        virtualView.addObserver(controller);

        LeaderCardSpace lcs = gameSession.getPlayerList().get(0).getBoard().getLeaderCardSpace();
        lcs.addCard(leaderCard_1);
        lcs.addCard(leaderCard_4);
        lcs.addCard(leaderCard_8);
        lcs.addCard(leaderCard_11);

        Deck<DevelopmentCard> fakeDeck = new Deck<>();
        fakeDeck.addElement(devCard_32);
        gameSession.getDevelopmentCardGrid().placeCardDeck(fakeDeck, 0, 0);

        //Register base production power
        List<ResourceStack> requirements = new ArrayList<>();
        List<ResourceStack> rewards = new ArrayList<>();
        try
        {
            requirements.add(new ResourceStack(Resource.GENERIC, 1, 1));
            requirements.add(new ResourceStack(Resource.GENERIC, 1, 1));
            rewards.add(new ResourceStack(Resource.GENERIC, 1, 1));
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }

    }

    /** Send a fake client message
     * @param event Mock player event
     */
    public void fakeReceiveFromClient(PlayerEvent event)
    {
        virtualView.notify(event);
    }

    /** Called by every skip(I/F)LAx() method */
    public void skipLeaderAction()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.LEADER_ACTION);
        playerEvent.setLeaderCards(null);
        fakeReceiveFromClient(playerEvent);
    }

    @Test
    @Order(1)
    public void setupPlayerChoices()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.INITIAL_CHOICES);
        playerEvent.setInputResources(new ArrayList<>());

        List<Integer> chosenLeaderCards = new ArrayList<>();
        chosenLeaderCards.add(4);
        chosenLeaderCards.add(11);

        playerEvent.setLeaderCards(chosenLeaderCards);
        fakeReceiveFromClient(playerEvent);

        assertEquals(gameSession.getTurnState(), TurnState.INITIAL_LEADER_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(2)
    public void skipILA1()
    {
        skipLeaderAction();

        assertEquals(gameSession.getTurnState(), TurnState.CHOOSE_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(3)
    public void marketActionNo1()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.MARKET_MOVE);
        playerEvent.setCol(0);
        playerEvent.setRow(-1);
        playerEvent.setHasDiscarded(false);
        fakeReceiveFromClient(playerEvent);

        assertEquals(gameSession.getTurnState(), TurnState.FINAL_LEADER_ACTIONS);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(4)
    public void skipFLA1()
    {
        skipLeaderAction();

        assertEquals(gameSession.getTurnState(), TurnState.INITIAL_LEADER_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(5)
    public void skipILA2()
    {
        skipLeaderAction();

        assertEquals(gameSession.getTurnState(), TurnState.CHOOSE_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(6)
    public void marketActionNo2()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.MARKET_MOVE);
        playerEvent.setCol(0);
        playerEvent.setRow(-1);
        playerEvent.setHasDiscarded(false);
        fakeReceiveFromClient(playerEvent);

        assertEquals(gameSession.getTurnState(), TurnState.FINAL_LEADER_ACTIONS);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(7)
    public void skipFLA2()
    {
        skipLeaderAction();

        assertEquals(gameSession.getTurnState(), TurnState.INITIAL_LEADER_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(8)
    public void skipILA3()
    {
        skipLeaderAction();

        assertEquals(gameSession.getTurnState(), TurnState.CHOOSE_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(9)
    public void buyDevCard()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.DEV_CARD_REQUEST);
        playerEvent.setRow(0);
        playerEvent.setCol(0);
        playerEvent.setIndex(3);
        fakeReceiveFromClient(playerEvent);

        assertEquals(gameSession.getTurnState(), TurnState.FINAL_LEADER_ACTIONS);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(10)
    public void skipFLA3()
    {
        skipLeaderAction();

        assertEquals(gameSession.getTurnState(), TurnState.INITIAL_LEADER_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(11)
    public void fastForward() {
        DevelopmentCardSpace space = gameSession.getPlayerList().get(0).getBoard().getDevelopmentCardSpace();

        space.pushCard(2, devCard_43);
        space.pushCard(1, devCard_18);
        space.pushCard(1, devCard_60);

        ProductionPowerRegistry reg = gameSession.getPlayerList().get(0).getBoard().getProductionPowerRegistry();
        reg.registerActivatedProductionPower(devCard_43.getProductionPower());
        reg.registerActivatedProductionPower(devCard_60.getProductionPower());

        gameSession.getPlayerList().get(0).getBoard().getStrongBox().setResourceAmount(Resource.SHIELD, 1);
    }


    @Test
    @Order(12)
    public void activateLeaderCard()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.LEADER_ACTION);
        playerEvent.setHasDiscarded(false);
        List<Integer> toActivate = new ArrayList<>();
        toActivate.add(4);
        playerEvent.setLeaderCards(toActivate);
        fakeReceiveFromClient(playerEvent);


        assertEquals(gameSession.getTurnState(), TurnState.CHOOSE_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(13)
    public void activateBaseProductionPower()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.PRODUCTION_POWER_REQUEST);
        playerEvent.setDevCard(-1);
        playerEvent.setLeaderCards(null);
        List<Pair<Resource, StorageType>> inputResources = new ArrayList<>();
        List<Resource> outputResources = new ArrayList<>();
        inputResources.add(new Pair<>(Resource.STONE, StorageType.WAREHOUSE_DEPOT));
        inputResources.add(new Pair<>(Resource.STONE, StorageType.WAREHOUSE_DEPOT));
        outputResources.add(Resource.COIN);

        playerEvent.setInputResources(inputResources);
        playerEvent.setOutputResources(outputResources);

        fakeReceiveFromClient(playerEvent);

        assertEquals(gameSession.getTurnState(), TurnState.PRODUCTION_PHASE);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(14)
    public void activateLeaderProductionPower()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.PRODUCTION_POWER_REQUEST);
        playerEvent.setDevCard(-1);
        List<Integer> leaderCardProdPower = new ArrayList<>();
        leaderCardProdPower.add(4);
        playerEvent.setLeaderCards(leaderCardProdPower);

        List<Resource> outputResources = new ArrayList<>();
        outputResources.add(Resource.STONE);

        playerEvent.setOutputResources(outputResources);

        fakeReceiveFromClient(playerEvent);

        assertEquals(gameSession.getTurnState(), TurnState.PRODUCTION_PHASE);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(15)
    public void stopProductionPowers()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.PRODUCTION_POWER_END);

        fakeReceiveFromClient(playerEvent);


        assertEquals(gameSession.getTurnState(), TurnState.FINAL_LEADER_ACTIONS);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }

    @Test
    @Order(16)
    public void discardLeaderCard()
    {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setPlayerUserName("Player");
        playerEvent.setPlayerMove(Move.LEADER_ACTION);
        playerEvent.setHasDiscarded(true);
        List<Integer> toDiscard = new ArrayList<>();
        toDiscard.add(11);
        playerEvent.setLeaderCards(toDiscard);
        playerEvent.setDiscardedList(toDiscard);
        fakeReceiveFromClient(playerEvent);

        assertEquals(gameSession.getTurnState(), TurnState.INITIAL_LEADER_ACTION);
        assertEquals(gameSession.getLastMoveResponse(), PlayerMoveResponse.SUCCESS);
    }
}
