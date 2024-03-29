package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public void initialize() {

    }

    public void clickCreateBoard() throws IOException {
        String name = fieldBoardName.getText();
        if (name.equals("")) {
            Alert box = new Alert(Alert.AlertType.ERROR);
            box.setTitle("Invalid name");
            box.setContentText("The name of the board cannot be empty");
            box.showAndWait();
        } else {
            List<String> defaultPresets = new ArrayList<>();
            defaultPresets.add("#ffffff/#000000/Basic");
            defaultPresets.add("#ff0008/#000000/High Contrast");
            defaultPresets.add("#abffc3/#004714/Mint");
            Board board = server.createBoard(
                    new Board(name, "", "", "#bababa/#000000", "#dedede/#000000", defaultPresets,
                            1));
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
