package it.polimi.ingsw.galaxytrucker.network.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Utility class for networking constants and methods.
 */
public class NetworkingUtils {
    public static final String LOOPBACK_ADDRESS = "127.0.0.1";
    public static final int SOCKET_DEFAULT_PORT = 5000;
    public static final int RMI_DEFAULT_PORT = 1099;

    /**
     * Gets the IP of a network interface that can reach the internet.
     * @return The IP, or <code>127.0.0.1</code> if the machine cannot reach the internet.
     * @author Alessandro Giuseppe Gioia
     */
    public static String getLocalIP(){
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("google.com", 80));
            return socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            System.err.println("Could not get local IP address: " + e.getMessage());
            return LOOPBACK_ADDRESS;
        }
    }
}
