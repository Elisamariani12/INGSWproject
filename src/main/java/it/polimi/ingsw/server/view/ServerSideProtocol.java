package it.polimi.ingsw.server.view;

import com.google.gson.Gson;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.serializable.PlayerEvent;
import it.polimi.ingsw.common.util.Move;
import it.polimi.ingsw.common.util.Observer;
import it.polimi.ingsw.common.util.io.TextReader;
import it.polimi.ingsw.common.util.io.TextWriter;

import java.net.Socket;

/**
 * The communication protocol server side
 */
public class ServerSideProtocol implements Runnable, Observer<String> {

    private Socket socket;
    private Gson gson;
    private TextReader textReader;
    private TextWriter textWriter;
    private VirtualView virtualView;
    private CompressedModel compressedModel;
    private boolean alreadyDisconnected;

    /**
     * Instantiates a new Server side protocol.
     *
     * @param socket      the socket
     * @param virtualView the virtual view
     */
    public ServerSideProtocol(Socket socket,VirtualView virtualView)
    {
        this.socket = socket;
        this.gson = new Gson();
        this.textReader = new TextReader(socket);
        this.textWriter = new TextWriter(socket);
        this.virtualView=virtualView;
        this.alreadyDisconnected=false;
    }

    @Override
    public void run() {
        while(true){

            //receive a message from client if the connection is still active
            try{
                String inputFromClient=textReader.receive();

                //create the PlayerEvent from the serialized received string
                PlayerEvent playerEvent= deserialize(inputFromClient);

                //with this notify, there will be an update in the controller that modifies the model(update) and triggers the update in the virtual view,
                //after this notify the view will automatically have the updated version of the compressed model, ready to send
                virtualView.notify(playerEvent);
            }
            catch(Exception e){
                //System.out.println("HERE");

                //CLIENT DISCONNECTED-SOCKET CLOSED
                handleConnectionOfMyClient(true);
                break;
            }
        }
    }

    /**
     * Handle connection of my client.
     *
     * @param isDisconnection the is disconnection
     */
    public void handleConnectionOfMyClient(Boolean isDisconnection){
        PlayerEvent fakePacket = new PlayerEvent();

        if (isDisconnection){
            socket=null;
            if(!alreadyDisconnected) {
                System.out.println("[ServerSideProtocol] Client disconnected, notifying controller.");
                //Fake packet 'DISCONNECTION'
                fakePacket.setPlayerMove(Move.DISCONNECTION);
                String usernameDisconnected = null;
                for (String usernames : virtualView.clients.keySet()) {
                    if (virtualView.clients.get(usernames).equals(this)) {
                        usernameDisconnected = usernames;
                    }
                }
                fakePacket.setPlayerUserName(usernameDisconnected);

                virtualView.notify(fakePacket);

                alreadyDisconnected=true;
            }
        }

        else{
            fakePacket.setPlayerMove(Move.RECONNECTION);
            String usernameReconnected = null;
            alreadyDisconnected = false;
            for (String usernames : virtualView.clients.keySet()) {
                if (virtualView.clients.get(usernames).equals(this)) {
                    usernameReconnected = usernames;
                }
            }
            fakePacket.setPlayerUserName(usernameReconnected);
            virtualView.notify(fakePacket);
        }
    }

    /**
     * Serialize string.
     *
     * @param compressedModel the compressed model
     * @return the string
     */
    public String serialize(CompressedModel compressedModel){
        return gson.toJson(compressedModel);
    }


    /**
     * Update the socket of this serversideProtocol for reconnection
     * @param serializedObj the serialized playerEvent
     * @return the deserialized playerEvent
     */
    private PlayerEvent deserialize(String serializedObj){
        return gson.fromJson(serializedObj, PlayerEvent.class);
    }

    /**
     * Serialize and send compressed model.
     *
     * @param compressedModel the compressed model
     */
    public void serializeAndSendCompressedModel(CompressedModel compressedModel){
        String compressedModelToSendToTheClient=serialize(compressedModel);
        textWriter.send(compressedModelToSendToTheClient);
    }

    /**
     * Is disconnected boolean.
     *
     * @return the boolean
     */
    public boolean isDisconnected()
    {
        return socket == null;
    }

    /**
     * Update the socket of this serversideProtocol for reconnection
     *
     * @param socket new socket
     */
    public void resetSocket(Socket socket){
        this.socket=socket;
        this.textReader = new TextReader(socket);
        this.textWriter = new TextWriter(socket);
        handleConnectionOfMyClient(false);
    }

    @Override
    public void update(String message) {
        if(message.equals("DISCONNECTION")){
            handleConnectionOfMyClient(true);
        }
    }
}
