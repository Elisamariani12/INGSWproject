package it.polimi.ingsw.common.util.io;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * The type Text reader.
 */
public class TextReader {
    private Scanner receiver;
    private final String escapeCode = "-q";

    /**
     * Instantiates a new Text reader.
     *
     * @param socket the socket
     */
    public TextReader(Socket socket) {
        try {
            receiver = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive as-sender-formatted string from socket.
     *
     * @return the string
     */
    public String receive(){
        String line;
        StringBuilder text = new StringBuilder("");

        do{
            line = receiver.nextLine();
            if(!line.equals(escapeCode)){
                text.append(line + "\n");
            }
        }while(!line.equals(escapeCode));
        text.deleteCharAt(text.length()-1);
        return text.toString();
    }
}
