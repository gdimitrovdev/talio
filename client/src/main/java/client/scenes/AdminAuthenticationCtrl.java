package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;

public class AdminAuthenticationCtrl {
    private final ServerUtils server;
    private final MainCtrlTalio mainCtrlTalio;
    @FXML
    private PasswordField fieldAdminPwd;

    @Inject
    public AdminAuthenticationCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void initialize() {
        fieldAdminPwd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                clickLogin();
            }
        });
    }

    public void clickLogin() {
        String pwd = fieldAdminPwd.getText();
        if (server.adminAuthenticate(pwd)) {
            fieldAdminPwd.clear();
            mainCtrlTalio.enableAdminMode();
        } else {
            mainCtrlTalio.alert("Authentication was unsuccessful",
                    "You entered wrong credentials");
        }
    }

}
