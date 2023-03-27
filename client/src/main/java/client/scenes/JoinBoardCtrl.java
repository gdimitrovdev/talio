package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class JoinBoardCtrl {
    private final ServerUtils server;
    private final MainCtrlTalio mainCtrlTalio;
    @FXML
    private TextField fieldBoardCode;

    @FXML
    private Button buttonJoin;

    @Inject
    public JoinBoardCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void clickJoinBoard() {
        // Button click

        String code = fieldBoardCode.getText();
        Board newBoard = server.retrieveBoard(code);
        System.out.printf("Code: %s", code);
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }
}
