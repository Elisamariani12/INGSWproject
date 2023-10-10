package it.polimi.ingsw.client;

import it.polimi.ingsw.common.util.io.TextReader;
import it.polimi.ingsw.common.util.io.TextWriter;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The type Ping handler client side.
 */
public class PingHandlerClientSide implements Runnable{

    private Socket secondarySocket;
    private TextReader textReader;
    private TextWriter textWriter;

    /**
     * Instantiates a new Ping handler client side.
     *
     * @param secondarysocket the secondarysocket to receive the pings
     */
    public PingHandlerClientSide(Socket secondarysocket){
        this.secondarySocket=secondarysocket;
        this.textReader=new TextReader(secondarySocket);
        this.textWriter=new TextWriter(secondarySocket);
    }

    @Override
    public void run() {
        while(true) {
            try{textReader.receive();
                Timer timer=new Timer();
                MyPingTaskClientSide myPingTaskClientSide=new MyPingTaskClientSide();
                timer.schedule(myPingTaskClientSide,1000);
            }
            //if the socket closes but the client is still active, end the game session, he should try to reconnect
            catch(Exception e){
                System.out.println("Unstable connection, you should try to reconnect.");
                System.exit(123);
            }
        }
    }

    /**
     * The type My ping task client side.
     */
//INTERNAL CLASS FOR THE TASK TO DO AFTER 1 SEC (WRITE PING BACK)
    public class MyPingTaskClientSide extends TimerTask {
        @Override
        public void run() {
            textWriter.send("PING");
        }
    }
}
