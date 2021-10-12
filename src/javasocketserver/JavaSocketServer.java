package javasocketserver;

/**
 *
 * @author 30039802 Caspian Maclean
 * 
 * 
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaSocketServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting");
        int requestPortNumber = 1234;
        try (ServerSocket serverSocket = new ServerSocket(requestPortNumber)) {
            System.out.println("The server is listening on port " + serverSocket.getLocalPort());
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("The server is connected");
                
            }
            
            
        } catch (IOException e) {
            System.err.println("IO Exception: " + e);
        }
        System.out.println("Finished");

    }
    
}
