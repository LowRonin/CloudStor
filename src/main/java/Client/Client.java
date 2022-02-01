package Client;


import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;


public class Client implements Initializable {
    private static Socket socket;
    private static Path dir = ClientConst.DEFAULT_DIR_PATH;
    private static DataInputStream iS = null;
    private static DataOutputStream oS = null;
    public static boolean watcherTrigger;
    static byte[] clientBuffer = new byte[8192];
    public ProgressBar serverPB;
    public ProgressBar clientPB;
    public TextField serverPath;
    public TextField clientPath;
    public ListView clientView;
    public ListView srvView;


    // FIXME: 01.02.2022 Добавить Watcher для обновления списков
    // FIXME: 01.02.2022 Добавить изменение пути
    // FIXME: 01.02.2022 Попробовать перевести в TreeView Files.walkFileTree()
    private  void open_connection() {
        try {
            socket = new Socket("localhost", 35555);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            iS = new DataInputStream(socket.getInputStream());
            oS = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void upload(ActionEvent actionEvent) throws IOException {
        oS.writeUTF("/upload");
        FileInputStream fIS = null;
        try {
            String fileName = clientView.getSelectionModel().getSelectedItem().toString();
            oS.writeUTF(fileName);
            fIS = new FileInputStream(dir.resolve(fileName).toFile());
        } catch (FileNotFoundException e) {
            System.out.println("Path not found");
            e.printStackTrace();
        }
        try {
            int count = fIS.available();
            while ((fIS.available()) > 0) {
                fIS.read(clientBuffer, 0, count);
                oS.write(clientBuffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void download(ActionEvent actionEvent) throws IOException {

        try {
            oS.writeUTF("/download");
            FileOutputStream fOS = null;
            String fileName = srvView.getSelectionModel().getSelectedItem().toString();
            oS.writeUTF(fileName);
            fOS = new FileOutputStream(dir.resolve(fileName).toFile());
            int count = iS.available();
            while ((iS.available()) > 0) {
                iS.read(clientBuffer, 0, count);
                fOS.write(clientBuffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(ActionEvent actionEvent) {
        fillListView();
    }

    public void clientPath(ActionEvent actionEvent) {
        try {
            dir = (Path) clientPath;
        } catch (Exception e) {
            System.out.println("Path not found");
        }
    }

    public void fillListView() {
        try {
            clientView.getItems().clear();
            srvView.getItems().clear();
            oS.writeUTF("/update");
            Files.list(dir)
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));

           int count = iS.readInt();
            while (count != 0) {
                String fileName = iS.readUTF();
                srvView.getItems().add(fileName);
                count--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fillPath() {
        try {
            clientPath.clear();
            serverPath.clear();
            clientPath.appendText(String.valueOf(dir));
            oS.writeUTF("/fillPath");
            serverPath.appendText(iS.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void watcherListener(boolean watcherTrigger){
        if (watcherTrigger){
            fillListView();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        open_connection();
        fillListView();
        fillPath();
        new Thread(() -> {
            {
                try {
                    new Watcher(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            try {
                watcherListener(watcherTrigger);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void svrBackPath(ActionEvent actionEvent) {
        String buf = serverPath.getText();
        int position = -1;
        for (int i = buf.length(); position < 0; i-- ){
            position = buf.indexOf("\\", i);
        }
        serverPath.clear();
        serverPath.appendText(buf.substring(0, position));
        try {
            oS.writeUTF("/setPath");
            oS.writeUTF(serverPath.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientBackPath(ActionEvent actionEvent) {
        String buf = clientPath.getText();
        int position = -1;
        for (int i = buf.length(); position < 0; i-- ){
           position = buf.indexOf("\\", i);
        }
        clientPath.clear();
        clientPath.appendText(buf.substring(0, position));
        dir = Paths.get(clientPath.getText());
    }

    public void setSvrPath(ActionEvent actionEvent) throws IOException {
        oS.writeUTF("/setPath");
        oS.writeUTF(serverPath.getText());
    }

    public void setClientPath(ActionEvent actionEvent) {
        dir = Paths.get(clientPath.getText());
        System.out.println(dir);
        System.out.println(dir.toString());
    }

}
