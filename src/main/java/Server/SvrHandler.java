package Server;

import Client.ClientConst;

import java.io.*;
import java.net.Socket;


public class SvrHandler implements Runnable {
    private DataInputStream iS = null;
    private DataOutputStream oS = null;
    private byte[] svrBuffer = new byte[8192];

    public SvrHandler(Socket socket) {
        try {
            iS = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            oS = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            while (true) {
                if (iS.readUTF().equals("/upload")) {
                    try {
                        file_catcher(svrBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public synchronized void file_catcher(byte[] buffer) throws IOException {
        while (true) {
            {
                FileOutputStream fOS = null;
                try {
                    fOS = new FileOutputStream(SvrConst.SRV_DIR_PATH.toFile());
                } catch (FileNotFoundException e) {
                    System.out.println("Path not found");
                    e.printStackTrace();
                }
                try {
                    int count = iS.available();
                    while ((iS.available()) > 0) {
                        iS.read(buffer, 0, count);
                        fOS.write(buffer, 0, count);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }

    public synchronized void file_pitcher(byte[] buffer) throws IOException {
        oS.writeUTF("/upload");
        FileInputStream fIS = null;
        try {
            fIS = new FileInputStream(ClientConst.CLIENT_DIR_PATH.toFile());
        } catch (FileNotFoundException e) {
            System.out.println("Path not found");
            e.printStackTrace();
        }
        try {
            int count = fIS.available();
            while ((fIS.available()) > 0) {
                fIS.read(buffer, 0, count);
                oS.write(buffer, 0, count);
                oS.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnection(){
        try {
            iS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
