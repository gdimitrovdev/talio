package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;

public class AdminAuthenticationCtrl {
    private final MainCtrlTalio mainCtrlTalio;
    @FXML
    private PasswordField fieldAdminPwd;

    @Inject
    public AdminAuthenticationCtrl(MainCtrlTalio mainCtrlTalio) {
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
        // This is not secure and would be changed in a professional app
        if (fieldAdminPwd.getText().contentEquals("oopp23")) {
            fieldAdminPwd.clear();
            mainCtrlTalio.enableAdminMode();
        } else {
            mainCtrlTalio.alert("Authentication was unsuccessful",
                    "You entered wrong credentials");
        }
    }

}
