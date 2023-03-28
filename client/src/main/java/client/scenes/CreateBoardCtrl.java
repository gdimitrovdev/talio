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

    private final ServerUtils serverUtils;
    @FXML
    private TextField fieldBoardName;

    @FXML
    private Button buttonCreate;

    @Inject
    public CreateBoardCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils serverUtils) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.serverUtils = serverUtils;
    }

    public void clickCreateBoard() throws IOException {
        // create new board
        String name = fieldBoardName.getText();
        Board board = new Board(name, "", "123", "red");
        board = serverUtils.createBoard(board);
        mainCtrlTalio.showBoard(board);
        System.out.printf("Name: %s", name);
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }
}
