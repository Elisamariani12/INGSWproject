package it.polimi.ingsw.server.view;

import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.Observable;
import it.polimi.ingsw.common.util.Observer;
import it.polimi.ingsw.server.model.gamemanager.GameSession;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents the View component from the MVC pattern in a client-server architecture
 */
public class VirtualView implements Observer<GameSession>, Observable<PlayerEvent>
{
    List<Observer<PlayerEvent>> registeredObservers;
    LinkedHashMap<String, ServerSideProtocol> clients;

    /**
     * Creates a new VirtualView object for the MVC completion
     */
    public VirtualView()
    {
        registeredObservers = new ArrayList<>();
        clients = new LinkedHashMap<>();
    }

    /**
     * Registers the connection of a player
     * (the socket will have to be passed to the server-side protocol)
     * @param username The username of the player recently connected
     * @param socket Server Socket for the active connection with the player
     * @return the serverSideProtocol
     */
    public synchronized ServerSideProtocol submitConnection(String username,Socket socket)
    {
        ServerSideProtocol serverSideProtocol = new ServerSideProtocol(socket,this);
        clients.put(username, serverSideProtocol);
        return serverSideProtocol;
    }

    /**
     * Calls the 'run' method of all players' seversideprotocols because the maximum number of players in the game session has been reached
     */
    public void startAllPlayers(){
        for(ServerSideProtocol serverSideProtocol: clients.values()){
            new Thread(serverSideProtocol).start();
        }
    }


    /**
     * Creates a compressed (serializable) game session snapshot for the player
     * @param username Username of the player that will receive the message
     * @param gameSession Reference to the party's game session
     * @return the CompressedModel instance
     */
    public synchronized CompressedModel compressGameSession(String username, GameSession gameSession)
    {

        CompressedModel buffer = new CompressedModel();
        buffer.retrieveValues(username, gameSession, getDisconnectedPlayers());
        return buffer;

    }

    @Override
    public void addObserver(Observer<PlayerEvent> observer) {
        registeredObservers.add(observer);
    }

    @Override
    public void removeObserver(Observer<PlayerEvent> observer) {
        registeredObservers.remove(observer);
    }

    @Override
    public synchronized void notify(PlayerEvent message)
    {
        registeredObservers.forEach(observer -> observer.update(message));
    }

    @Override
    public synchronized void update(GameSession message) {
        //Message has to be dispatched to the clients
        for(String username : clients.keySet())
        {
            CompressedModel compressedModel = compressGameSession(username, message);
            //Here, the server side protocol will receive the packet
             if(!clients.get(username).isDisconnected()) clients.get(username).serializeAndSendCompressedModel(compressedModel);
        }
    }

    /**
     * Returns a list of disconnected players
     * @return List of usernames that have left the game session
     */
    private synchronized List<String> getDisconnectedPlayers()
    {
        List<String> disconnectedPlayers = new ArrayList<>();

        //List all disconnected players
        for(String playerName : clients.keySet())
        {
            if(clients.get(playerName).isDisconnected())
                disconnectedPlayers.add(playerName);
        }
        return disconnectedPlayers;
    }

    /**
     * Reconnect the player to the game by updating the socket of the respective serversideprotocol
     * @param username of the player trying to reconnect
     * @param socket new socket of the player trying to reconnect
     * @return the restarted serverSideProtocol instance
     */
    public synchronized ServerSideProtocol reconnectPlayer(String username, Socket socket){
        ServerSideProtocol restartedSSP=clients.get(username);
        restartedSSP.resetSocket(socket);
        new Thread(restartedSSP).start();
        return restartedSSP;
    }
}
