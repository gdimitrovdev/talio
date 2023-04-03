package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Subtask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CardComponentCtrl extends AnchorPane {
    private MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;
    private Long cardId;
    private Object updateCard;

    @FXML
    private TextField title;

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

    private boolean cardHasBeenCreated = false;
    public boolean selected = false;

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
        title.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                if (!cardHasBeenCreated) {
                    // Should cancel the creation of a card now
                    ((ListComponentCtrl) getParent()).getChildren().remove(this);
                }
                title.setDisable(true);
            } else {
                title.selectAll();
            }
        });
        this.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode() && !title.isDisabled()) {
                title.setDisable(true);
            }
        });
        // TODO not exactly like the backlog says, but I think this is more intuitive
        //  determine whether it should be this way
        setOnMouseSingleClicked((me) -> {
            this.loadPopup();
        });
        setOnMouseDoubleClicked((me) -> {
            if (title.isDisabled()) {
                title.setDisable(false);
                title.requestFocus();
            }
        });
    }

    public CardComponentCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server, Long cardId) {
        init(mainCtrlTalio, server);
        this.cardHasBeenCreated = true;
        this.cardId = cardId;
        setCard(server.getCard(cardId));
        // Updates that this should handle
        // card: card update DONE
        // card: add tag DONE
        // card: remove tag DONE
        server.registerForMessages("/topic/cards", Card.class, card -> {
            if (card.getId().equals(cardId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setCard(card);
                    }
                });
            }
        });

        server.registerForMessages("/topic/subtasks", Subtask.class, subtask -> {
            if (subtask.getCard().getId().equals(cardId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setCard(subtask.getCard());
                    }
                });
            }
        });

        server.registerForMessages("/topic/subtasks", Card.class, cardReceived -> {
            if (cardReceived.getId().equals(cardId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setCard(cardReceived);
                    }
                });
            }
        });
    }

    public CardComponentCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        init(mainCtrlTalio, server);
        setCard(new Card("Untitled", "", "", null));
        title.requestFocus();
    }

    public Long getCardId() {
        return cardId;
    }

    public void highlight() {
        cardOverview.setStyle("-fx-border-color: blue;");
    }

    public void removeHighlight() {
        cardOverview.setStyle("-fx-border-color: black;");
    }

    // TODO hide the progressbar, the progresslabel and the delete button
    //  if the card has not been created yet

    public void setCard(Card newCardData) {
        title.setText(newCardData.getTitle());

        subtaskProgress.setVisible(newCardData.getSubtasks().size() != 0);
        subtaskLabel.setVisible(newCardData.getSubtasks().size() != 0);
        deleteButton.setVisible(cardHasBeenCreated);

        int numSubtasksDone =
                (int) newCardData.getSubtasks().stream().filter(Subtask::getCompleted).count();
        subtaskLabel.setText(numSubtasksDone + "/" + newCardData.getSubtasks().size());
        subtaskProgress.setProgress((float) numSubtasksDone / newCardData.getSubtasks().size());

        tagsContainer.getChildren().clear();
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
            tagsContainer.getChildren().add(rect);
        }

        this.setOnMouseEntered(e -> {
            this.highlight();
            selected = true;
        });

        this.setOnMouseExited(e -> {
            this.removeHighlight();
            selected = false;
        });
    }

    public void saveTitle() {
        if (!cardHasBeenCreated) {
            ((ListComponentCtrl) getParent()).getChildren().remove(this);
            server.createCard(new Card(title.getText(), "", "",
                    server.getCardList(((ListComponentCtrl) getParent()).getListId())));
        } else {
            Card card = server.getCard(cardId);
            card.setTitle(title.getText());
            server.updateCard(card);
        }
        title.setDisable(true);
    }

    @FXML
    private void delete() {
        server.deleteCard(cardId);
        /*// Get the parent of the card
        Parent parent = this.getParent();

        // Remove the card from the parent if it is an instance of Pane
        if (parent instanceof Pane) {
            Pane parentPane = (Pane) parent;
            parentPane.getChildren().remove(this);
        }*/
    }

    public void loadPopup() {
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
        root1.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });
        /*stage.setOnCloseRequest(event -> {
            setCard(server.getCard(cardId));
        });*/
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
                            onMouseSingleClickedProperty.get().handle(me);
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
