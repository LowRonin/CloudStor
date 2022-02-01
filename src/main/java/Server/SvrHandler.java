package Server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class SvrHandler implements Runnable {
    private DataInputStream iS = null;
    private DataOutputStream oS = null;
    private final byte[] svrBuffer = new byte[8192];
    private Path dir = SvrConst.DEFAULT_DIR_PATH;
    String command = null;

    // FIXME: 01.02.2022 Сделать кнопки неактивными до завершения оперции
    // FIXME: 01.02.2022 Добавить кнопку удаления Files.delete()
    // FIXME: 01.02.2022 Переделать через socketChanel
    public SvrHandler(Socket socket) {
        try {
            iS = new DataInputStream(socket.getInputStream());
            oS = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                command = iS.readUTF();
                if (command.equals("/upload")) {
                    download(svrBuffer);
                }
                if (command.equals("/download")) {
                    upload(svrBuffer);
                }
                if (command.equals("/update")) {
                    update();
                }
                if (command.equals("/fillPath")){
                    fillPath();
                }
                if (command.equals("/setPath")){
                    setPath();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void download(byte[] buffer) {
        FileOutputStream fOS = null;
        try {
            String fileName = iS.readUTF();
            fOS = new FileOutputStream(dir.resolve(fileName).toFile());
        } catch (FileNotFoundException e) {
            System.out.println("Path not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File name lose");
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
        System.out.println(command);
    }

    public synchronized void upload(byte[] buffer) throws IOException {
        FileInputStream fIS = null;
        try {
            String fileName = iS.readUTF();
            fIS = new FileInputStream(dir.resolve(fileName).toFile());
        } catch (FileNotFoundException e) {
            System.out.println("Path not found");
            e.printStackTrace();
        }
        try {
            assert fIS != null;
            int count = fIS.available();
            while ((fIS.available()) > 0) {
                fIS.read(buffer, 0, count);
                oS.write(buffer, 0, count);
                oS.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(command);
    }

    public synchronized void update() {
        try {
            List<String> files = Files.list(dir)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
            oS.writeInt(files.size());
            for (String file : files) {
                oS.writeUTF(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(command);
    }

    public void fillPath() {
        try {
            oS.writeUTF(String.valueOf(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPath(){
        try {
            dir = Paths.get(iS.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(command);
    }

}
