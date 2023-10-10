package it.polimi.ingsw.server.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runnable class to quit the server when received a specific input
 */
public class ServerClosingThread implements Runnable {
    private Scanner scanner;
    private ArrayList<Socket> sockets;

    /**
     * Instantiates a new Server closing thread.
     *
     * @param socketList the socket list
     */
    public ServerClosingThread(ArrayList<Socket> socketList) {
        this.scanner = new Scanner(System.in);
        sockets = socketList;
    }

    @Override
    public void run() {
        while(true){
            String input = scanner.nextLine();
            if(input.equalsIgnoreCase("Quit")){
                System.out.println("Quitting Server");
                for(Socket socket : sockets){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Socket closing error");
                    }
                }
                break;
            }

        }
        System.exit(123);
    }
}
