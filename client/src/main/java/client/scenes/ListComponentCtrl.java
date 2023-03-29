package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.CardList;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ListComponentCtrl extends VBox {

    private MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;
    private BoardCtrl board;

    private CardList list;

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

    //not sure about this yet
    //@FXML
    // ScrollPane scrollPane = new ScrollPane();

    public ListComponentCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server, BoardCtrl board,
            CardList list) throws IOException {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
        this.board = board;
        this.list = list;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ListComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });

        this.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                highlight();
            }
        });

        this.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (!board.getDroppedOnCard()) {
                    System.out.println("List from:"
                            + board.getCurrentSelectedCard().getCardData().getList().getTitle());
                    System.out.println(
                            "Card: " + board.getCurrentSelectedCard().getCardData().getTitle());
                    System.out.println("List: " + list.getTitle());
                    CardList updatedList = server.moveCardToListLast(
                            board.getCurrentSelectedCard().getCardData().getId(), list.getId()
                    );
                    // TODO listen to the websocket connection for updated lists
                } else {
                    board.setDroppedOnCard(false);
                }
            }
        });

        this.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                removeHighlight();
            }
        });

        updateOverview();
        //scrollPane.setContent(this);

    }

    public void highlight() {
        listVBox.setStyle("-fx-border-color: blue;");
    }

    public void removeHighlight() {
        listVBox.setStyle("-fx-border-color: black;");
    }

    public void updateOverview() throws IOException {
        for (Card card : list.getCards()) {
            var child = new CardComponentCtrl(mainCtrlTalio, card);

            child.setOnMousePressed(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    board.setCurrentSelectedCard(child);
                }
            });

            child.setOnDragDetected(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    Dragboard db = child.startDragAndDrop(TransferMode.ANY);
                    db.setDragView(child.snapshot(null, null));

                    ClipboardContent content = new ClipboardContent();
                    content.putString("Drag worked");
                    db.setContent(content);

                    child.startFullDrag();

                    event.consume();
                }
            });

            child.setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    event.acceptTransferModes(TransferMode.ANY);
                    event.consume();
                }
            });

            child.setOnDragEntered(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    child.highlight();
                }
            });

            child.setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    board.setDroppedOnCard(true);
                    System.out.println("List from:"
                            + board.getCurrentSelectedCard().getCardData().getList().getTitle());
                    System.out.println(
                            "Card: " + board.getCurrentSelectedCard().getCardData().getTitle());
                    System.out.println("List: " + list.getTitle());
                    System.out.println("Under the card: " + child.getCardData().getTitle());
                    CardList updatedList = server.moveCardToListAfterCard(
                            board.getCurrentSelectedCard().getCardData().getId(),
                            list.getId(),
                            child.getCardData().getId()
                    );
                    // TODO listen to the websocket connection for updated lists
                }
            });

            child.setOnDragExited(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    child.removeHighlight();
                }
            });

            cards.getChildren().add(child);
        }
    }

    @FXML
    private void deleteList() {
        // Get the parent of the list component, which is the board
        Parent parent = this.getParent();
        // Remove the list component from the parent
        if (parent instanceof Pane) {
            Pane parentPane = (Pane) parent;
            parentPane.getChildren().remove(this);
        }
    }

    @FXML
    protected void addCard() throws IOException {
        Card card = new Card("Enter title", "", "", list);
        cards.getChildren().add(new CardComponentCtrl(mainCtrlTalio, card));
    }

    public void refreshList(CardList list) throws IOException {
        cards.getChildren().clear();
        this.list = list;
        this.updateOverview();
    }
}
