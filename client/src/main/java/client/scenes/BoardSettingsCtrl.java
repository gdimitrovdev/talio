package client.scenes;

import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BoardSettingsCtrl {
    private final MainCtrlTalio mainCtrlTalio;
    private Board board;
    @FXML
    private TextField fieldBoardName;

    @Inject
    public BoardSettingsCtrl(MainCtrlTalio mainCtrlTalio) {
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void initialize(Board board) {
        this.board = board;
        fieldBoardName.setText(board.getName());

        //  TODO: this can be another event
        fieldBoardName.textProperty().addListener(((observable, oldValue, newValue) -> {
            changeBoardName(newValue.toString());
        }));
    }

    private void changeBoardName(String newName) {
        board.setName(newName);
    }
}