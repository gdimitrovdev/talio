package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CreateBoardCtrl {
    private final MainCtrlTalio mainCtrlTalio;
    private final ServerUtils server;

    @FXML
    private TextField fieldBoardName;

    @FXML
    private Button buttonCreate;

    @Inject
    public CreateBoardCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
    }

    public void clickCreateBoard() throws IOException {
        String name = fieldBoardName.getText();
        Board board = server.createBoard(new Board(name, "", "", ""));
        mainCtrlTalio.addJoinedBoard(server.getServerUrl(), board.getId());
        mainCtrlTalio.showBoard(board);
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }
}
