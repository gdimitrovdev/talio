package client.scenes;

import client.components.TitleField;
import client.utils.ServerUtils;
import client.utils.Utils;
import com.google.inject.Inject;
import commons.Board;
import java.util.Objects;
import java.util.function.Predicate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

public class BoardSettingsCtrl extends AnchorPane {
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
    private AnchorPane anchorPane;
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

    private Stage stage;

    private Predicate<String> colorSchemeValidator;

    @Inject
    public BoardSettingsCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server, Stage stage,
            Board board) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
        this.stage = stage;
        this.board = board;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("BoardSettings.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        colorSchemeValidator = title -> (!title.equals("")
                && this.presetsBox.getChildren().stream().noneMatch(scheme -> {
                    if (!scheme.isVisible()) {
                        return false;
                    }
                    try {
                        return ((TitleField) ((HBox) scheme).getChildren().get(0)).getTitle().equals(title);
                    } catch (Exception ignored) {
                        return false;
                    }
                }
        ));

        init(board);
    }

    public void init(Board board) {
        this.setHasUnsavedChanges(false);
        fieldBoardName.setText(board.getName());

        String bgColorBoard = Utils.getBackgroundColor(board.getBoardColor());
        String fontColorBoard = Utils.getForegroundColor(board.getBoardColor());

        String bgColorList = Utils.getBackgroundColor(board.getListsColor());
        String fontColorList = Utils.getForegroundColor(board.getListsColor());
        cpBackgroundBoard.setValue(Color.web(bgColorBoard));
        cpFontBoard.setValue(Color.web(fontColorBoard));
        cpBackgroundLists.setValue(Color.web(bgColorList));
        cpFontLists.setValue(Color.web(fontColorList));

        int id = 0;
        for (String preset : board.getCardColorPresets()) {
            var row = generateColorSchemeRow(preset, id);
            // If the preset has been deleted
            if (preset.equals("")) {
                row.setDisable(true);
                row.setVisible(false);
                row.setManaged(false);
            }
            if (id == board.getDefaultPresetNum()) {
                ((CheckBox) row.getChildren().get(3)).setSelected(true);
                row.getChildren().get(3).setDisable(true);
                row.getChildren().get(4).setDisable(true);
            }
            presetsBox.getChildren().add(row);
            id++;
        }

    }

    private HBox generateColorSchemeRow(String preset, int id) {
        HBox row = new HBox(10);
        row.setMinHeight(40);
        row.setMinWidth(260);

        Button deleteBtn = new Button();
        var trashIcon = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/client/images/bin.png"))));
        trashIcon.setFitHeight(18);
        trashIcon.setFitWidth(18);
        deleteBtn.setGraphic(trashIcon);
        deleteBtn.setOnAction((e) -> {
            Alert confirmationDialogue =
                    new Alert(Alert.AlertType.CONFIRMATION, "Delete color preset ?", ButtonType.YES,
                            ButtonType.NO);
            confirmationDialogue.setContentText("Delete color preset ?");
            confirmationDialogue.showAndWait();
            if (confirmationDialogue.getResult() == ButtonType.YES) {
                row.setManaged(false);
                row.setVisible(false);
                row.setDisable(true);
                board.getCardColorPresets().set(id, "");
            }
        });

        TitleField name = new TitleField();

        ColorPicker colorPickerBackground = new ColorPicker();
        ColorPicker colorPickerForeground = new ColorPicker();
        CheckBox defaultCheckbox = new CheckBox();

        defaultCheckbox.setOnAction((e) -> {
            this.setHasUnsavedChanges(true);
            deleteBtn.setDisable(true);
            defaultCheckbox.setDisable(true);
            for (Node hbox : presetsBox.getChildren()) {
                if (!hbox.equals(row)) {
                    ((HBox) hbox).getChildren().get(3).setDisable(false);
                    ((CheckBox) ((HBox) hbox).getChildren().get(3)).setSelected(false);
                    ((HBox) hbox).getChildren().get(4).setDisable(false);
                }
            }
            board.setDefaultPresetNum(id);
        });

        name.setMinWidth(250);
        name.setMaxWidth(250);
        colorPickerBackground.setMaxSize(38, 26);
        colorPickerForeground.setMaxSize(38, 26);

        if (preset.equals("NEW_SCHEME")) {
            colorPickerBackground.setValue(Color.WHITE);
            colorPickerForeground.setValue(Color.BLACK);
            name.init(s -> {
                    }, s -> {
                        board.getCardColorPresets().add("");
                    }, () -> {
                        presetsBox.getChildren().remove(row);
                    }, colorSchemeValidator
            );
        } else if (!preset.equals("")) {
            String presetBg = Utils.getBackgroundColor(preset);
            String presetFont = Utils.getForegroundColor(preset);
            colorPickerBackground.setValue(Color.web(presetBg));
            colorPickerForeground.setValue(Color.web(presetFont));
            try {
                String colorSchemeName = Utils.getColorSchemeName(preset);
                name.init(colorSchemeName, s -> {
                }, colorSchemeValidator);
            } catch (Exception ignored) {
                name.init("Untitled color scheme", s -> {
                }, colorSchemeValidator);
            }
        }

        row.getChildren()
                .addAll(name, colorPickerBackground, colorPickerForeground, defaultCheckbox,
                        deleteBtn);

        HBox.setHgrow(deleteBtn, Priority.NEVER);
        HBox.setHgrow(colorPickerBackground, Priority.NEVER);
        HBox.setHgrow(colorPickerForeground, Priority.NEVER);
        HBox.setHgrow(defaultCheckbox, Priority.NEVER);
        HBox.setHgrow(name, Priority.ALWAYS);

        row.setAlignment(Pos.CENTER);
        return row;
    }

    public void save() {

        hasUnsavedChanges = false;

        String boardColor = "#" + cpBackgroundBoard.getValue().toString().substring(2, 8);
        String boardFont = "#" + cpFontBoard.getValue().toString().substring(2, 8);
        String listColor = "#" + cpBackgroundLists.getValue().toString().substring(2, 8);
        String listFont = "#" + cpFontLists.getValue().toString().substring(2, 8);

        board.setBoardColor(boardColor + "/" + boardFont);
        board.setListsColor(listColor + "/" + listFont);
        board.setName(fieldBoardName.getText());

        int id = 0;
        for (var row : presetsBox.getChildren()) {
            if (!row.isDisable()) {
                String background = "#" + ((ColorPicker) ((HBox) row).getChildren().get(1)).
                        getValue().toString().substring(2, 8);
                String foreground = "#" + ((ColorPicker) ((HBox) row).getChildren().get(2)).
                        getValue().toString().substring(2, 8);
                String name = ((TitleField) ((HBox) row).getChildren().get(0)).getTitle();
                board.getCardColorPresets().set(id, background + "/" + foreground + "/" + name);
            }
            ++id;
        }

        board = server.updateBoard(board);
        stage.close();
    }

    public void resetBoardColors() {
        this.setHasUnsavedChanges(true);
        String defaultCombo = "#bababa/#000000";
        //updates after you press 'save'
        //board.setBoardColor(defaultCombo);
        cpBackgroundBoard.setValue(Color.web("#bababa"));
        cpFontBoard.setValue(Color.web("#000000"));

    }

    public void resetListColors() {
        this.setHasUnsavedChanges(true);
        String defaultCombo = "#dedede/#000000";
        //updates after you press 'save'
        //board.setListsColor(defaultCombo);
        cpBackgroundLists.setValue(Color.web("#dedede"));
        cpFontLists.setValue(Color.web("#000000"));
    }

    public void deleteBoard() {
        Alert confirmationDialogue =
                new Alert(Alert.AlertType.CONFIRMATION, "Delete this board permanently ?",
                        ButtonType.YES, ButtonType.NO);
        confirmationDialogue.showAndWait();
        if (confirmationDialogue.getResult() == ButtonType.YES) {
            server.deleteBoard(board.getId());

            Window window = anchorPane.getScene().getWindow();

            if (window instanceof Stage) {
                ((Stage) window).close();
            }
            //not sure if it is actually deleted from db
            //since for deleteBoard() we will use long polling

        }
    }

    public void addColorPreset() {
        this.setHasUnsavedChanges(true);
        presetsBox.getChildren().add(generateColorSchemeRow("NEW_SCHEME",
                board.getCardColorPresets().size()));
    }

    @FXML
    public void madeChange() {
        this.setHasUnsavedChanges(true);
    }

}

