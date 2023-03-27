package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class JoinBoardCodeCtrl {
    private final MainCtrlTalio mainCtrlTalio;

    private final ServerUtils server;
    @FXML
    private TextField fieldBoardCode;

    @FXML
    private Button buttonJoin;

    @Inject
    public JoinBoardCodeCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
    }

    public void clickJoinBoard() {
        // Button click


        String code = fieldBoardCode.getText();
        Board newBoard = server.retrieveBoard(code);
        System.out.printf("Code: %s", code);
    }
}
