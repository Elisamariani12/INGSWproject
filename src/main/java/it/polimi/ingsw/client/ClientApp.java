package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.common.util.CardRepository;
import it.polimi.ingsw.common.util.GameConstants;

import javax.swing.text.View;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("ALL")
public class ClientApp {

    private static View view;
    private static Socket clientSocket;
    private static Socket clientSecondarySocket;
    private static ClientSideProtocol clientSideProtocol;
    private static CLI cli;
    private static String ip = "";
    public static String secondaryIP = "";
    public static int port = 50000;
    public static int secondaryPort = 50001;

    public static void main(String[] args){
        CardRepository cardRepository = CardRepository.getInstance();
        cardRepository.loadAllData();

        //Turn args into arg list
        List<String> argList = new ArrayList<>();
        for(String arg : args) argList.add(arg);

        boolean isCLI = argList.stream().anyMatch(s -> s.equals("-nogui") || s.equals("-cli"));

        for(String arg : argList)
        {
            try
            {
                if (arg.contains("-port1:"))
                    port = Integer.valueOf(arg.substring(7));

                if (arg.contains("-port2:"))
                    secondaryPort = Integer.valueOf(arg.substring(7));

                if (arg.contains("-ipPing:"))
                    secondaryIP = arg.substring(8);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Sorry, the port / secondary ip ovverride command could not be performed.");
                System.exit(-1);
            }
        }

        if(isCLI)
        {
            //Welcome message for the player
            System.out.println(GameConstants.INITIAL_CLIENT_WELCOME_MESSAGE);

            boolean connectionEstablished = false;

            //Find a server to connect to
            while(!connectionEstablished)
            {
                ip = askForServerIpAddress();

                try
                {
                    clientSocket = new Socket(ip, port);
                    clientSecondarySocket=new Socket(secondaryIP.isBlank() ? ip : secondaryIP,secondaryPort);
                    connectionEstablished = true;
                }
                catch (IOException e)
                {
                    System.out.println(GameConstants.CONNECTION_FAILED_MESSAGE);
                    connectionEstablished = false;
                }
            }

            clientSideProtocol = new ClientSideProtocol(clientSocket,clientSecondarySocket);
            cli = new CLI();
            clientSideProtocol.addObserver(cli);
            cli.addObserver(clientSideProtocol);
            new Thread(clientSideProtocol).start();
        }
        else
        {
            new GUIMessageHandler();
        }

    }

    /** Returns an IP address that matches the conventional IPv4 format
     * @return Correctly formatted IP address as String
     */
    public static String askForServerIpAddress()
    {
        Scanner systemTeletype = new Scanner(System.in);
        String ipAddress = "";
        boolean isValid = false;

        System.out.print(GameConstants.IP_ADDRESS_REQUEST);
        ipAddress = systemTeletype.nextLine();

        return ipAddress;
    }
}
