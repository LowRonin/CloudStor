package Client;

import Server.SvrConst;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

public class ClientHandler implements Runnable {

    private TreeView<String> clientTree;
    private Path clientDir;
    private InputStream iS = null;
    private OutputStream oS = null;
    byte[] clientBuffer = new byte[8192];

    public ClientHandler(Socket socket) {
        try {
            iS = socket.getInputStream();
            oS = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                iS.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                oS.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
    }

    @Override
    public void run() {
        while (true) {
            file_pitcher(clientBuffer);
        }
    }
//   public autorization(){
//    }
//
//    public file_catcher(){
//
//    }
//
    public void file_pitcher(byte[] buffer){
        FileInputStream fIS = null;
        try {
            fIS = new FileInputStream(ClientConst.CLIENT_DIR_PATH.toFile());
        } catch (FileNotFoundException e) {
            System.out.println("Path not found");
            e.printStackTrace();
        }
        try {
            int count = buffer.length;
            while ((fIS.available()) > 0){
                fIS.read(buffer, 0, count);
                byte[] a = buffer;
                oS.write(buffer,0,count);
                oS.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 //   private appClose {
  //  }
}
