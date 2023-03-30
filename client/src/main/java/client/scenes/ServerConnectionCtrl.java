package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ServerConnectionCtrl {
    private final ServerUtils server;
    private final MainCtrlTalio mainCtrlTalio;
    @FXML
    private TextField fieldServerAddress;

    @FXML
    private Button buttonConnect;

    @Inject
    public ServerConnectionCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void clickConnectServer() {
        String serverAddressText = fieldServerAddress.getText();
        if (server.setServerUrl(serverAddressText)) {
            mainCtrlTalio.showHome();
        } else {
            Alert box = new Alert(Alert.AlertType.ERROR);
            box.setTitle("Wrong server");
            box.setContentText("Wrong server address!");
            box.showAndWait();
        }
    }

    @FXML
    private void setDefaultServer() {
        fieldServerAddress.setText("http://localhost:8080/");
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }
}
