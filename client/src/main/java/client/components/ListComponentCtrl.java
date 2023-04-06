package client.components;

import client.scenes.BoardCtrl;
import client.scenes.MainCtrlTalio;
import client.utils.ServerUtils;
import commons.Card;
import commons.CardList;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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
    private TitleField titleField;

    @FXML
    public Button deleteListBtn;

    @FXML
    private VBox cards;

    @FXML
    public Button addCardBtn;

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

        titleField.init("You should not see this", (newTitle) -> {
            CardList currentList = server.getCardList(listId);
            currentList.setTitle(newTitle);
            server.updateCardList(currentList);
        });

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

        refresh();

        // Updates that the list should handle
        // - list: card deleted DONE
        // - list: move card to or from DONE
        // - list: create list DONE
        // - list: update list DONE
        /*server.registerForMessages("/topic/lists", Card.class, card -> {
            if (card.getList().getId().equals(listId)) {
                Platform.runLater(this::refresh);
            }
        });*/

        server.registerForMessages("/topic/lists", CardList.class, cardList -> {
            if (cardList.getId().equals(listId)) {
                Platform.runLater(this::refresh);
            }
        });

        server.registerForMessages("/topic/cards/deleted", Card.class, cardReceived -> {
            if (cardReceived.getList().getId().equals(listId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });
    }

    public Long getListId() {
        return listId;
    }

    // TODO replace this with standard colors
    public void highlight() {
        this.setStyle("-fx-border-color: blue;");
    }

    public void removeHighlight() {
        this.setStyle("-fx-border-color: black;");
    }

    public void close() {
        server.removeUpdateEvent(updateList);
        cards.getChildren().forEach(c -> ((CardComponentCtrl) c).close());
    }

    public void refresh() {
        //get the color of the lists
        String[] colorsLists = server.getCardList(listId).
                getBoard().getListsColor().split("/"); // Split the string into two parts
        String bgColorLists = colorsLists[0];
        String fontColorLists = colorsLists[1];
        this.setStyle("-fx-background-color:" + bgColorLists);
        this.titleField.setStyle("-fx-text-fill: " + fontColorLists);
        this.deleteListBtn.setStyle("-fx-text-fill: " + fontColorLists);
        this.addCardBtn.setStyle("-fx-text-fill: " + fontColorLists);
        titleField.setTitle(server.getCardList(listId).getTitle());
        System.out.println("refreshing: " + listId);
        cards.getChildren().forEach(c -> ((CardComponentCtrl) c).close());
        cards.getChildren().clear();
        List<Card> cardsOfList = server.getCardList(listId).getCards();
        for (Card card : cardsOfList.stream().sorted(Comparator.comparing(Card::getListPriority)).toList()) {
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

    @FXML
    private void deleteList() {
        server.deleteCardList(listId);
    }

    @FXML
    protected void addCard() {
        cards.getChildren().add(new CardComponentCtrl(mainCtrlTalio, server, this));
        // Scroll to bottom
        Platform.runLater(() -> {
            scrollPane.setVvalue(1);
        });
    }

}
