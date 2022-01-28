package Client;

import GUI.GUI;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private static Socket socket;
    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 35666 );
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            new Thread(new ClientHandler(socket)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GUI.main(args);



    }
}
