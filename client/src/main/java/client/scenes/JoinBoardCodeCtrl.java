package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class JoinBoardCodeCtrl {
    private MainCtrlTalio mainCtrlTalio;
    @FXML
    private TextField fieldBoardCode;

    @FXML
    private Button buttonJoin;

    @Inject
    public JoinBoardCodeCtrl(MainCtrlTalio mainCtrlTalio) {
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void clickJoinBoard() {
        // Button click

        String code = fieldBoardCode.getText();
        System.out.printf("Code: %s", code);
    }
}
