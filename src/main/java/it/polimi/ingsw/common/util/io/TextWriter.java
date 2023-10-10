package it.polimi.ingsw.common.util.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The type Text writer.
 */
public class TextWriter {
    private PrintWriter sender;
    private final String escapeCode = "-q";

    /**
     * Instantiates a new Text writer.
     *
     * @param socket the socket
     */
    public TextWriter(Socket socket) {
        try {
            sender = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send formatted string.
     *
     * @param string the string
     */
    public void send(String string){
        sender.print(string);
        if(string.charAt(string.length()-1) == '\n'){
            sender.print(escapeCode+"\n");
        }
        else{
            sender.print("\n"+escapeCode+"\n");
        }
        sender.flush();
    }
}
