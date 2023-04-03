package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.CardList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ListComponentCtrl extends VBox {
    private MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;
    private BoardCtrl boardCtrl;
    private Object updateList;
    private Long listId;

    @FXML
    private HBox titleHolder;

    @FXML
    private TextField titleField;

    @FXML
    private Button deleteListBtn;

    @FXML
    private VBox cards;

    @FXML
    private Button addCardBtn;

    @FXML
    private VBox listVBox;

    @FXML
    ScrollPane scrollPane = new ScrollPane();

    public ListComponentCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server, BoardCtrl boardCtrl,
            Long listId) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
        this.boardCtrl = boardCtrl;
        this.listId = listId;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ListComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
        });

        this.setOnDragEntered(event -> highlight());

        this.setOnDragDropped(event -> {
            if (!boardCtrl.getDroppedOnCard()) {
                server.moveCardToListLast(boardCtrl.getCurrentSelectedCard().getCardId(), listId);
            } else {
                boardCtrl.setDroppedOnCard(false);
            }
        });

        this.setOnDragExited(event -> removeHighlight());

        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            titleField.selectAll();
        });

        refresh();
        //scrollPane.setContent(this);

        // Updates that the list should handle
        // - list: card deleted DONE
        // - list: move card to or from DONE
        // - list: create list DONE
        // - list: update list DONE
        server.registerForMessages("/topic/lists", Card.class, card -> {
            if (card.getList().getId().equals(listId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });

        server.registerForMessages("/topic/lists", CardList.class, cardList -> {
            if (cardList.getId().equals(listId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });

        this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

            if (e.getCode() == KeyCode.ENTER) {
                List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
                for (int n = 0; n < cards.getChildren().size(); n++) {
                    CardComponentCtrl cardComponentCtrl = (CardComponentCtrl) cards.getChildren().get(n);
                    cardComponentCtrls.add(cardComponentCtrl);
                }
                if (cards.getChildren().size() > 0) {
                    CardComponentCtrl cardComponentCtrl = cardComponentCtrls.
                            stream().filter(a -> a.selected).toList().get(0);
                    cardComponentCtrl.loadPopup();
                }
            }

            if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) {
                List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
                for (int n = 0; n < cards.getChildren().size(); n++) {
                    CardComponentCtrl cardComponentCtrl = (CardComponentCtrl) cards.getChildren().get(n);
                    cardComponentCtrls.add(cardComponentCtrl);
                }
                CardComponentCtrl cardComponentCtrl = cardComponentCtrls.
                        stream().filter(a -> a.selected).toList().get(0);
                if (!cardComponentCtrl.isFocusWithin() == true) {
                    server.deleteCard(cardComponentCtrl.getCardId());
                }
            }

            if (e.getCode() == KeyCode.DOWN) {
                List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
                int pos = 0;
                for (int n = 0; n < cards.getChildren().size(); n++) {
                    CardComponentCtrl cardComponentCtrl = (CardComponentCtrl) cards.getChildren().get(n);
                    if (cardComponentCtrl.selected) {
                        pos = n;
                    }
                    cardComponentCtrls.add(cardComponentCtrl);
                }
                CardComponentCtrl cardComponentCtrlSelected = cardComponentCtrls.
                        stream().filter(a -> a.selected).toList().get(0);
                if (pos < cards.getChildren().size() - 1) {
                    cardComponentCtrlSelected.selected = false;
                    cardComponentCtrlSelected.removeHighlight();
                    cardComponentCtrls.get(pos + 1).selected = true;
                    cardComponentCtrls.get(pos + 1).highlight();
                }
            }

            if (e.getCode() == KeyCode.UP) {
                List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
                int pos = 0;
                for (int n = 0; n < cards.getChildren().size(); n++) {
                    CardComponentCtrl cardComponentCtrl = (CardComponentCtrl) cards.getChildren().get(n);
                    if (cardComponentCtrl.selected) {
                        pos = n;
                    }
                    cardComponentCtrls.add(cardComponentCtrl);
                }
                CardComponentCtrl cardComponentCtrlSelected = cardComponentCtrls.
                        stream().filter(a -> a.selected).toList().get(0);
                if (pos > 0) {
                    cardComponentCtrlSelected.selected = false;
                    cardComponentCtrlSelected.removeHighlight();
                    cardComponentCtrls.get(pos - 1).selected = true;
                    cardComponentCtrls.get(pos - 1).highlight();
                }
            }

            if (e.getCode() == KeyCode.RIGHT) {
                List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
                int pos = 0;
                for (int n = 0; n < cards.getChildren().size(); n++) {
                    CardComponentCtrl cardComponentCtrl = (CardComponentCtrl) cards.getChildren().get(n);
                    if (cardComponentCtrl.selected) {
                        pos = n;
                    }
                    cardComponentCtrls.add(cardComponentCtrl);
                }
                CardComponentCtrl cardComponentCtrlSelected = cardComponentCtrls.
                        stream().filter(a -> a.selected).toList().get(0);
            }

            if (e.getCode() == KeyCode.E) {
                List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
                int pos = 0;
                for (int n = 0; n < cards.getChildren().size(); n++) {
                    CardComponentCtrl cardComponentCtrl = (CardComponentCtrl) cards.getChildren().get(n);
                    if (cardComponentCtrl.selected) {
                        pos = n;
                    }
                    cardComponentCtrls.add(cardComponentCtrl);
                }
                CardComponentCtrl cardComponentCtrlSelected = cardComponentCtrls.
                        stream().filter(a -> a.selected).toList().get(0);
                VBox vBox = (VBox) cardComponentCtrlSelected.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TextField textField = (TextField) hBox.getChildren().get(0);
                textField.requestFocus();
                textField.clear();
                textField.setText(server.getCard(cardComponentCtrlSelected.getCardId()).getTitle());
            }
        });
    }

    public Long getListId() {
        return listId;
    }

    // TODO replace this with standard colors
    public void highlight() {
        listVBox.setStyle("-fx-border-color: blue;");
    }

    public void removeHighlight() {
        listVBox.setStyle("-fx-border-color: black;");
    }

    public void close() {
        server.removeUpdateEvent(updateList);
        cards.getChildren().forEach(c -> ((CardComponentCtrl) c).close());
    }

    public void refresh() {
        titleField.setText(server.getCardList(listId).getTitle());
        System.out.println("refreshing: " + listId);
        cards.getChildren().forEach(c -> ((CardComponentCtrl) c).close());
        cards.getChildren().clear();
        List<Card> cardsOfList = server.getCardList(listId).getCards();
        for (Card card : cardsOfList) {
            var child = new CardComponentCtrl(mainCtrlTalio, server, card.getId());

            child.setOnMousePressed(event -> boardCtrl.setCurrentSelectedCard(child));

            child.setOnDragDetected(event -> {
                Dragboard db = child.startDragAndDrop(TransferMode.ANY);
                db.setDragView(child.snapshot(null, null));

                ClipboardContent content = new ClipboardContent();
                content.putString("Drag worked");
                db.setContent(content);

                child.startFullDrag();

                event.consume();
            });

            child.setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            });

            child.setOnDragEntered(event -> child.highlight());

            child.setOnDragDropped(event -> {
                boardCtrl.setDroppedOnCard(true);
                server.moveCardToListAfterCard(
                        boardCtrl.getCurrentSelectedCard().getCardId(),
                        listId,
                        child.getCardId()
                );
            });

            child.setOnDragExited(event -> child.removeHighlight());

            cards.getChildren().add(child);
        }
    }

    public void updateListTitle() {
        CardList currentList = server.getCardList(listId);
        currentList.setTitle(titleField.getText());
        server.updateCardList(currentList);
    }

    @FXML
    private void deleteList() {
        server.deleteCardList(listId);
        /*
        // Get the parent of the list component, which is the board
        Parent parent = this.getParent();
        // Remove the list component from the parent
        if (parent instanceof Pane) {
            Pane parentPane = (Pane) parent;
            parentPane.getChildren().remove(this);
        }
        */
    }

    @FXML
    protected void addCard() {
        Card card = server.createCard(new Card("Untitled", "", "", server.getCardList(listId)));
        server.moveCardToListLast(card.getId(), listId);
    }
}
