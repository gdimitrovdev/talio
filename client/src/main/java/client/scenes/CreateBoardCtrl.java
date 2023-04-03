package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

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

    @FXML
    public void initialize() {
        fieldBoardName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    clickCreateBoard();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void clickCreateBoard() throws IOException {
        String name = fieldBoardName.getText();
        if (name.equals("")) {
            Alert box = new Alert(Alert.AlertType.ERROR);
            box.setTitle("Invalid name");
            box.setContentText("The name of the board cannot be empty");
            box.showAndWait();
        } else {
            Board board = server.createBoard(new Board(name, "", "", ""));
            mainCtrlTalio.addJoinedBoard(server.getServerUrl(), board.getId());
            mainCtrlTalio.showBoard(board);
        }
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }

    public void refreshFieldBoardName() {
        fieldBoardName.setText("");
    }
}
