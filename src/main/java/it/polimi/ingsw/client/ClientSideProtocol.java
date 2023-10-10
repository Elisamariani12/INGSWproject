package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.BiObservable;
import it.polimi.ingsw.common.util.BiObserver;
import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.io.TextReader;
import it.polimi.ingsw.common.util.io.TextWriter;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * The type Client side protocol.
 */
public class ClientSideProtocol implements Runnable, BiObserver<String,PlayerEvent>, BiObservable<String,CompressedModel> {

    private long identifier;
    private Socket socket;
    private Socket secondarySocket;
    private Gson gson;
    private TextReader textReader;
    private TextWriter textWriter;
    private TextReader pingReader;
    private TextWriter pingWriter;
    private boolean joiningCompleted;
    /**
     * The Registered observers.
     */
    List<BiObserver<String,CompressedModel>> registeredObservers;

    /**
     * Instantiates a new Client side protocol.
     *
     * @param socket          the client socket
     * @param secondarySocket the secondary socket
     */
    public ClientSideProtocol(Socket socket,Socket secondarySocket) {
        Random randomLong=new Random();
        this.identifier= randomLong.nextLong();
        this.socket = socket;
        this.secondarySocket=secondarySocket;
        this.gson = new Gson();
        textReader = new TextReader(socket);
        textWriter = new TextWriter(socket);
        pingReader = new TextReader(secondarySocket);
        pingWriter = new TextWriter(secondarySocket);
        joiningCompleted= false;
        registeredObservers = new ArrayList<>();
    }

    @Override
    public void run() {
        //SEND MY IDENTIFIER with BOTH SOCKETS////////////////////////////////////////////////////////////////////////
        textWriter.send(((Long)identifier).toString());
        pingWriter.send(((Long)identifier).toString());

        //START THE PING PONG WITH SERVER
        new  Thread(new PingHandlerClientSide(secondarySocket)).start();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        initialCommunication();
        System.out.println("Initial Communication done");

        while(true){
            //receive a message from server
            try {
                String inputFromServer = textReader.receive();

                //deserialize the compressed model
                CompressedModel compressedModel = deserialize(inputFromServer);

                //notify UserInterface with the compressed model, create the response in 'playerEvent' and automatically send it with the 'update' method
                notify(null, compressedModel);
            }
            catch(RuntimeException runtimeException)
            {
                runtimeException.printStackTrace();
                System.exit(-1);
            }
            catch(Exception e)
            {
                System.out.println("\n\nDisconnected Server");//SERVER UNAVAILABLE
                System.exit(123);
            }
        }
    }


    /**
     * Send player event over the network.
     *
     * @param playerEvent the player event
     */
    public void sendPlayerEvent(PlayerEvent playerEvent){
        String serializedObj = serialize(playerEvent);
        textWriter.send(serializedObj);
    }

    /**
     * Serialize the player event with GSON
     *
     * @param  playerEvent the playerEvent to serialize
     * @return serialized playerEvent
     */
    private String serialize(PlayerEvent playerEvent){
        return gson.toJson(playerEvent);
    }

    /**
     * Deserialize the player event with GSON
     *
     * @param  serializedObj the serialized playerEvent
     * @return playerEvent
     */
    private CompressedModel deserialize(String serializedObj){
        return gson.fromJson(serializedObj, CompressedModel.class);
    }

    /**
     * Initial communication with a new client
     */
    private void initialCommunication() {
        //the notify asks "new or reconnecting?" to the cli and also SENDS back the reply to the server: NEW or RECONNECTING
        String messageReceivedFromServer=textReader.receive();
        notify(messageReceivedFromServer,null);

        //the notify asks "name" or "name and players" to the cli and also sends back the reply to the server
        //in case the cli has to reply name+players it has to be in the format "name-numberOfPlayers"
        messageReceivedFromServer=textReader.receive();
        notify(messageReceivedFromServer, null);

        //If player receives an ack 'OK', joining process is completed
        if(textReader.receive().equals(GameConstants.ACK)){
            joiningCompleted = true;
        }

        //Otherwise he has to sent a response nack 'KO' and redo the entire process
        else{
             textWriter.send(GameConstants.NACK);
            initialCommunication();
        }

    }



    @Override
    public void addObserver(BiObserver<String, CompressedModel> biObserver) {
        registeredObservers.add(biObserver);
    }

    @Override
    public void removeObserver(BiObserver<String, CompressedModel> biObserver) {
        registeredObservers.remove(biObserver);
    }

    @Override
    public void notify(String message1, CompressedModel message2) {
        registeredObservers.forEach(observer -> observer.update(message1,message2));
    }

    @Override
    public void update(String message1, PlayerEvent message2) {
        //INITIAL MESSAGES IF MESSAGE1=null
        if(message1 != null){
            textWriter.send(message1);
        }
        //NORMAL MESSAGE
        else{
            sendPlayerEvent(message2);
        }
    }
}

