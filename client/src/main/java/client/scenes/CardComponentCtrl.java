package client.scenes;

import client.components.TitleField;
import client.utils.ServerUtils;
import commons.Card;
import commons.CardList;
import commons.Subtask;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CardComponentCtrl extends AnchorPane {
    private MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;
    private ListComponentCtrl list;
    private Long cardId;
    private Object updateCard;

    @FXML
    private TitleField titleField;

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

    @FXML
    private ImageView descriptionIcon;

    @FXML
    private ImageView checkboxIcon;

    @FXML
    private HBox detailsContainer;

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
            FXMLLoader cardPopupLoader = new FXMLLoader(getClass().getResource("CardPopup.fxml"));
            try {
                cardPopupLoader.setController(new CardPopupCtrl(mainCtrlTalio,
                        server.getCard(cardId), server));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Parent root1 = null;
            try {
                root1 = cardPopupLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
            /*stage.setOnCloseRequest(event -> {
                setCard(server.getCard(cardId));
            });*/
        });
        /*setOnMouseDoubleClicked((me) -> {
            if (title.isDisabled()) {
                title.setDisable(false);
                title.requestFocus();
            }
        });*/

        subtaskProgress.setDisable(true);
    }

    public CardComponentCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server, Long cardId) {
        init(mainCtrlTalio, server);
        this.cardId = cardId;
        titleField.init("Untitled", newTitle -> {
            Card card = server.getCard(this.cardId);
            card.setTitle(newTitle);
            server.updateCard(card);
        });
        setCard(server.getCard(cardId));

        // Updates that this should handle
        // card: card update DONE
        // card: add tag DONE
        // card: remove tag DONE
        server.registerForMessages("/topic/cards", Card.class, card -> {
            if (card.getId().equals(cardId)) {
                Platform.runLater(() -> setCard(card));
            }
        });

        server.registerForMessages("/topic/subtasks", Subtask.class, subtask -> {
            if (subtask.getCard().getId().equals(cardId)) {
                Platform.runLater(() -> setCard(subtask.getCard()));
            }
        });

        server.registerForMessages("/topic/subtasks", Card.class, cardReceived -> {
            if (cardReceived.getId().equals(cardId)) {
                Platform.runLater(() -> setCard(cardReceived));
            }
        });
    }

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
                server.createCard(new Card(newTitle, "", cardList,
                        server.getBoard(cardList.getBoard().getId()).getDefaultPresetNum()));
            });
        }, () -> {
            Platform.runLater(() -> {
                ((VBox) getParent()).getChildren().remove(this);
            });

        });
        CardList cardList = server.getCardList((list).getListId());
        setCard(new Card("", "", cardList,
                        server.getBoard(cardList.getBoard().getId()).getDefaultPresetNum()));
    }

    public Long getCardId() {
        return cardId;
    }

    public void highlight() {
        cardOverview.setStyle("-fx-border-color: blue;-fx-border-width: 0 0 2 0");
    }

    public void removeHighlight() {
        cardOverview.setStyle("-fx-border-width: 0 0 0 0;");
    }

    // TODO hide the progressbar, the progresslabel and the delete button
    //  if the card has not been created yet

    public void setCard(Card newCardData) {
        titleField.setTitle(newCardData.getTitle());

        boolean hasSubtasks = newCardData.getSubtasks().size() != 0;
        if (hasSubtasks) {
            checkboxIcon.setVisible(true);
            // This determines if the node is taken into account for the layout calculations of
            // its parent
            checkboxIcon.setManaged(true);
            subtaskLabel.setVisible(true);
            subtaskLabel.setManaged(true);
            subtaskProgress.setVisible(true);
            subtaskProgress.setManaged(true);
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

        boolean hasDescription =
                newCardData.getDescription() != null && !newCardData.getDescription().equals("");
        if (hasDescription) {
            descriptionIcon.setVisible(true);
            descriptionIcon.setManaged(true);
        } else {
            descriptionIcon.setVisible(false);
            descriptionIcon.setManaged(false);
        }

        boolean hasTags = newCardData.getTags().size() != 0;
        if (hasTags) {
            tagsContainer.setVisible(true);
            tagsContainer.setManaged(true);
            Platform.runLater(() -> tagsContainer.getChildren().clear());
            for (var tag : newCardData.getTags()) {
                var rect = new Rectangle();
                // TODO remove magic numbers from here
                rect.setHeight(10);
                rect.setWidth(70);
                // TODO fix this, when we figure out the colors
                //rect.setFill(Color.web("0x" + tag.getColor().substring(2)));
                rect.setFill(Color.web(tag.getColor()));
                // TODO perhaps move those to a CSS file
                rect.setArcHeight(5);
                rect.setArcWidth(5);
                Platform.runLater(() -> tagsContainer.getChildren().add(rect));
            }
        } else {
            tagsContainer.setVisible(false);
            tagsContainer.setManaged(false);
        }
    }

    @FXML
    private void delete() {
        server.deleteCard(cardId);
    }

    public void close() {
        server.removeUpdateEvent(updateCard);
    }

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
        //Handling the mouse clicked event (not using 'onMouseClicked' so it can still be used by developer).
        EventHandler<MouseEvent> eventHandler = me -> {
            switch (me.getButton()) {
                case PRIMARY:
                    if (me.getClickCount() == 1) {
                        latestClickRunner = new ClickRunner(() -> {
                            System.out.println("ButtonWithDblClick : SINGLE Click fired");
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
                        System.out.println("ButtonWithDblClick : DOUBLE Click fired");
                        onMouseDoubleClickedProperty.get().handle(me);
                    }
                    break;
                case SECONDARY:
                    // Right-click operation. Not implemented since usually no double RIGHT click needs to be caught.
                    break;
                default:
                    break;
            }
        };
        //Adding the event handler
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
}
