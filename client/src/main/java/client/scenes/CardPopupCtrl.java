package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.Subtask;
import commons.Tag;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CardPopupCtrl extends AnchorPane implements Initializable {

    private final MainCtrlTalio mainCtrlTalio;
    private CardComponentCtrl cardComponentCtrl;

    private ServerUtils server;

    private Card card;

    @FXML
    private AnchorPane anchorPane = new AnchorPane();

    @FXML
    private TextField cardTitle = new TextField();

    @FXML
    private TextField cardDescription = new TextField();

    @FXML
    private VBox subtaskVBox = new VBox();

    @FXML
    private HBox subtaskHBox;

    @FXML
    private HBox tagHBox = new HBox();

    @FXML
    private HBox tagElement;

    @FXML
    private MenuButton tagMenu;

    @FXML
    private TextField newSubtaskEntry;

    @FXML
    private Button addNewSubtask;

    @FXML
    private Button deleteSubtask;

    @FXML
    private TextField newTagTextfield;

    @FXML
    private ColorPicker tagColorPicker;

    @FXML
    private Button save;

    @FXML
    private Button close;

    @Inject
    public CardPopupCtrl(MainCtrlTalio mainCtrlTalio, Card card) throws IOException {
        this.mainCtrlTalio = mainCtrlTalio;
        this.card = card;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CardPopup.fxml"));
        loader.setRoot(this);
        loader.setController(this);
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.setCardData(card);
    }

    public void setCardData(Card cardData) {
        cardTitle.setText(card.getTitle());
        cardDescription.setText(cardData.getDescription());

        subtaskVBox.getChildren().clear();
        tagHBox.getChildren().clear();

        initializeSubtasks(cardData);

        subtaskHBox = new HBox();

        newSubtaskEntry = new TextField();
        newSubtaskEntry.setPromptText("Add new subtask");
        newSubtaskEntry.setOnAction(a -> {
            addNewSubtask(newSubtaskEntry.getText());
        });

        addNewSubtask = new Button("Add new subtask");
        addNewSubtask.setOnAction(a -> {
            addNewSubtask(newSubtaskEntry.getText());
        });

        subtaskHBox.getChildren().add(newSubtaskEntry);
        subtaskHBox.getChildren().add(addNewSubtask);
        subtaskHBox.setLayoutX(10);
        subtaskHBox.setLayoutY(290);

        anchorPane.getChildren().add(subtaskHBox);

        initializeTags(cardData);

        tagMenu = new MenuButton("Select a tag");
        tagMenu.getItems().clear();
        initializeTagDropdownMenu(cardData);
        tagMenu.setLayoutX(10);
        tagMenu.setLayoutY(370);
        anchorPane.getChildren().add(tagMenu);

        tagColorPicker = new ColorPicker(Color.ORANGE);
        tagColorPicker.setValue(Color.ORANGE);
        tagColorPicker.setLayoutX(120);
        tagColorPicker.setLayoutY(370);
        anchorPane.getChildren().add(tagColorPicker);

        newTagTextfield = new TextField();
        newTagTextfield.setPromptText("New Tag");
        newTagTextfield.setLayoutX(260);
        newTagTextfield.setLayoutY(370);
        anchorPane.getChildren().add(newTagTextfield);

        newTagTextfield.setOnKeyTyped(a -> {
            checkTagDuplicate(newTagTextfield.getText());
        });

        newTagTextfield.setOnKeyPressed(a -> {
            if (a.getCode() == KeyCode.ENTER) {
                addAndMakeNewTag(newTagTextfield.getText());
            }
        });

        save = new Button("Save");
        anchorPane.getChildren().add(save);
        save.setLayoutX(360);
        save.setLayoutY(10);
        save.setOnAction(a -> {
            save();
            // TODO display the board overview again
        });

        close = new Button("Close");
        close.setOnAction(a -> {
            close();
            // TODO display the board overview again
        });
    }

    public void deleteSubtask(Subtask subtask) {
        card.getSubtasks().remove(subtask);
        this.setCardData(card);
    }

    public void initializeSubtasks(Card cardData) {
        for (Subtask subtask : cardData.getSubtasks()) {
            HBox subtaskElement = new HBox();

            CheckBox checkBox = new CheckBox(subtask.getTitle());
            checkBox.getStyleClass().add("subtask-checkbox");
            subtaskElement.getChildren().add(checkBox);

            deleteSubtask = new Button("x");
            deleteSubtask.getStyleClass().add("remove-subtask-button");
            deleteSubtask.setOnAction(a -> {
                deleteSubtask(subtask);
            });
            subtaskElement.getChildren().add(deleteSubtask);

            subtaskVBox.getChildren().add(subtaskElement);
        }
    }

    public void initializeTags(Card cardData) {
        for (Tag tag : cardData.getTags()) {
            // TODO the colors for the tags
            tagElement = new HBox();
            tagElement.getStyleClass().add("tag");
            tagElement.setStyle("-fx-background-color: #" + tag.getColor().substring(2));

            Text tagText = new Text(tag.getTitle());
            tagText.getStyleClass().add("tag-text");

            Button deleteTagButton = new Button("x");
            deleteTagButton.setOnAction(a -> {
                card.getTags().remove(tag);
                setCardData(card);
            });
            deleteTagButton.getStyleClass().add("remove-tag-button");

            tagElement.getChildren().add(tagText);
            tagElement.getChildren().add(deleteTagButton);

            tagHBox.getStyleClass().add("tag-HBox");
            tagHBox.getChildren().add(tagElement);
        }
    }

    public void initializeTagDropdownMenu(Card cardData) {
        List<Tag> tagsOfBoard = cardData.getList().getBoard().getTags();

        for (Tag tag : tagsOfBoard) {
            if (!cardData.getTags().contains(tag)) {
                MenuItem menuItem = new MenuItem(tag.getTitle());
                menuItem.setOnAction(event -> {
                    card.getTags().add(tag);
                    setCardData(card);
                });

                tagMenu.getItems().add(menuItem);
            }
        }
    }

    public void addNewSubtask(String entry) {
        if (!entry.isEmpty()) {
            card.getSubtasks().add(new Subtask(entry, card));
            this.setCardData(card);
        }
    }

    public void checkTagDuplicate(String entry) {
        boolean tagExists = false;

        for (Tag tag : card.getList().getBoard().getTags()) {
            if (newTagTextfield.getText().equals(tag.getTitle())) {
                tagExists = true;
            }
        }

        if (tagExists) {
            newTagTextfield.getStyleClass().remove("valid-textField-input");
            newTagTextfield.getStyleClass().add("invalid-textField-input");
        } else {
            newTagTextfield.getStyleClass().remove("invalid-textField-input");
            newTagTextfield.getStyleClass().add("valid-textField-input");
        }
    }

    public void addAndMakeNewTag(String entry) {
        if (!entry.isEmpty()) {
            boolean tagExists = false;
            for (Tag tag : card.getList().getBoard().getTags()) {
                if (newTagTextfield.getText().equals(tag.getTitle())) {
                    tagExists = true;
                }
            }

            if (!tagExists) {
                Tag tag = new Tag(entry, tagColorPicker.getValue().toString(), null);

                card.getList().getBoard().getTags().add(tag);
                card.getTags().add(tag);
                newTagTextfield.clear();

                setCardData(card);
            }
        }
    }

    public void close() {
        // this would reset to the state of the card regardless of any changes that have been made
        // could remove this and just use the window "X" button
        card.setTitle(cardTitle.getText());
        card.setTitle(cardDescription.getText());
    }

    public void save() {
        // TODO get the results from the checkboxes
        card.setTitle(cardTitle.getText());
        card.setDescription(cardDescription.getText());
    }

    public Card getCard() {
        return card;
    }

}
