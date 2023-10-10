package it.polimi.ingsw;

import it.polimi.ingsw.common.util.io.TextReader;
import it.polimi.ingsw.common.util.io.TextWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InOutTest {
    Socket clientSocket;
    ServerSocket serverMainSocket;
    Socket serverHelperSocket;
    TextReader reader;
    TextWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        serverMainSocket = new ServerSocket(1005);
        clientSocket = new Socket("127.0.0.1", 1005);
        serverHelperSocket = serverMainSocket.accept();
        //Instantiates new reader(receiver) e writer(sender), argument is socket where it has to receive/send
        reader = new TextReader(clientSocket);
        writer = new TextWriter(serverHelperSocket);
    }

    @Test
    void readAndWrite() {
        String stringa = "Ciao \n ciao \n ciaociao";
        //Server send stringa over the socket
        writer.send(stringa);
        //Client receive the string stringa as previously formatted by server, no changes at all
        assertEquals(stringa, reader.receive());

    }
}
