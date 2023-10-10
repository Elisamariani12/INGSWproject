package it.polimi.ingsw.server;

import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.server.controller.ConnectionHelper;

/**
 * Main class of the server
 */
public class ServerApp {
    private static ConnectionHelper connectionHelper;
    private static int port = 50000;
    private static int secondaryPort = 50001;

    @SuppressWarnings("JavaDoc")
    public static void main(String[] args)
    {
        CardRepository cardRepository = CardRepository.getInstance();
        cardRepository.loadAllData();

        for(String arg : args)
        {
            if(arg.contains("-port1:"))
                port = Integer.valueOf(arg.substring(7));

            if(arg.contains("-port2:"))
                secondaryPort = Integer.valueOf(arg.substring(7));
        }

        connectionHelper = new ConnectionHelper(port,secondaryPort);
        System.out.println("Starting server");
        connectionHelper.startServer();

    }
}
