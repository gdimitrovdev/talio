package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class BoardSettingsCtrl {
    private final MainCtrlTalio mainCtrlTalio;
    private final ServerUtils server;
    private Board board;
    @FXML
    private TextField fieldBoardName;
    @FXML
    private Button resetColorsBoard;
    @FXML
    private Button resetColorsLists;
    @FXML
    private Button resetColorsCards;

    @FXML
    private Button setDefaultScheme1;

    @FXML
    private Button setDefaultScheme2;

    @FXML
    private Button setDefaultScheme3;

    @FXML
    private TextField scheme1Name;
    @FXML
    private TextField scheme2Name;
    @FXML
    private TextField scheme3Name;

    @FXML
    private AnchorPane boardColorPickers;
    @FXML
    private ColorPicker cpBackgroundBoard;
    @FXML
    private ColorPicker cpFontBoard;
    @FXML
    private ColorPicker cpBackgroundLists;
    @FXML
    private ColorPicker cpFontLists;

    @Inject
    public BoardSettingsCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
    }

    public void initialize(Board board) {
        this.board = board;
        fieldBoardName.setText(board.getName());

        //setting the color wheels to have the right color in them
        String[] boardColorWheels = board.getBoardColor().split("/");
        String bgColorBoard = boardColorWheels[0];
        String fontColorBoard = boardColorWheels[1];
        String[] listsColorWheels = board.getListsColor().split("/");
        String bgColorList = listsColorWheels[0];
        String fontColorList = listsColorWheels[1];
        cpBackgroundBoard.setValue(Color.web(bgColorBoard));
        cpFontBoard.setValue(Color.web(fontColorBoard));
        cpBackgroundLists.setValue(Color.web(bgColorList));
        cpFontLists.setValue(Color.web(fontColorList));

    }

    private void changeBoardName(String newName) {
        board.setName(newName);
        server.updateBoard(board);
    }

    public void save() {
        String boardColor = "#" + cpBackgroundBoard.getValue().toString().substring(2, 8);
        String boardFont = "#" + cpFontBoard.getValue().toString().substring(2, 8);
        String listColor = "#" + cpBackgroundLists.getValue().toString().substring(2, 8);
        String listFont = "#" + cpFontLists.getValue().toString().substring(2, 8);
        String boardColorCombo = boardColor + "/" + boardFont;
        String listColorCombo = listColor + "/" + listFont;
        board.setBoardColor(boardColorCombo);
        board.setListsColor(listColorCombo);
        board = server.updateBoard(board);
        changeBoardName(fieldBoardName.getText());
    }

    public void resetBoardColors(){
        String defaultCombo = "#bababa/#000000";
        //updates after you press 'save'
        board.setBoardColor(defaultCombo);
        cpBackgroundBoard.setValue(Color.web("#bababa"));
        cpFontBoard.setValue(Color.web("#000000"));

    }
    public void resetListColors(){
        String defaultCombo = "#dedede/#000000";
        //updates after you press 'save'
        board.setListsColor(defaultCombo);
        cpBackgroundLists.setValue(Color.web("#dedede"));
        cpFontLists.setValue(Color.web("#000000"));
    }
    public void deleteBoard(){
        mainCtrlTalio.removeJoinedBoard(server.getServerUrl(), board.getId());
        server.deleteBoard(board.getId());
        mainCtrlTalio.showHome();
        //not sure if it is actually deleted from db
        //since for deleteBoard() we will use long polling
    }
}
