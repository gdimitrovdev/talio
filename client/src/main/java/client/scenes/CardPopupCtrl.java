package client.scenes;

import client.components.TitleField;
import client.utils.ServerUtils;
import client.utils.Utils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.Subtask;
import commons.Tag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CardPopupCtrl extends AnchorPane {
    private final MainCtrlTalio mainCtrlTalio;
    private final ServerUtils server;
    private Card card;

    @FXML
    private TextField cardTitle;

    @FXML
    private TextField cardDescription;

    @FXML
    private VBox subtasksContainer;

    @FXML
    private MenuButton addTagButton;

    @FXML
    private ScrollPane subtasksScroll;

    @FXML
    private FlowPane tagsContainer;

    @FXML
    private ScrollPane tagsScrollPane;

    @FXML
    private ChoiceBox<String> colorSchemeDropdown;

    private Stage stage;
    private Set<Tag> tagsToDelete = new HashSet<>();
    private Set<Subtask> subtasksToDelete = new HashSet<>();

    private final Predicate<String> subtaskTitleValidator = title ->
            !title.equals("")
                    && card.getSubtasks().stream().noneMatch(s -> s.getTitle().equals(title));

    @Inject
    public CardPopupCtrl(MainCtrlTalio mainCtrlTalio, Card card, ServerUtils server, Stage stage) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.card = card;
        this.server = server;
        this.stage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CardPopup.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        cardTitle.setText(this.card.getTitle());
        cardDescription.setText(card.getDescription());

        for (Subtask subtask : Utils.getSubtasksSorted(card)) {
            subtasksContainer.getChildren().add(generateSubtaskComponent(subtask));
        }

        initTagMenuButton();

        for (Tag tag : card.getTags()) {
            tagsContainer.getChildren().add(tagsContainer.getChildren().size() - 1,
                    generateTagComponent(tag));
        }

        colorSchemeDropdown.getItems().add("(default)");
        for (String scheme : card.getList().getBoard().getCardColorPresets()) {
            if (!scheme.equals("")) {
                colorSchemeDropdown.getItems().add(Utils.getColorSchemeName(scheme));
            }
        }
        if (card.getColorPresetNumber().equals(-1)) {
            colorSchemeDropdown.setValue("(default)");
        } else {
            colorSchemeDropdown.setValue(Utils.getColorSchemeName(
                    card.getList().getBoard().getCardColorPresets()
                            .get(card.getColorPresetNumber())));
        }

        server.registerForMessages("/topic/cards/deleted", Card.class, cardReceived -> {
            if (cardReceived.getId().equals(card.getId())) {
                Platform.runLater(() -> {
                    Alert box = new Alert(Alert.AlertType.ERROR);
                    box.setTitle("Card deleted");
                    box.setContentText("The card that you are editing has been deleted!");
                    box.setOnCloseRequest(e -> stage.close());
                    box.showAndWait();
                });
            }
        });

    }

    public void newSubtask() {
        subtasksContainer.getChildren().add(generateSubtaskComponent(null));
        // TODO fix this, as sometimes it doesn't scroll properly
        // Scroll to bottom
        Platform.runLater(() -> {
            subtasksScroll.setVvalue(1);
        });
    }

    /**
     * Generate an HBox that can be placed in the subtaskContainer
     *
     * @param subtask If the given subtask is null it will generate a component with a
     *                TitleField that requires a name for the subtask to be entered, and once it
     *                is a new subtask will be added to the card (the local one, that will only
     *                be saved on the server once Save is pressed).
     */
    public HBox generateSubtaskComponent(Subtask subtask) {
        HBox subtaskElement = new HBox();
        subtaskElement.setMaxWidth(Double.POSITIVE_INFINITY);
        subtaskElement.setSpacing(10);

        CheckBox checkBox = new CheckBox();
        checkBox.getStyleClass().add("subtask-checkbox");
        TitleField titleField = new TitleField();
        titleField.setMaxWidth(200);
        Button moveUpSubtask = new Button("^");
        Button moveDownSubtask = new Button("v");
        Pane spacer = new Pane();
        spacer.setMaxWidth(Double.POSITIVE_INFINITY);
        spacer.setMinWidth(100);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button deleteSubtask = new Button();
        var trashIcon = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/bin.png"))));
        trashIcon.setFitHeight(18);
        trashIcon.setFitWidth(18);
        deleteSubtask.setGraphic(trashIcon);
        deleteSubtask.getStyleClass().add("remove-subtask-button");

        if (subtask != null) {
            checkBox.setSelected(subtask.getCompleted());
            checkBox.setOnAction(a -> {
                subtask.setCompleted(!subtask.getCompleted());
            });

            titleField.init(subtask.getTitle(), newTitle -> {
            }, subtaskTitleValidator);

            moveUpSubtask.setOnAction(a -> {
                Utils.subtaskComponentMoveUp(subtaskElement, subtask, subtasksContainer);
            });
            moveDownSubtask.setOnAction(a -> {
                Utils.subtaskComponentMoveDown(subtaskElement, subtask, subtasksContainer);
            });

            deleteSubtask.setOnAction(a -> {
                card.removeSubtask(subtask);
                subtasksContainer.getChildren().remove(subtaskElement);
                subtasksToDelete.add(subtask);
            });
        } else {
            checkBox.setSelected(false);
            // This is purposely still managed, so that the TitleField aligns with the other
            // subtasks
            checkBox.setVisible(false);

            moveUpSubtask.setVisible(false);
            moveDownSubtask.setVisible(false);
            deleteSubtask.setVisible(false);

            titleField.init(updatedTitle -> {
                    }, newTitle -> {
                        Subtask s = Utils.createNewSubtask(card, newTitle);
                        checkBox.setVisible(true);
                        moveUpSubtask.setVisible(true);
                        moveDownSubtask.setVisible(true);
                        deleteSubtask.setVisible(true);

                        checkBox.setOnAction(a -> {
                            s.setCompleted(!s.getCompleted());
                        });

                        moveUpSubtask.setOnAction(a -> {
                            Utils.subtaskComponentMoveUp(subtaskElement, s, subtasksContainer);
                        });

                        moveDownSubtask.setOnAction(a -> {
                            Utils.subtaskComponentMoveDown(subtaskElement, s, subtasksContainer);
                        });

                        deleteSubtask.setOnAction(a -> {
                            card.removeSubtask(s);
                            subtasksContainer.getChildren().remove(subtaskElement);
                        });

                    }, () -> {
                        subtasksContainer.getChildren().remove(subtaskElement);
                    }, subtaskTitleValidator
            );
        }

        subtaskElement.getChildren().add(checkBox);
        subtaskElement.getChildren().add(moveUpSubtask);
        subtaskElement.getChildren().add(moveDownSubtask);
        subtaskElement.getChildren().add(titleField);
        subtaskElement.getChildren().add(spacer);
        subtaskElement.getChildren().add(deleteSubtask);
        return subtaskElement;
    }

    public HBox generateTagComponent(Tag tag) {
        HBox tagElement = new HBox();
        tagElement.getStyleClass().add("tag");
        tagElement.setStyle("-fx-background-color: " + Utils.getBackgroundColor(tag.getColor())
                + "; -fx-border-width: 1; -fx-border-color: " + Utils.getForegroundColor(
                tag.getColor())
        );

        Label tagText = new Label(tag.getTitle());
        tagText.getStyleClass().add("tag-text");
        tagText.setStyle("-fx-text-fill: " + Utils.getForegroundColor(tag.getColor()));

        Button deleteTagButton = new Button("X");
        deleteTagButton.setOnAction(a -> {
            card.getTags().remove(tag);
            tagsToDelete.add(tag);
            tagsContainer.getChildren().remove(tagElement);
            addTagButton.getItems().add(generateMenuItemForTag(tag));
            addTagButton.setVisible(true);
            addTagButton.setManaged(true);
        });
        deleteTagButton.getStyleClass().add("remove-tag-button");
        deleteTagButton.setStyle("-fx-text-fill: " + Utils.getForegroundColor(tag.getColor()));

        tagElement.getChildren().add(tagText);
        tagElement.getChildren().add(deleteTagButton);
        return tagElement;
    }

    private MenuItem generateMenuItemForTag(Tag tag) {
        MenuItem menuItem = new MenuItem(tag.getTitle());
        menuItem.setOnAction(event -> {
            tagsToDelete.remove(tag);
            card.getTags().add(tag);
            addTagButton.getItems().remove(menuItem);
            tagsContainer.getChildren().add(tagsContainer.getChildren().size() - 1,
                    generateTagComponent(tag));
            if (addTagButton.getItems().size() == 0) {
                addTagButton.setVisible(false);
                addTagButton.setManaged(false);
            }
            // Scroll to bottom
            Platform.runLater(() -> {
                tagsScrollPane.setVvalue(1);
            });
        });
        return menuItem;
    }

    private void initTagMenuButton() {
        addTagButton.setVisible(false);
        addTagButton.setManaged(false);
        List<Tag> tagsOfBoard = card.getList().getBoard().getTags();
        addTagButton.getItems().clear();
        for (Tag tag : tagsOfBoard) {
            if (!card.getTags().contains(tag)) {
                addTagButton.setVisible(true);
                addTagButton.setManaged(true);
                addTagButton.getItems().add(generateMenuItemForTag(tag));
            }
        }
    }

    public void save() {
        card.setTitle(cardTitle.getText());
        card.setDescription(cardDescription.getText());

        if (colorSchemeDropdown.getValue().equals("(default)")) {
            card.setColorPresetNumber(-1);
        } else {
            for (int i = 0; i < card.getList().getBoard().getCardColorPresets().size(); ++i) {
                String scheme = card.getList().getBoard().getCardColorPresets().get(i);
                if (!scheme.equals("") && Utils.getColorSchemeName(scheme)
                        .equals(colorSchemeDropdown.getValue())) {
                    card.setColorPresetNumber(i);
                    break;
                }
            }
        }

        server.updateCard(card);

        List<Tag> tagsOnServer = server.getCard(card.getId()).getTags();
        List<Tag> tagsToAdd = new ArrayList<Tag>();
        List<Tag> tagsToRemove = new ArrayList<Tag>();

        for (Tag tag : card.getTags()) {
            if (tagsOnServer.stream()
                    .noneMatch(tagOnServer -> tagOnServer.getId().equals(tag.getId()))) {
                tagsToAdd.add(tag);
            }
        }
        for (Tag tagOnServer : tagsOnServer) {
            if (card.getTags().stream().noneMatch(tag -> tag.getId().equals(tagOnServer.getId()))) {
                tagsToRemove.add(tagOnServer);
            }
        }

        for (Tag tag : tagsToAdd) {
            server.addTagToCard(card.getId(), tag.getId());

        }
        for (Tag tag : tagsToRemove) {
            server.removeTagFromCard(card.getId(), tag.getId());
        }

        for (Subtask subtask : subtasksToDelete) {
            server.deleteSubtask(subtask.getId());
        }

        for (Subtask subtask : card.getSubtasks()) {
            if (subtask.getId() == null) {
                server.createSubtask(subtask);
            } else {
                server.updateSubtask(subtask);
            }
        }

        stage.close();
    }

    public void delete() {

    }

    private void assignEmptyBoardWithId(Tag tag) {
        Board empty = new Board();
        empty.setId(tag.getBoard().getId());
        tag.setBoard(empty);
    }
}
