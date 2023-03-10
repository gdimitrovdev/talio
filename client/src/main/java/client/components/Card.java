package client.components;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

import java.util.concurrent.CompletableFuture;

public class Card extends Pane {
    public Card() {
        super();
        TextField textField = new TextField();
        textField.setLayoutX(10);
        textField.setLayoutY(10);
        /*setOnMousePressed(mouseEvent -> {
            // CLICK catches
            if (mouseEvent.getClickCount() == 1) {
                System.out.println("Button clicked");
            } else if (mouseEvent.getClickCount() == 2)
                System.out.println("Button double clicked");
        });*/
        getChildren().add(textField);
        setPadding(new Insets(10));
        textField.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent; -fx-background-insets: 0; -fx-padding: 1 3 1 3;");
        //textField.setBorder(new Border(new BorderStroke()));
        //textField.setPadding();
        addClickedEventHandler();
        setOnMouseSingleClicked(me -> {
            System.out.println("single click");
        });
        setOnMouseDoubleClicked(me -> {
            System.out.println("double click");
        });
    }

    public void setOnMouseSingleClicked(EventHandler<MouseEvent> eventHandler) {
        this.onMouseSingleClickedProperty.set(eventHandler);
    }

    public void setOnMouseDoubleClicked(EventHandler<MouseEvent> eventHandler) {
        this.onMouseDoubleClickedProperty.set(eventHandler);
    }
    public long getSingleClickDelayMillis() {
        return singleClickDelayMillis;
    }

    public void setSingleClickDelayMillis(long singleClickDelayMillis) {
        this.singleClickDelayMillis = singleClickDelayMillis;
    }

    private long singleClickDelayMillis = 250;
    private ClickRunner latestClickRunner  = null;

    private ObjectProperty<EventHandler<MouseEvent>> onMouseSingleClickedProperty = new SimpleObjectProperty<>();
    private ObjectProperty<EventHandler<MouseEvent>> onMouseDoubleClickedProperty = new SimpleObjectProperty<>();

    private void addClickedEventHandler() {
        //Handling the mouse clicked event (not using 'onMouseClicked' so it can still be used by developer).
        EventHandler<MouseEvent> eventHandler = me -> {
            switch (me.getButton()) {
                case PRIMARY:
                    if (me.getClickCount() == 1) {
                        latestClickRunner = new ClickRunner(() -> {
                            //System.out.println("ButtonWithDblClick : SINGLE Click fired");
                            onMouseSingleClickedProperty.get().handle(me);
                        });
                        CompletableFuture.runAsync(latestClickRunner);
                    }
                    if (me.getClickCount() == 2) {
                        if (latestClickRunner != null) {
                            latestClickRunner.abort();
                        }
                        //System.out.println("ButtonWithDblClick : DOUBLE Click fired");
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
        addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
    }

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
                Thread.sleep(singleClickDelayMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!aborted) {
                Platform.runLater(onClick::run);
            }
        }
    }
}
