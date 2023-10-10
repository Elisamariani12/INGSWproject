package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.Observable;
import it.polimi.ingsw.common.util.Observer;
import it.polimi.ingsw.common.util.Triplet;
import it.polimi.ingsw.common.util.io.TextReader;
import it.polimi.ingsw.common.util.io.TextWriter;
import it.polimi.ingsw.server.view.ServerSideProtocol;
import it.polimi.ingsw.server.view.VirtualView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Listens for new connections, creates controller objects and redirects active connections to them
 */
public class ConnectionHelper
{
    //List of all active controllers (all running on their own thread)
    private List<Controller> controllerPool;
    private List<VirtualView> virtualViewList;
    private int port;
    private int secondaryPort;
    private ServerClosingThread serverClosingThread;
    //the second one is the main socket, the third one is for the pings
    private List<Triplet<String,Socket,Socket>> socketsPairs;
    private ServerClosingThread serverClosingThreadForSecondarySockets;
    private ArrayList<Socket> allmainSockets;
    private ArrayList<Socket> allSecondarySockets;
    private List<String> alreadyConnectedClientsIdentifiers;



    /**
     * Creates a new connection helper on the specified port
     *
     * @param port  TCP-IP port
     * @param port2 the port 2
     */
    public ConnectionHelper(int port, int port2)
    {
        this.port = port;
        this.secondaryPort=port2;
        controllerPool = new ArrayList<>();
        virtualViewList = new ArrayList<>();
        socketsPairs= new ArrayList<>();
        allmainSockets=new ArrayList<>();
        allSecondarySockets=new ArrayList<>();
        alreadyConnectedClientsIdentifiers=new ArrayList<>();

    }


    /**
     * Start server.
     */
    public void startServer(){

        ServerSocket serverSocket = null;
        ServerSocket secondaryServerSocket= null;


        //try to create a new server socket, error if the port is unavailable
        try{
            serverSocket= new ServerSocket(port);
            secondaryServerSocket=new ServerSocket(secondaryPort);
        }
        catch(IOException e){e.printStackTrace();}

        serverClosingThread = new ServerClosingThread(allmainSockets);
        serverClosingThreadForSecondarySockets=new ServerClosingThread(allSecondarySockets);

        new Thread(serverClosingThread).start();
        new Thread(serverClosingThreadForSecondarySockets).start();

        //initial small messages between client and server to set up the game

        ServerSocket finalServerSocket = serverSocket;
        new Thread(() -> {
            while (true){
                try{
                    Socket mainSocket = finalServerSocket.accept();
                    //look for a match between two sockets, look in socketsPairs to see if there is an identifier with both main and secondary sockets registered
                    lookForMatchedSockets(mainSocket, true);
                }catch(IOException e){
                    e.printStackTrace();//case if serversocket is unavailable
                }
            }
        }).start();

        ServerSocket finalSecondaryServerSocket = secondaryServerSocket;
        new Thread(() -> {
            while (true){
                try{
                    Socket secondarySocket = finalSecondaryServerSocket.accept();
                    //look for a match between two sockets, look in socketsPairs to see if there is an identifier with both main and secondary sockets registered
                    lookForMatchedSockets(secondarySocket, false);
                }catch(IOException e){
                    e.printStackTrace();//case if serversocket is unavailable
                }
            }
        }).start();
    }

    private synchronized void lookForMatchedSockets(Socket newSocket, Boolean isPrimary){

        if(isPrimary){
            allmainSockets.add(newSocket);
            askTheIdentifierAndAddToThePairsList_main(newSocket);
        }

        else{
            allSecondarySockets.add(newSocket);
            askTheIdentifierAndAddToThePairsList_secondary(newSocket);
        }

        List<Triplet<String, Socket, Socket>> socketsMatched = findMatchedSockets(alreadyConnectedClientsIdentifiers);
        for (Triplet<String, Socket, Socket> socketMatched : socketsMatched) {
            System.out.println("Accepted Client");
            Socket socket = socketMatched.getSecond();

            //add the identifier of the client to the list of the previously added clients
            alreadyConnectedClientsIdentifiers.add(socketMatched.getFirst());

            //START THE PING PONG WITH THIS CLIENT
            PingHandlerServerSide pingHandlerServerSide = new PingHandlerServerSide(socketMatched.getThird());
            new Thread(pingHandlerServerSide).start();

            new Thread(new innerClassInitialConnectionSupport(socket, pingHandlerServerSide)).start();
        }


        //check if there are any controllers whose game is over, in that case erase them and their virtual view
        List<Controller> controllerToRemove = new ArrayList<>();
        for (Controller c : controllerPool) {
            if (c.hasSessionEnded()) {
                virtualViewList.remove(virtualViewList.get(controllerPool.indexOf(c)));
                controllerToRemove.add(c);
            }
        }
        for (Controller c : controllerToRemove) {
            controllerPool.remove(c);
        }
    }


    /**
     * The type Inner class initial connection support.
     */
    class innerClassInitialConnectionSupport implements Runnable,Observable<ServerSideProtocol>{
        private Socket socket;
        private List<Observer<ServerSideProtocol>> observerListToSendTheServerSideProtocol;
        private ServerSideProtocol serverSideProtocolCreated;

        /**
         * Instantiates a new Inner class initial connection support.
         *
         * @param socket                the socket
         * @param pingHandlerServerSide the ping handler server side
         */
        public innerClassInitialConnectionSupport(Socket socket, PingHandlerServerSide pingHandlerServerSide){
            this.socket=socket;
            this.observerListToSendTheServerSideProtocol=new ArrayList<>();
            this.observerListToSendTheServerSideProtocol.add(pingHandlerServerSide);
        }

        @Override
        public void run() {
            //instantiates reader and writer
            TextReader textReader=new TextReader(socket);
            TextWriter textWriter=new TextWriter(socket);

            String username;
            boolean insertedCorrectedInputs = false;

            try{
                //until the username(and number of players) are set correctly, ask the the user to insert them
                do {
                    textWriter.send(GameConstants.WELCOME_ACTION);
                    String input="";
                    input= textReader.receive();


                    //##########JOIN - the player wants to enter a new session or join a not full one if possible
                    if (input.equals(GameConstants.NEW_CONNECTION)) {
                        boolean areAllSessionFull = true;
                        int numberOfPlayers = 0;
                        Controller controllerNotFull=null;

                        for (Controller c : controllerPool) {if (!c.isSessionFull() && !c.hasSessionEnded()) {areAllSessionFull = false;controllerNotFull=c;} }

                        //if all the already created sessions are full, create a new one, otherwise add the player to an incomplete one
                        if (areAllSessionFull) {
                            textWriter.send(GameConstants.HOST_PLAYER_MESSAGE);
                            username="";
                            String[] parts = textReader.receive().split("-");
                            if(parts.length > 2){
                                StringBuilder usernameBuffer = new StringBuilder();
                                for(int i = 0; i < parts.length-1; i++) {
                                    usernameBuffer.append(parts[i]);
                                    if(i != parts.length-2){
                                        usernameBuffer.append('-');
                                    }
                                }
                                username = usernameBuffer.toString();
                            }
                            else{
                                username = parts[0];
                            }
                            numberOfPlayers = Integer.parseInt(parts[parts.length-1]);
                        }
                        else {
                            textWriter.send(GameConstants.GUEST_PLAYER_MESSAGE);
                            username = textReader.receive();
                        }

                        //check if the username is already present or not,
                        boolean username_already_in_use = false;
                        for (Controller c : controllerPool) {
                            if(c.isUsernameAlreadyPresent(username)) {username_already_in_use=true;}
                        }
                        // if all inputs are OK, add player to session/ create session, otherwise send 'KO'
                        if ((username_already_in_use)||(numberOfPlayers > GameConstants.MAX_PLAYER_COUNT)){
                            textWriter.send("KO");
                            textReader.receive();
                        }

                        else{//if the inserted inputs are correct, create a new controller+virtualview in case of a new game, otherwise it adds a new player
                            insertedCorrectedInputs=true;
                            textWriter.send(GameConstants.ACK);
                            if(areAllSessionFull){  //CASE FIRST PLAYER IN A NEW SESSION
                                //CREATE A NEW CONTROLLER and add the player
                                Controller controller=new Controller(username,numberOfPlayers);
                                controller.submitConnection(username);
                                controllerPool.add(controller);


                                //CREATE VIRTUAL VIEW(and server side protocol automatically)
                                VirtualView virtualView=new VirtualView();
                                virtualViewList.add(virtualView);
                                virtualView.addObserver(controller);
                                serverSideProtocolCreated=virtualView.submitConnection(username,socket);
                                notify(serverSideProtocolCreated);      //notify the ping handler of the server side protocol created

                                //ADD the virtualview to the list of observers of the model (created with the Controller)
                                controller.addObserverToTheModel(virtualView);

                                //if, after adding the new player, the session is full, call 'run' from all the server side protocols of the players
                                if(controller.isSessionFull()) {
                                    virtualViewList.get(virtualViewList.indexOf(virtualView)).startAllPlayers();

                                    if(numberOfPlayers == 1)
                                        controller.resendState();
                                }

                            }
                            else{       //CASE NOT THE FIRST PLAYER, ADD TO PREEXISTING SESSION

                                //new server side protocol
                                int index=controllerPool.indexOf(controllerNotFull);
                                serverSideProtocolCreated=virtualViewList.get(index).submitConnection(username,socket);
                                notify(serverSideProtocolCreated);   //notify the ping handler of the server side protocol created

                                //add a player to the game (in the model)
                                controllerNotFull.submitConnection(username);

                                //if, after adding the new player, the session is full, call 'run' from all the server side protocols of the players
                                if(controllerNotFull.isSessionFull()) {
                                    virtualViewList.get(index).startAllPlayers();
                                }
                            }
                        }
                    }

                    //#######RECONNECTION - the player wants to reconnect to his previous session
                    else { textWriter.send(GameConstants.GUEST_PLAYER_MESSAGE);
                        username="";
                        username = textReader.receive();

                        //check if the username is already present or not, in case save the controller of the session to reconnect to in 'controllerToReconnectTo'
                        boolean username_already_in_use = false;
                        Controller controllerToReconnectTo= null;
                        for (Controller c : controllerPool) { if(c.getDisconnectedPlayers().contains(username)){username_already_in_use=true;controllerToReconnectTo=c;}}

                        //if the session of the user is still active, reconnect the user to the game, otherwise ask him if he wants to join a new game
                        if (username_already_in_use){
                            //remove the name of the player from the Disconnected Player List
                            controllerToReconnectTo.getDisconnectedPlayers().remove(username);

                            //connect the previous server side protocol(AND MAKE IT RUN) to the new socket of the player
                            int index=controllerPool.indexOf(controllerToReconnectTo);
                            serverSideProtocolCreated=virtualViewList.get(index).reconnectPlayer(username,socket);
                            notify(serverSideProtocolCreated);

                            textWriter.send(GameConstants.ACK);insertedCorrectedInputs=true;
                        }
                        else {textWriter.send(GameConstants.NACK);try{textReader.receive();}catch(NoSuchElementException ignored){}}
                        //this final scanner.next just to read the "KO" from the user
                    }
                }
                while(!insertedCorrectedInputs);
            }catch (NoSuchElementException e){
                System.out.println("Initial Connection not completed");
            }
        }

        @Override
        public void addObserver(Observer<ServerSideProtocol> observer) {
            observerListToSendTheServerSideProtocol.add(observer);
        }

        @Override
        public void removeObserver(Observer<ServerSideProtocol> observer) {
            observerListToSendTheServerSideProtocol.add(observer);
        }

        //notify to the ping Handler the serverSideProtocol matched to his client
        @Override
        public void notify(ServerSideProtocol message) {
            for (Observer<ServerSideProtocol> observer : observerListToSendTheServerSideProtocol) {
                observer.update(serverSideProtocolCreated);
            }
        }
    }


    /**
     * The type Ping handler server side.
     */
//TO HANDLE PING WITH CLIENT. INNER CLASS AND NOT SEPARATE BECAUSE IT USES 'SOCKET PAIRS LIST' IN CASE OF DISCONNECTION BEFORE THE CREATION OF A SERVER SIDE PROTOCOL
    public class PingHandlerServerSide implements Runnable, Observable<String>, Observer<ServerSideProtocol> {
        private Socket secondarySocket;
        private boolean receivedPing;

        List<Observer<String>> observerList;
        ServerSideProtocol serverSideProtocolPairedToMyClient;
        Timer timer;

        /**
         * Instantiates a new Ping handler server side.
         *
         * @param secondarysocket the secondarysocket
         */
        public PingHandlerServerSide(Socket secondarysocket){
            this.secondarySocket=secondarysocket;
            this.observerList=new ArrayList<>();
        }

        @Override
        public void run() {
            TextReader textReader=new TextReader(secondarySocket);
            TextWriter textWriter=new TextWriter(secondarySocket);

            while(true) {

                textWriter.send("PING");

                receivedPing=false;
                timer=new Timer();
                MyPingTaskServerSide myPingTaskServerSide= new MyPingTaskServerSide();
                //For debug
                //timer.schedule(myPingTaskServerSide,1000000000);
                timer.schedule(myPingTaskServerSide,5000);     //IF AFTER 5 SECONDS THE CLIENT DOES NOT PING BACK IT MEANS THAT IT'S DISCONNECTED
                try{textReader.receive();
                    receivedPing=true;
                    timer.purge();timer.cancel();}
                catch(Exception e){break;}


                receivedPing=false;
            }

        }

        /**
         * Receive the ServerSideProtocol matched to my client, that we have to notify in case of disconnection
         * @param message serversideProtocol received from the observable
         */
        @Override
        public void update(ServerSideProtocol message) {
            serverSideProtocolPairedToMyClient=message;
            addObserver(message);
        }

        /**
         * The type My ping task server side.
         */
        //INTERNAL CLASS FOR THE TASK TO DO AFTER 1 SEC (WRITE PING BACK)
        public class MyPingTaskServerSide extends TimerTask {
            @Override
            public void run() {
                if(!receivedPing){
                    System.out.println("Ping time ran out: Client disconnected");
                    PingHandlerServerSide.this.notify("DISCONNECTION");
                    timer.purge();timer.cancel();
                }
            }
        }

        @Override
        public void addObserver(Observer observer) {
            observerList.add(observer);
        }

        @Override
        public void removeObserver(Observer observer) {
            observerList.remove(observer);
        }

        @Override
        public void notify(String message) {
            if(!observerList.isEmpty()) {
                for (Observer<String> observer : observerList) {
                    System.out.println("");
                    observer.update(message);
                }
            }

            Optional<Triplet<String, Socket, Socket>> tripletToDeleteOPT=socketsPairs.stream().filter((x)->(x.getThird()!= null && x.getThird().equals(secondarySocket))).findFirst();
            if(tripletToDeleteOPT.isPresent()) {
                Triplet<String, Socket, Socket> tripletToDelete = tripletToDeleteOPT.get();
                try {
                    tripletToDelete.getSecond().close();
                    tripletToDelete.getThird().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                socketsPairs.remove(tripletToDelete);
            }

            }
    }



    //-----------------------methods used to handle the socketPairsList-------------------------------------------------



    /**
     * Find the match (player, mainSocket, pingSocket) for each connected id
     * @param alreadyConnectedClientsIdentifiers IDs of the already connected players
     * @return a triplet that contains identifier, mainSocket, secondarySocket
     */
    private List<Triplet<String, Socket, Socket>> findMatchedSockets(List<String> alreadyConnectedClientsIdentifiers) {
        List<Triplet<String, Socket, Socket>> fullTriplet=socketsPairs.stream().filter((x)->((x.getFirst()!=null)&&(x.getSecond()!=null)&&(x.getThird()!=null)&&(!alreadyConnectedClientsIdentifiers.contains(x.getFirst())))).collect(Collectors.toList());
        return fullTriplet;
    }


    /**
     * Receives an identifier in a certain mainSocket and saves it
     * @param mainSocket the main socket
     */
    private void askTheIdentifierAndAddToThePairsList_main(Socket mainSocket) {
        TextReader textReader=new TextReader(mainSocket);

        try{
            Timer mytimer=new Timer();
            TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    try {
                        mainSocket.close();
                    } catch (IOException ignored) { }
                    System.out.println("Primary identifier not received");
                    allmainSockets.remove(mainSocket);
                }
            };
            mytimer.schedule(timerTask,1000);
            String identifierInString= textReader.receive();
            mytimer.purge();mytimer.cancel();
            System.out.println("Main socket identifier was received:"+identifierInString);

            //search the identifier between the already present ones
            if(extractAllTheIdentifiers().contains(identifierInString)){
                //if the identifier is already present,add the main socket in his triplet
                Optional<Triplet<String, Socket, Socket>> alreadyPresentTripletOPTIONAL= socketsPairs.stream().filter((x)->(x.getFirst().equals(identifierInString))).findFirst();
                Triplet<String, Socket, Socket> alreadyPresentTriplet=alreadyPresentTripletOPTIONAL.get();
                alreadyPresentTriplet.setSecond(mainSocket);
            }
            else{
                Triplet<String,Socket,Socket> tripletWithNewIdentifier=new Triplet<>(identifierInString,mainSocket, null);
                socketsPairs.add(tripletWithNewIdentifier);
            }}
        catch(NoSuchElementException e){allmainSockets.remove(mainSocket);}
    }


    /**
     * Receives an identifier in a certain secondarySocket and saves it
     * @param secondarySocket the secondary socket
     */
    private void askTheIdentifierAndAddToThePairsList_secondary(Socket secondarySocket) {
        TextReader textReader=new TextReader(secondarySocket);

        try {
            Timer mytimer=new Timer();
            TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    try {secondarySocket.close(); } catch (IOException ignored) { }
                    System.out.println("Secondary identifier not received");
                    allSecondarySockets.remove(secondarySocket);
                }
            };
            mytimer.schedule(timerTask,1000);
            String identifierInString = textReader.receive();
            mytimer.purge();mytimer.cancel();

            System.out.println("Secondary socket identifier was received:" + identifierInString);

            //search the identifier between the already present ones
            if (extractAllTheIdentifiers().contains(identifierInString)) {
                //if the identifier is already present,add the secondary socket in his triplet
                Optional<Triplet<String, Socket, Socket>> alreadyPresentTripletOPTIONAL = socketsPairs.stream().filter((x) -> (x.getFirst().equals(identifierInString))).findFirst();
                Triplet<String, Socket, Socket> alreadyPresentTriplet = alreadyPresentTripletOPTIONAL.get();
                alreadyPresentTriplet.setThird(secondarySocket);

            } else {
                Triplet<String, Socket, Socket> tripletWithNewIdentifier = new Triplet<>(identifierInString, null, secondarySocket);
                socketsPairs.add(tripletWithNewIdentifier);
            }
        }
        catch(NoSuchElementException e){allSecondarySockets.remove(secondarySocket);}
    }

    /**
     * Isolates the identifiers of the clients
     *
     * @return list of all the identifiers of the clients
     */
    public List<String> extractAllTheIdentifiers(){
        return socketsPairs.stream().map(Triplet::getFirst).collect(Collectors.toList());
    }




}
