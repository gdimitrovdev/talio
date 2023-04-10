package client.scenes;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BoardSettingsCtrl {
    private final MainCtrlTalio mainCtrlTalio;
    private final ServerUtils server;
    private Board board;
    private boolean hasUnsavedChanges;

    public boolean isHasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    public void setHasUnsavedChanges(boolean hasUnsavedChanges) {
        this.hasUnsavedChanges = hasUnsavedChanges;
    }

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

    @FXML
    private VBox presetsBox;

    @Inject
    public BoardSettingsCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
    }

    public void initialize(Board board) {
        this.setHasUnsavedChanges(false);
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
        int counter = 0;
        for (String preset : board.getCardColorPresets()) {
            if (!preset.equals("")) {
                HBox hboxPreset = generateHboxPreset(preset, counter);
                if (counter == board.getDefaultPresetNum()) {
                    ((CheckBox) hboxPreset.getChildren().get(3)).setSelected(true);
                }
                presetsBox.getChildren().add(hboxPreset);
            }
            counter++;

        }

    }

    private HBox generateHboxPreset(String preset, int counter) {
        //get the colors for this preset
        String[] colors = preset.split("/");
        String presetBg = colors[0];
        String presetFont = colors[1];

        HBox hboxPreset = new HBox(5);
        hboxPreset.setMinHeight(40);
        hboxPreset.setMinWidth(260);

        //initialize the elements for the hbox
        Button deleteBtn = new Button("x");
        deleteBtn.setOnAction((e) -> {
            Alert confirmationDialogue = new Alert(Alert.AlertType.CONFIRMATION, "Delete color preset ?", ButtonType.YES, ButtonType.NO);
            confirmationDialogue.setContentText("Delete color preset ?");
            confirmationDialogue.showAndWait();
            if (confirmationDialogue.getResult() == ButtonType.YES) {
                presetsBox.getChildren().remove(hboxPreset);
                board.getCardColorPresets().set(counter, "");
            }

        });
        ColorPicker colorPickerBG = new ColorPicker();
        ColorPicker colorPickerF = new ColorPicker();
        CheckBox setAsDefault = new CheckBox();
        setAsDefault.setOnAction((e) -> {
            this.setHasUnsavedChanges(true);
            //disable the delete button
            deleteBtn.setDisable(true);
            //for all other hboxes in the presetsBox disselect their checkbox
            //update the board's preset id
            //update the cards that have a preset id = the board's preset id
            //ignore the preset id of the cards which don't match to that of the board
            for (Node hbox : presetsBox.getChildren()) {
                if (!hbox.equals(hboxPreset)) {
                    ((CheckBox) ((HBox) hbox).getChildren().get(3)).setSelected(false);
                    ((Button) ((HBox) hbox).getChildren().get(4)).setDisable(false);
                }

            }

            board.setDefaultPresetNum(counter);
            server.updateBoard(board);
        });
        Label name = new Label("Your color scheme:");

        //style the elements for the hbox
        name.setMinSize(105, 26);
        colorPickerBG.setMaxSize(38, 26); // 38 x 26
        colorPickerF.setMaxSize(38, 26); // 38 x 26
        name.setPrefSize(USE_COMPUTED_SIZE, 26);



        hboxPreset.getChildren().addAll(name, colorPickerBG, colorPickerF, setAsDefault, deleteBtn);

        HBox.setHgrow(deleteBtn, Priority.ALWAYS);
        HBox.setHgrow(colorPickerBG, Priority.ALWAYS);
        HBox.setHgrow(colorPickerF, Priority.ALWAYS);
        HBox.setHgrow(setAsDefault, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);

        hboxPreset.setAlignment(Pos.CENTER);
        return hboxPreset;

    }

    private void changeBoardName(String newName) {
        board.setName(newName);
        server.updateBoard(board);
    }

    public void save() {

        hasUnsavedChanges = false;

        String boardColor = "#" + cpBackgroundBoard.getValue().toString().substring(2, 8);
        String boardFont = "#" + cpFontBoard.getValue().toString().substring(2, 8);
        String listColor = "#" + cpBackgroundLists.getValue().toString().substring(2, 8);
        String listFont = "#" + cpFontLists.getValue().toString().substring(2, 8);
        String boardColorCombo = boardColor + "/" + boardFont;
        String listColorCombo = listColor + "/" + listFont;
        board.setBoardColor(boardColorCombo);
        board.setListsColor(listColorCombo);
        changeBoardName(fieldBoardName.getText());

        List<String> presets = new ArrayList<>();

        int counter = 0;
        for (int i = 0; i < board.getCardColorPresets().size(); i++) {
            HBox presetHbox = (HBox) presetsBox.getChildren().get(i);
            String bgColor = "#" + ((ColorPicker) ((HBox) presetHbox).getChildren().get(1)).
                    getValue().toString().substring(2, 8);
            String fontColor =
                    "#" + ((ColorPicker) ((HBox) presetHbox).getChildren().get(2)).
                            getValue().toString().substring(2, 8);
            String colorCombo = bgColor + "/" + fontColor;

            counter++;
            if (((CheckBox) presetHbox.getChildren().get(3)).isSelected()) {
                board.setDefaultPresetNum(counter);
            }
        }
        if (board.getCardColorPresets().size() < (presetsBox.getChildren().size())) {
            for (int i = 0; i < (presetsBox.getChildren().size()
                    - board.getCardColorPresets().size()); i++) {
                //get new entries
                HBox presetHbox = (HBox) presetsBox.getChildren().get(counter++);
                String bgColor = "#" + ((ColorPicker) ((HBox) presetHbox).getChildren().get(1)).
                        getValue().toString().substring(2, 8);
                String fontColor =
                        "#" + ((ColorPicker) ((HBox) presetHbox).getChildren().get(2)).
                                getValue().toString().substring(2, 8);
                String colorCombo = bgColor + "/" + fontColor;

                if (((CheckBox) presetHbox.getChildren().get(3)).isSelected()) {
                    board.setDefaultPresetNum(counter);
                }
            }
        }


        board.setCardColorPresets(presets);

        board = server.updateBoard(board);
    }

    public void resetBoardColors() {
        this.setHasUnsavedChanges(true);
        String defaultCombo = "#bababa/#000000";
        //updates after you press 'save'
        board.setBoardColor(defaultCombo);
        cpBackgroundBoard.setValue(Color.web("#bababa"));
        cpFontBoard.setValue(Color.web("#000000"));

    }

    public void resetListColors() {
        this.setHasUnsavedChanges(true);
        String defaultCombo = "#dedede/#000000";
        //updates after you press 'save'
        board.setListsColor(defaultCombo);
        cpBackgroundLists.setValue(Color.web("#dedede"));
        cpFontLists.setValue(Color.web("#000000"));
    }

    public void deleteBoard() {
        Alert confirmationDialogue = new Alert(Alert.AlertType.CONFIRMATION, "Delete this board permanently ?", ButtonType.YES);
        confirmationDialogue.showAndWait();
        if (confirmationDialogue.getResult() == ButtonType.YES) {
            mainCtrlTalio.removeJoinedBoard(server.getServerUrl(), board.getId());
            server.deleteBoard(board.getId());
            mainCtrlTalio.showHome();
            //not sure if it is actually deleted from db
            //since for deleteBoard() we will use long polling

        }
    }

    public void addColorPreset() {
        this.setHasUnsavedChanges(true);
        HBox newHbox = generateHboxPreset("#ffffff/#000000",
                board.getCardColorPresets().size() + 1);
        presetsBox.getChildren().add(newHbox);

    }

    @FXML
    public void madeChange() {
        this.setHasUnsavedChanges(true);
    }

}

