package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CreateBoardCtrl {
    private MainCtrlTalio mainCtrlTalio;
    @FXML
    private TextField fieldBoardName;

    @FXML
    private Button buttonCreate;

    @Inject
    public CreateBoardCtrl(MainCtrlTalio mainCtrlTalio) {
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void clickCreateBoard() {
        // Button click

        String name = fieldBoardName.getText();
        System.out.printf("Name: %s", name);
    }
}
