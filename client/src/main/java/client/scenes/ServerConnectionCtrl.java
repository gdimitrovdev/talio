package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ServerConnectionCtrl {
    private final MainCtrlTalio mainCtrlTalio;
    @FXML
    private TextField fieldServerAddress;

    @FXML
    private Button buttonConnect;

    @Inject
    public ServerConnectionCtrl(MainCtrlTalio mainCtrlTalio) {
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void clickConnectServer() {
        // Button click

        String addr = fieldServerAddress.getText();
        System.out.printf("Address: %s", addr);
    }
}
