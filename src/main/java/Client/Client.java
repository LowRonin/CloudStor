package Client;


import Server.SvrConst;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;


public class Client extends Application {
    private static Socket socket;
    private Path clientDir;
    private static InputStream iS = null;
    private static DataOutputStream oS = null;
    static byte[] clientBuffer = new byte[8192];

    public ProgressBar serverPB;
    public ProgressBar clientPB;
    public TextField serverPath;
    public TextField clientPath;
    public ListView clientView;
    public ListView srvView;


    public static void main(String[] args)  {
        open_connection();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/GUI/WorkingPanel.fxml"));
        System.out.println(parent.toString());
        stage.setScene(new Scene(parent));
        stage.show();
    }


    private static void open_connection(){
        try {
            socket = new Socket("localhost", 35666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            iS = socket.getInputStream();
            oS = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }// создать потоки для каждого пользователя
    }

    public synchronized void upload(ActionEvent actionEvent) throws IOException {
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
                    fIS.read(clientBuffer, 0, count);
                    oS.write(clientBuffer, 0, count);
                    oS.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public synchronized void download(ActionEvent actionEvent) {
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
                        iS.read(clientBuffer, 0, count);
                        fOS.write(clientBuffer, 0, count);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
