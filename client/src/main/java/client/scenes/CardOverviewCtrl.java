package client.scenes;

import commons.Card;
import commons.Subtask;
import commons.Tag;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class CardOverviewCtrl implements Initializable {
    @FXML
    private AnchorPane cardOverview;

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

    private commons.Card cardData;

    private boolean cardHasBeenCreated = false;

    // TODO figure out exactly how this dependency injection stuff works,
    //  I don't think we need it for now though
    /*@Inject
    public CardOverviewCtrl() {

    }*/

    // TODO hide the progressbar, the progresslabel and the delete button
    //  if the card has not been created yet

    // TODO figure out a better way to initialize these UI components,
    //  but for now it should be fine
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addClickedEventHandler();

        setCardData(new Card("Enter title",
                "",
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>()));

        title.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
                if(!cardHasBeenCreated) {
                    System.out.println("Should cancel the creation of the card now");
                }
                title.setDisable(true);
            }
            else {
                title.selectAll();
            }
        });

        cardOverview.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode() && !title.isDisabled()) {
                title.setDisable(true);
            }
        });
        // TODO not exactly like the backlog says, but I think this is more intuitive
        //  determine whether it should be this way
        setOnMouseSingleClicked((me) -> System.out.println("Should open pop-up now"));
        setOnMouseDoubleClicked((me) -> {
            if(title.isDisabled()) {
                title.setDisable(false);
                title.requestFocus();
            }
        });

        title.requestFocus();
    }

    public void setCardData(commons.Card newCardData) {
        cardData = newCardData;
        title.setText(cardData.getTitle());
        // TODO this calculation will have to change once the Subtask Model changes in #40, #41
        int numSubtasksDone = (int) cardData.getSubtasks().stream().filter(x -> x.getTitle().equals("Done")).count();
        subtaskLabel.setText(numSubtasksDone + "/" + cardData.getSubtasks().size() + "Subtasks");
        subtaskProgress.setProgress((float)numSubtasksDone / cardData.getSubtasks().size());

        tagsContainer.getChildren().clear();
        for(var tag : cardData.getTags()) {
            var rect = new Rectangle();
            // TODO remove magic numbers from here
            rect.setHeight(10);
            rect.setWidth(70);
            rect.setFill(Color.web("0x" + tag.getColor().substring(1)));
            // TODO perhaps move those to a CSS file
            rect.setArcHeight(5);
            rect.setArcWidth(5);
            tagsContainer.getChildren().add(rect);
        }

    }

    public commons.Card getCardData() {
        return cardData;
    }

    public void saveTitle() {
        // TODO remove the details, they are just here for testing
        if(!cardHasBeenCreated) {
            setCardData(new Card(title.getText(),
                    "Do certain things",
                    null,
                    null,
                    List.of(
                            new Tag("Urgent", "#FF0000", null, null),
                            new Tag("Feature", "#00FF00", null, null)
                    ),
                    List.of(
                            new Subtask("Done", null),
                            new Subtask("Done", null),
                            new Subtask("TODO 3", null)
                    ))
            );
        }
        else {
            cardData.setTitle(title.getText());
            setCardData(cardData);
        }

        cardHasBeenCreated = true;
        title.setDisable(true);
    }

    public void delete() {
        System.out.println("Card " + this + " should be deleted");
    }

    // TODO extract the single/double click event handler so that it can be used with any UI
    //  component using composition
    long delayMs = 250;
    private ClickRunner latestClickRunner = null;

    private final ObjectProperty<EventHandler<MouseEvent>> onMouseSingleClickedProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<EventHandler<MouseEvent>> onMouseDoubleClickedProperty = new SimpleObjectProperty<>();

    private class ClickRunner implements Runnable {

        private final Runnable  onClick;
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
        cardOverview.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
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