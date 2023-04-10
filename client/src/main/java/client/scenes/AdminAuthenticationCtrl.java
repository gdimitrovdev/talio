package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class AdminAuthenticationCtrl {
    private final MainCtrlTalio mainCtrlTalio;
    @FXML
    private PasswordField fieldAdminPwd;

    @Inject
    public AdminAuthenticationCtrl(MainCtrlTalio mainCtrlTalio) {
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void clickLogin() {
        // This is not secure and would be changed for a professional app
        if (fieldAdminPwd.getText().contentEquals("oop23")) {
            mainCtrlTalio.enableAdminMode();
        } else {
            mainCtrlTalio.alert("Authentication was unsuccessful",
                    "You entered wrong credentials");
        }
    }

}
