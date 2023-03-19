package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class CardPopupCtrl extends AnchorPane implements Initializable {
    private final MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;

    private Card card;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField cardTitle = new TextField();
    @FXML
    private TextField cardDescription = new TextField();
    @FXML
    private VBox subtaskVBox = new VBox();
    @FXML
    private HBox subtaskHBox = new HBox();
    @FXML
    private FlowPane tagHBox = new FlowPane();
    @FXML
    private MenuButton tagMenu = new MenuButton();
    @FXML
    private TextField newSubtaskEntry;
    @FXML
    private Button addNewSubtask = new Button("Add subtask");
    @FXML
    private Button deleteSubtask;
    @FXML
    private final TextField newTagTextfield = new TextField();
    @FXML
    private final ColorPicker tagColorPicker = new ColorPicker();
    @FXML
    private final Button newTagButton = new Button("Add new tag");
    @FXML
    private Button save;
    @FXML
    private Button close;

    @Inject
    public CardPopupCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
    }


    public void initialize(URL location, ResourceBundle resources) {
        // this is purely for testing the functionality
        Tag tag1 = new Tag("tag1", "red", null);
        Tag tag2 = new Tag("tag2", "red", null);
        Tag tag3 = new Tag("tag3", "red", null);
        Tag tag4 = new Tag("tag4", "red", null);
        List<Tag> tagsOfBoard = new ArrayList<>();
        tagsOfBoard.add(tag1);
        tagsOfBoard.add(tag2);
        tagsOfBoard.add(tag3);
        tagsOfBoard.add(tag4);
        Board board = new Board(true, "board", "pass", "aaa", "red", null, tagsOfBoard);
        CardList cardList = new CardList("name", board);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(new Subtask("subtask1", null));
        subtasks.add(new Subtask("subtask2", null));
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        card = new Card("lol", "desc", "red", cardList, tags, subtasks);
        setCardData(card);
    }


    private void setCardData(Card cardData) {
        cardTitle.setText(cardData.getTitle());
        cardDescription.setText(cardData.getDescription());

        subtaskVBox.getChildren().clear();
        tagHBox.getChildren().clear();
        for (Subtask subtask : cardData.getSubtasks()) {
            HBox subtaskElement = new HBox();
            CheckBox checkBox = new CheckBox(subtask.getTitle());
            deleteSubtask = new Button("x");
            deleteSubtask.getStyleClass().add("remove-subtask-button");
            deleteSubtask.setOnAction(a -> {
                deleteSubtask(subtask);
            });
            subtaskElement.getChildren().add(checkBox);
            subtaskElement.getChildren().add(deleteSubtask);
            subtaskVBox.getChildren().add(subtaskElement);
        }
        
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
        subtaskHBox.setLayoutY(300);
        anchorPane.getChildren().add(subtaskHBox);

        tagHBox.setHgap(5);
        tagHBox.setVgap(5);
        tagHBox.setStyle(".hbox");
        for (Tag tag : cardData.getTags()) {
            // TODO the colors for the tags
            // TODO add the x so the tag can be deleted, because right now the whole tag is just a button
            Button tagButton = new Button(tag.getTitle());
            tagButton.setOnAction(a -> {
                card.getTags().remove(tag);
                setCardData(card);
            });
            tagHBox.getChildren().add(tagButton);
        }

        tagMenu.getItems().clear();
        List<Tag> tagsOfBoard = cardData.getList().getBoard().getTags();
        for (Tag tag : tagsOfBoard) {
            if (!cardData.getTags().contains(tag)) {
                MenuItem menuItem = new MenuItem(tag.getTitle());
                menuItem.setOnAction(event -> {
                    card.getTags().add(tag);
                    card.getList().getBoard().getTags().add(tag);
                    setCardData(card);
                });
                tagMenu.getItems().add(menuItem);
            }
        }

        tagColorPicker.setValue(Color.ORANGE);
        tagHBox.getChildren().add(tagMenu);
        tagHBox.getChildren().add(newTagTextfield);
        tagHBox.getChildren().add(tagColorPicker);

        newTagTextfield.setPromptText("New Tag");
        newTagTextfield.setOnAction(a -> {
            addAndMakeNewTag(newTagTextfield.getText());
        });


        save = new Button("Save");
        save.setOnAction(a -> {
            save(card);
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

    public void addNewSubtask(String entry) {
        if (!entry.isEmpty()) {
            card.getSubtasks().add(new Subtask(entry, card));
            this.setCardData(card);
        }
    }

    public void addAndMakeNewTag(String entry) {
        if (!entry.isEmpty()) {
            boolean tagExists = false;
            for (Tag tag : card.getTags()) {
                if (entry.equals(tag.getTitle())) {
                    // TODO add a warning message that a tag with the same name already exists
                    tagExists = true;
                }
            }
            if (!tagExists) {
                Tag tag = new Tag(entry,
                        tagColorPicker.getValue().toString(), null);
                card.getList().getBoard().getTags().add(tag);
                card.getTags().add(tag);
                newTagTextfield.clear();
                setCardData(card);
            }
        }
    }

    public void close() {
        // this would reset to the state of the card regardless of any changes that have been made
        card.setTitle(cardTitle.getText());
        card.setTitle(cardDescription.getText());
    }

    public void save(Card card) {
        // TODO get the results from the checkboxes
        // this would save any card information that has been changed
    }
}
