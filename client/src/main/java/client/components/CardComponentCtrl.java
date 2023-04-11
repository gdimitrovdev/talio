package client.components;

import client.scenes.CardPopupCtrl;
import client.scenes.MainCtrlTalio;
import client.utils.ServerUtils;
import client.utils.Utils;
import commons.Card;
import commons.CardList;
import commons.Subtask;
import commons.Topics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CardComponentCtrl extends AnchorPane {
    private MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;
    private ListComponentCtrl list;
    private Long cardId;
    private Object updateCard;

    @FXML
    public TitleField titleField;

    @FXML
    private Button deleteButton;

    @FXML
    private ProgressBar subtaskProgress;

    @FXML
    private Label subtaskLabel;

    @FXML
    private FlowPane tagsContainer;

    @FXML
    private AnchorPane cardOverview;

    private boolean selected = false;
    @FXML
    private ImageView descriptionIcon;

    @FXML
    private ImageView checkboxIcon;

    @FXML
    private HBox detailsContainer;

    @FXML
    private VBox vBox;

    String colors;

    private final Predicate<String> titleValidator = newTitle -> !newTitle.equals("");

    private void init(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CardComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addClickedEventHandler();

        setOnMouseDoubleClicked((me) -> {
            this.loadPopup();
        });

        subtaskProgress.setDisable(true);
    }

    /**
     * This constructor is used when creating a CardComponent of a card that's already on the server
     *
     * @param mainCtrlTalio
     * @param server
     * @param cardId
     */
    public CardComponentCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server, Long cardId) {
        init(mainCtrlTalio, server);
        this.cardId = cardId;
        titleField.init("Untitled", newTitle -> {
            Card card = server.getCard(this.cardId);
            card.setTitle(newTitle);
            server.updateCard(card);
        }, titleValidator);
        setCard(server.getCard(cardId));

        // Updates that this should handle
        // card: card update DONE
        // card: add tag DONE
        // card: remove tag DONE
        server.registerForMessages(Topics.CARDS.toString(), Card.class, card -> {
            if (card.getId().equals(cardId)) {
                Platform.runLater(() -> setCard(card));
            }
        });

        server.registerForMessages(Topics.SUBTASKS.toString(), Subtask.class, subtask -> {
            if (subtask.getCard().getId().equals(cardId)) {
                Platform.runLater(() -> setCard(subtask.getCard()));
            }
        });

        server.registerForMessages(Topics.SUBTASKS.toString(), Card.class, cardReceived -> {
            if (cardReceived.getId().equals(cardId)) {
                Platform.runLater(() -> setCard(cardReceived));
            }
        });

        if (!mainCtrlTalio.hasAuthenticationForBoard(
                server.getCard(cardId).getList().getBoard().getId())) {
            deleteButton.setOnAction(e -> mainCtrlTalio.getBoardComponentCtrl().lock());
            titleField.setDisable(true);
        }
    }

    /**
     * Create a CardComponent for a card that does not yet exist on the server
     * Once the user enters a title and presses enter it will create the card on the server
     *
     * @param mainCtrlTalio
     * @param server
     * @param list
     */
    public CardComponentCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server,
            ListComponentCtrl list) {
        this.list = list;
        init(mainCtrlTalio, server);

        deleteButton.setVisible(false);
        deleteButton.setManaged(false);

        titleField.init(newTitle -> {
            Card card = server.getCard(cardId);
            card.setTitle(newTitle);
            server.updateCard(card);
        }, newTitle -> {
            Platform.runLater(() -> {
                deleteButton.setVisible(true);
                deleteButton.setManaged(true);
                CardList cardList = server.getCardList((list).getListId());
                server.createCard(new Card(newTitle, "", cardList, -1));
            });
        }, () -> {
            Platform.runLater(() -> {
                ((VBox) getParent()).getChildren().remove(this);
            });

        }, titleValidator);
        CardList cardList = server.getCardList((list).getListId());
        setCard(new Card("", "", cardList,
                server.getBoard(cardList.getBoard().getId()).getDefaultPresetNum()));
    }

    public Long getCardId() {
        return cardId;
    }

    public void highlight() {
        this.setStyle("-fx-background-color: " + Utils.getBackgroundColor(colors));
        var rect = new Rectangle();
        rect.setHeight(5);
        rect.setWidth(this.getWidth());
        rect.setFill(Color.web("#9a9aff"));
        rect.setStyle("-fx-border-style: dashed; -fx-border-width: 2; -fx-border-color: #9a9aff");
        rect.setArcHeight(5);
        rect.setArcWidth(5);
        ((VBox) this.getParent()).getChildren()
                .add(((VBox) this.getParent()).getChildren().indexOf(this) + 1, rect);
    }

    public void removeHighlight() {
        this.setStyle("-fx-background-color: " + Utils.getBackgroundColor(colors));
        ((VBox) this.getParent()).getChildren()
                .remove(((VBox) this.getParent()).getChildren().indexOf(this) + 1);
    }

    public void highlightShortcut() {
        this.setStyle("-fx-background-color: " + Utils.getBackgroundColor(colors));
        var rect = new Rectangle();
        rect.setHeight(5);
        rect.setWidth(this.getWidth());
        rect.setFill(Color.web("#9a9aff"));
        rect.setStyle("-fx-border-style: dashed; -fx-border-width: 2; -fx-border-color: #9a9aff");
        rect.setArcHeight(5);
        rect.setArcWidth(5);
    }

    public void removeHighlightShortcut() {
        this.setStyle("-fx-background-color: " + Utils.getBackgroundColor(colors));
    }

    public void setCard(Card newCardData) {
        String colors;

        if (newCardData.getColorPresetNumber().equals(-1)) {
            // Board's default color preset
            colors = newCardData.getList().getBoard().getCardColorPresets()
                    .get(newCardData.getList().getBoard().getDefaultPresetNum());
        } else {
            colors = newCardData.getList().getBoard().getCardColorPresets()
                    .get(newCardData.getColorPresetNumber());
        }

        this.colors = colors;

        String colorBackground = Utils.getBackgroundColor(colors);
        String colorForeground = Utils.getForegroundColor(colors);

        this.setStyle("-fx-color-background: " + colorBackground + "; -fx-color-foreground: "
                + colorForeground + ";");

        this.setStyle("-fx-background-color: " + colorBackground + "; -fx-text-fill: "
                + colorForeground + ";");

        ImageView deleteButtonImageView = (ImageView) deleteButton.getGraphic();
        deleteButtonImageView.setImage(
                Utils.reColor(deleteButtonImageView.getImage(), colorForeground));

        boolean hasSubtasks = newCardData.getSubtasks().size() != 0;
        if (hasSubtasks) {
            checkboxIcon.setVisible(true);
            // This determines if the node is taken into account for the layout calculations of
            // its parent
            checkboxIcon.setManaged(true);
            checkboxIcon.setImage(Utils.reColor(checkboxIcon.getImage(), colorForeground));
            subtaskLabel.setVisible(true);
            subtaskLabel.setManaged(true);
            subtaskLabel.setStyle("-fx-text-fill: " + colorForeground);
            subtaskProgress.setVisible(true);
            subtaskProgress.setManaged(true);

            // Yup, this is the code. You might be wondering how I got here.
            // You see, in order to style the progressbar's colors you need to use the .track and
            // .bar selectors, like we do in CardComponent.css. To do that in code we have to use
            // the lookup method of Node. However, that only works once the Node has been
            // rendered, otherwise lookup returns null. So we attach a listener to the width
            // property of the progressbar, which will be executed only once the Node is rendered.
            // But if we change the color of a Card, it will have already been rendered, so we
            // have to put the same code outside the listener event. However, since we don't know if
            // the card has been rendered, we wrap it in the try catch, so it doesn't crash if the
            // lookup returns null.
            subtaskProgress.widthProperty().addListener((num) -> {
                subtaskProgress.lookup(".track").setStyle("-fx-background-color: ladder"
                        + "(grey, " + colorBackground + " 0%, " + colorForeground + " 100%)");
                subtaskProgress.lookup(".bar")
                        .setStyle("-fx-background-color: " + colorForeground);
            });
            try {
                subtaskProgress.lookup(".track").setStyle("-fx-background-color: ladder"
                        + "(grey, " + colorBackground + " 0%, " + colorForeground + " 100%)");
                subtaskProgress.lookup(".bar")
                        .setStyle("-fx-background-color: " + colorForeground);
            } catch (Exception ignored) {

            }

            int numSubtasksDone =
                    (int) newCardData.getSubtasks().stream().filter(Subtask::getCompleted).count();
            subtaskLabel.setText(numSubtasksDone + "/" + newCardData.getSubtasks().size());
            subtaskProgress.setProgress((float) numSubtasksDone / newCardData.getSubtasks().size());
        } else {
            checkboxIcon.setVisible(false);
            checkboxIcon.setManaged(false);
            subtaskLabel.setVisible(false);
            subtaskLabel.setManaged(false);
            subtaskProgress.setVisible(false);
            subtaskProgress.setManaged(false);
        }

        // Same weirdness as with the progressbar
        titleField.setTitle(newCardData.getTitle());
        titleField.widthProperty().addListener(w -> titleField.lookup(".text-field")
                .setStyle("-fx-text-fill: " + colorForeground));
        try {
            titleField.lookup(".text-field").setStyle("-fx-text-fill: " + colorForeground);
        } catch (Exception ignored) {

        }

        boolean hasDescription =
                newCardData.getDescription() != null && !newCardData.getDescription().equals("");
        if (hasDescription) {
            descriptionIcon.setVisible(true);
            descriptionIcon.setManaged(true);
            descriptionIcon.setImage(Utils.reColor(descriptionIcon.getImage(), colorForeground));
        } else {
            descriptionIcon.setVisible(false);
            descriptionIcon.setManaged(false);
        }

        boolean hasTags = newCardData.getTags().size() != 0;
        if (hasTags) {
            tagsContainer.setVisible(true);
            tagsContainer.setManaged(true);
            tagsContainer.getChildren().clear();
            for (var tag : newCardData.getTags()) {
                var rect = new Rectangle();
                rect.setHeight(10);
                rect.setWidth(70);
                rect.setFill(Color.web(Utils.getBackgroundColor(tag.getColor())));
                rect.setArcHeight(5);
                rect.setArcWidth(5);
                tagsContainer.getChildren().add(rect);
            }
            // This magic here fixes the FlowPane's height, since it doesn't properly do it, itself.
            // If we change the design, it will break. I could do the calculations so, it doesn't
            // use magic numbers, but it works well for now.
            // TODO remove the magic numbers from here
            if (newCardData.getTags().size() >= 1 && newCardData.getTags().size() % 3 == 0) {
                tagsContainer.setMinHeight(((newCardData.getTags().size()) / 3) * 20 - 10);
            } else {
                tagsContainer.setMinHeight(((newCardData.getTags().size()) / 3) * 20 + 10);
            }
        } else {
            tagsContainer.setVisible(false);
            tagsContainer.setManaged(false);
        }
    }

    @FXML
    private void delete() {
        Alert confirmationDialogue = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this card ?", ButtonType.YES, ButtonType.NO);
        confirmationDialogue.showAndWait();

        if (confirmationDialogue.getResult() == ButtonType.YES) {
            server.deleteCard(cardId);
        }
    }

    public void loadPopup() {
        Stage stage = new Stage();
        var popup = new CardPopupCtrl(mainCtrlTalio, server.getCard(cardId), server, stage);
        stage.setScene(new Scene(popup));
        stage.setTitle("Talio: Card Settings");
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setOnCloseRequest(event -> {
            if (popup.hasUnsavedChanges()) {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText(
                        "Any unsaved changes will be lost. Are you sure you want to discard "
                                + "them?");
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.YES) {
                        stage.close();
                    }
                });
            }

        });

        stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            System.out.println("close");
            if (e.getCode() == KeyCode.ESCAPE) {
                // TODO add the alert window
                stage.close();
            }
        });

        stage.show();

        /*stage.setOnCloseRequest(event -> {
            setCard(server.getCard(cardId));
        });*/
    }

    public void close() {
        server.removeUpdateEvent(updateCard);
    }

    // Adapted from https://stackoverflow.com/a/70493396/6431494
    // TODO extract the single/double click event handler so that it can be used with any UI
    //  component using composition
    private long delayMs = 250;
    private ClickRunner latestClickRunner = null;

    private final ObjectProperty<EventHandler<MouseEvent>> onMouseSingleClickedProperty =
            new SimpleObjectProperty<>();
    private final ObjectProperty<EventHandler<MouseEvent>> onMouseDoubleClickedProperty =
            new SimpleObjectProperty<>();

    private class ClickRunner implements Runnable {

        private final Runnable onClick;
        private boolean aborted = false;

        public ClickRunner(Runnable onClick) {
            this.onClick = onClick;
        }

        public void abort() {
            this.aborted = true;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!aborted) {
                Platform.runLater(onClick);
            }
        }
    }

    private void addClickedEventHandler() {
        // Handling the mouse clicked event (not using 'onMouseClicked' so it can still be used
        // by developer).
        EventHandler<MouseEvent> eventHandler = me -> {
            switch (me.getButton()) {
                case PRIMARY:
                    if (me.getClickCount() == 1) {
                        latestClickRunner = new ClickRunner(() -> {
                            try {
                                onMouseSingleClickedProperty.get().handle(me);
                            } catch (Exception ignored) {

                            }

                        });
                        CompletableFuture.runAsync(latestClickRunner);
                    }
                    if (me.getClickCount() == 2) {
                        if (latestClickRunner != null) {
                            latestClickRunner.abort();
                        }
                        onMouseDoubleClickedProperty.get().handle(me);
                    }
                    break;
                default:
                    break;
            }
        };
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
    }

    public void setOnMouseSingleClicked(EventHandler<MouseEvent> eventHandler) {
        this.onMouseSingleClickedProperty.set(eventHandler);
    }

    public void setOnMouseDoubleClicked(EventHandler<MouseEvent> eventHandler) {
        this.onMouseDoubleClickedProperty.set(eventHandler);
    }

    public long getSingleClickDelayMillis() {
        return delayMs;
    }

    public void setSingleClickDelayMillis(long singleClickDelayMillis) {
        this.delayMs = singleClickDelayMillis;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
