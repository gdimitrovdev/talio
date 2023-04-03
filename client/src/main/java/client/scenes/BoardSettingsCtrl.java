package client.scenes;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    @FXML
    private VBox presetsBox;

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
        int counter = 0;
        for(String preset : board.getCardColorPresets()){
            HBox hboxPreset = generateHboxPreset(preset);
            if(counter++== board.getDefaultPresetNum()){
                ((CheckBox)hboxPreset.getChildren().get(3)).setSelected(true);
            }
            presetsBox.getChildren().add(hboxPreset);
        }
    }
    private HBox generateHboxPreset(String preset){
        //get the colors for this preset
        String[] colors = preset.split("/");
        String presetBg = colors[0];
        String presetFont = colors[1];

        HBox hboxPreset = new HBox(10);
        hboxPreset.setPrefSize(260, 40);

        //initialize the elements for the hbox
        Button deleteBtn = new Button();
        ColorPicker colorPickerBG = new ColorPicker();
        ColorPicker colorPickerF = new ColorPicker();
        CheckBox setAsDefault = new CheckBox();
        Label name = new Label("Your color scheme:");

        //style the elements for the hbox
        deleteBtn.setPrefSize(26, 36); // 26 x 36
        colorPickerBG.setPrefSize(38, 26); // 38 x 26
        colorPickerF.setPrefSize(38, 26); // 38 x 26
        setAsDefault.setPrefSize(115, 26); // 115 x 26
        name.setPrefSize(USE_COMPUTED_SIZE, 26);
        hboxPreset.setAlignment(Pos.CENTER);

        hboxPreset.getChildren().addAll(name, colorPickerBG ,colorPickerF, setAsDefault, deleteBtn);
        return hboxPreset;

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
        changeBoardName(fieldBoardName.getText());

        List<String> presets = new ArrayList<>();

                int counter = 0;
        //get the string values of the colorpickers and connect them in one string and put it in the array
        //check the checkbox - if its checked: update te board.defaultNum
        //the board's list get overwritten - the colors are takan again and set and strings and inserted in the list
        //the board's attribute for DefaultPresetNum is also updated.
        // and if there are leftover hboxes then they are added in the list
                //iterating over old entries and updating them
                for (int i = 0; i < board.getCardColorPresets().size(); i++) {
                    HBox presetHbox = (HBox) presetsBox.getChildren().get(i);
                    String bgColor = "#" + ((ColorPicker) ((HBox) presetHbox).getChildren().get(1)).
                            getValue().toString().substring(2, 8);
                    String fontColor =
                            "#" + ((ColorPicker) ((HBox) presetHbox).getChildren().get(2)).
                                    getValue().toString().substring(2, 8);
                    String colorCombo = bgColor+"/"+fontColor;

                    counter++;
                    if(((CheckBox) presetHbox.getChildren().get(3)).isSelected()){
                        board.setDefaultPresetNum(counter);
                    }
                }
                if (board.getCardColorPresets().size() < (presetsBox.getChildren().size())) {
                    for (int i = 0; i < (presetsBox.getChildren().size() -
                            board.getCardColorPresets().size()); i++) {
                        //get new entries
                        HBox presetHbox = (HBox) presetsBox.getChildren().get(counter++);
                        String bgColor = "#" + ((ColorPicker) ((HBox) presetHbox).getChildren().get(1)).
                                getValue().toString().substring(2, 8);
                        String fontColor =
                                "#" + ((ColorPicker) ((HBox) presetHbox).getChildren().get(2)).
                                        getValue().toString().substring(2, 8);
                        String colorCombo = bgColor+"/"+fontColor;

                        if(((CheckBox) presetHbox.getChildren().get(3)).isSelected()){
                            board.setDefaultPresetNum(counter);
                        }
                    }
                }


        board.setCardColorPresets(presets);

        board = server.updateBoard(board);
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
    public void addColorPreset(){
        HBox newHbox = generateHboxPreset("#ffffff/#000000");
        presetsBox.getChildren().add(newHbox);

    }
}

