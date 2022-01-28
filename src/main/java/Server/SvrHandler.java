package Server;

import java.io.*;
import java.net.Socket;


public class SvrHandler implements Runnable {
    private InputStream iS = null;
    private FileOutputStream fOS = null;
    private byte[] svrBuffer = new byte[8192];

    public SvrHandler(Socket socket) {
        try {
            iS = socket.getInputStream();
            fOS = new FileOutputStream(SvrConst.SRV_DIR_PATH.toFile());
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
//                fOS.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


}

    @Override
    public void run() {
        while (true) {
            file_catcher(svrBuffer, iS);
        }
    }

    public void file_catcher(byte[] buffer, InputStream iS) {

        FileOutputStream fOS = null;
        try {
            fOS = new FileOutputStream(SvrConst.SRV_DIR_PATH.toFile());
        } catch (FileNotFoundException e) {
            System.out.println("Path not found");
            e.printStackTrace();
        }
        try {
            int count = buffer.length;
            while ((iS.available()) > 0) {
                iS.read(buffer,0,count);
                byte[] a = buffer;
                fOS.write(buffer, 0, count);
            }
            System.out.println("File catch");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void file_pitcher() {
    }
}
