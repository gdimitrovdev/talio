package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

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
        Board board = new Board(false,name,"","123","red");
        board = serverUtils.createBoard(board);
        mainCtrlTalio.showBoard(board);
        System.out.printf("Name: %s", name);
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }
}
