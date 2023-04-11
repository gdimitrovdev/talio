package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ServerConnectionCtrl {
    private final ServerUtils server;
    private final MainCtrlTalio mainCtrlTalio;
    @FXML
    public TextField fieldServerAddress;

    @FXML
    private Button buttonConnect;
    @FXML
    private Button buttonBack;

    public void setIsConnected(boolean isConnected) {
        this.isConnected.set(isConnected);
    }

    public boolean isIsConnected() {
        return isConnected.get();
    }

    public BooleanProperty isConnectedProperty() {
        return isConnected;
    }

    private BooleanProperty isConnected = new SimpleBooleanProperty(false);

    @Inject
    public ServerConnectionCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void initialize() {
        buttonBack.visibleProperty().bind(isConnected);
    }

    public void clickConnectServer() {
        String serverAddressText = fieldServerAddress.getText();
        if (server.setServerUrl(serverAddressText)) {
            isConnected.set(true);
            mainCtrlTalio.showHome();
        } else {
            Alert box = new Alert(Alert.AlertType.ERROR);
            box.setTitle("Server Error");
            box.setContentText("Problem connecting with the server.\nPlease make sure that you "
                    + "entered the URL correctly.");
            box.showAndWait();
        }
    }

    @FXML
    private void setDefaultServer() {
        fieldServerAddress.setText("http://localhost:8080/");
        fieldServerAddress.requestFocus();
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }

    public void refreshServerAddress() {
        if (isConnected.get()) {
            fieldServerAddress.setText("http://" + server.getServerUrl());
        } else {
            fieldServerAddress.setText("");
        }
    }
}
