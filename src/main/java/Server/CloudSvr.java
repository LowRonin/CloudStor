package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CloudSvr {
    private static ServerSocket server = null;
    private static Socket socket = null;
    private static DataInputStream in = null;
    private static DataOutputStream out = null;

    public static void main(String[] args) throws IOException {
            server = new ServerSocket(35666);
        while (true) {
            socket = server.accept();
            System.out.println("New client connection...");
            new Thread(new SvrHandler(socket)).start();
        }

    }
}
