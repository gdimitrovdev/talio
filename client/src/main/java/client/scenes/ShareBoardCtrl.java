package client.scenes;

import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ShareBoardCtrl {
    private final MainCtrlTalio mainCtrlTalio;
    private final Clipboard clipboard;
    @FXML
    private TextField fieldReadOnlyCode;
    @FXML
    private Button buttonCopyReadOnlyCode;
    @FXML
    private TextField fieldCode;
    @FXML
    private Button buttonCopyCode;
    @FXML
    private Button buttonBack;

    @Inject
    public ShareBoardCtrl(MainCtrlTalio mainCtrlTalio) {
        this.mainCtrlTalio = mainCtrlTalio;
        clipboard = Clipboard.getSystemClipboard();
    }

    public void copyReadOnlyCode() {
        copyToClipboard(fieldReadOnlyCode.getText());
    }

    public void copyCode() {
        copyToClipboard(fieldCode.getText());
    }

    public void initialize(Board board) {
        fieldReadOnlyCode.setText(board.getReadOnlyCode());
        fieldCode.setText(board.getCode());
    }

    private void copyToClipboard(String text) {

        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
}
