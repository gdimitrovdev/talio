package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

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

    @FXML
    public void initialize() {
        fieldBoardCode.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                clickJoinBoard();
            }
        });
    }

    public void clickJoinBoard() {
        String code = fieldBoardCode.getText();
        Board newBoard = server.joinBoard(code);
        if (newBoard.getName().equals("NotFoundInSystem")) {
            Alert box = new Alert(Alert.AlertType.ERROR);
            box.setTitle("Invalid code");
            box.setContentText("There is no board that can be joined using that code!");
            box.showAndWait();
        } else {
            mainCtrlTalio.addJoinedBoard(server.getServerUrl(), newBoard.getId());
            mainCtrlTalio.showBoard(newBoard);
        }
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }

    public void refreshFieldBoardCode() {
        fieldBoardCode.setText("");
    }
}
