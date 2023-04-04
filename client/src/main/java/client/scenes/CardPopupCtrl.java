package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.Subtask;
import commons.Tag;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.application.Platform;
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

    private final ServerUtils server;

    private Card card;
    private Set<Tag> cardTags;
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
    private Button moveUpSubtask;

    @FXML
    private Button moveDownSubtask;

    @FXML
    private TextField newTagTextfield;

    @FXML
    private ColorPicker tagColorPicker;

    @FXML
    private Button save;

    @FXML
    private Button close;

    @Inject
    public CardPopupCtrl(MainCtrlTalio mainCtrlTalio, Card card, ServerUtils server) throws IOException {
        this.mainCtrlTalio = mainCtrlTalio;
        this.card = card;
        this.server = server;
        cardTags = new HashSet<>(card.getTags());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CardPopup.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        server.registerForMessages("/topic/subtasks", Subtask.class, subtask -> {
            if (subtask.getCard().getId().equals(card.getId())) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });

        server.registerForMessages("/topic/subtasks", Card.class, cardReceived -> {
            if (cardReceived.getId().equals(card.getId())) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.refreshCardData();
    }

    public void refreshCardData() {
        if (cardTitle.getText().isEmpty()) {
            cardTitle.setText(this.card.getTitle());
        }
        if (cardDescription.getText().isEmpty()) {
            cardDescription.setText(card.getDescription());
        }

        subtaskVBox.getChildren().clear();
        tagHBox.getChildren().clear();

        initializeSubtasks();

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

        initializeTags();

        tagMenu = new MenuButton("Select a tag");
        tagMenu.getItems().clear();
        initializeTagDropdownMenu();
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
        save.setLayoutX(450);
        save.setLayoutY(10);
        save.setOnAction(a -> {
            save();
        });
    }

    public void deleteSubtask(Subtask subtask) {
        System.out.println(subtask.getId());
        server.deleteSubtask(subtask.getId());
    }

    public void moveUp(Subtask subtask) {
        server.moveUp(subtask);
    }

    public void moveDown(Subtask subtask) {
        server.moveDown(subtask);
    }

    public void initializeSubtasks() {
        subtaskVBox.getChildren().clear();
        for (Subtask subtask : card.getSubtasks().stream().sorted(Comparator.comparing(
                Subtask::getPositionInCard
        )).toList()) {
            HBox subtaskElement = new HBox();

            CheckBox checkBox = new CheckBox(subtask.getTitle());
            checkBox.setSelected(subtask.getCompleted());
            checkBox.setOnAction(a -> {
                subtask.setCompleted(!subtask.getCompleted());
                server.updateSubtask(subtask);
                //refreshCardData();
            });
            checkBox.getStyleClass().add("subtask-checkbox");
            subtaskElement.getChildren().add(checkBox);

            deleteSubtask = new Button("x");
            deleteSubtask.getStyleClass().add("remove-subtask-button");
            deleteSubtask.setOnAction(a -> {
                deleteSubtask(subtask);
            });
            subtaskElement.getChildren().add(deleteSubtask);

            moveUpSubtask = new Button("^");
            // moveUpSubtask.getStyleClass().add("remove-subtask-button");
            moveUpSubtask.setOnAction(a -> {
                moveUp(subtask);
            });
            subtaskElement.getChildren().add(moveUpSubtask);

            moveDownSubtask = new Button("v");
            // moveUpSubtask.getStyleClass().add("remove-subtask-button");
            moveDownSubtask.setOnAction(a -> {
                moveDown(subtask);
            });
            subtaskElement.getChildren().add(moveDownSubtask);

            subtaskVBox.getChildren().add(subtaskElement);
        }
    }

    public void initializeTags() {
        for (Tag tag : cardTags) {
            // TODO the colors for the tags
            tagElement = new HBox();
            tagElement.getStyleClass().add("tag");
            // TODO fix this when we figure out the colors
            tagElement.setStyle("-fx-background-color: #" + tag.getColor().substring(2));

            Text tagText = new Text(tag.getTitle());
            tagText.getStyleClass().add("tag-text");

            Button deleteTagButton = new Button("x");
            deleteTagButton.setOnAction(a -> {
                cardTags.remove(tag);
                refreshCardData();
            });
            deleteTagButton.getStyleClass().add("remove-tag-button");

            tagElement.getChildren().add(tagText);
            tagElement.getChildren().add(deleteTagButton);

            tagHBox.getStyleClass().add("tag-HBox");
            tagHBox.getChildren().add(tagElement);
        }
    }

    public void initializeTagDropdownMenu() {
        List<Tag> tagsOfBoard = card.getList().getBoard().getTags();

        for (Tag tag : tagsOfBoard) {
            if (!cardTags.contains(tag)) {
                MenuItem menuItem = new MenuItem(tag.getTitle());
                menuItem.setOnAction(event -> {
                    cardTags.add(tag);
                    refreshCardData();
                });

                tagMenu.getItems().add(menuItem);
            }
        }
    }

    public void addNewSubtask(String entry) {
        if (!entry.isEmpty()) {
            server.createSubtask(new Subtask(entry, card, false));
        }
    }

    public void refresh() {
        card = server.getCard(card.getId());
        initializeSubtasks();
        initializeTags();
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
            Tag tagToAdd = null;

            for (Tag tag : card.getList().getBoard().getTags()) {
                if (newTagTextfield.getText().equals(tag.getTitle())) {
                    tagExists = true;
                    tagToAdd = tag;
                }
            }

            if (!tagExists) {
                tagToAdd = new Tag(entry, tagColorPicker.getValue().toString(),
                        card.getList().getBoard());
                assignEmptyBoardWithId(tagToAdd);
                tagToAdd = server.createTag(tagToAdd);
            }

            cardTags.add(tagToAdd);
            newTagTextfield.clear();
            refreshCardData();
        }
    }

    public void save() {
        // TODO get the results from the checkboxes
        // TODO take care of the color as well
        card.setTitle(cardTitle.getText());
        card.setDescription(cardDescription.getText());

        server.updateCard(card);

        Set<Tag> originalTags = new HashSet<Tag>(server.getCard(card.getId()).getTags());

        //Tags to add is Set difference cardTags - originalTags
        Set<Tag> tagsToAdd = new HashSet<Tag>(cardTags);
        tagsToAdd.removeAll(originalTags);

        //Tags to remove is Set difference originalTags - cardTags
        Set<Tag> tagsToRemove = new HashSet<Tag>(originalTags);
        tagsToRemove.removeAll(cardTags);

        for (Tag tag : tagsToAdd) {
            server.addTagToCard(card.getId(), tag.getId());
        }
        for (Tag tag : tagsToRemove) {
            server.removeTagFromCard(card.getId(), tag.getId());
        }
        //card.
        //mainCtrlTalio.showBoard(card.getList().getBoard());
    }

    private void assignEmptyBoardWithId(Tag tag) {
        Board empty = new Board();
        empty.setId(tag.getBoard().getId());
        tag.setBoard(empty);
    }
}
