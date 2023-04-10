package client.scenes;

import client.components.CardComponentCtrl;
import client.components.ListComponentCtrl;
import client.components.TitleField;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.CardList;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BoardCtrl implements Initializable {
    private MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;
    private Long boardId;
    private CardComponentCtrl currentSelectedCard;
    private boolean droppedOnCard = false;
    private Object updateBoard, updateList;

    @FXML
    private AnchorPane pane;

    @FXML
    private ToolBar toolbar;

    @FXML
    private ScrollPane root;

    @FXML
    private HBox outerHBox;

    @FXML
    private HBox innerHBox;

    @FXML
    private Button newListButton;

    @FXML
    private Button shareBTN;

    @FXML
    private Button settingsBTN;

    @FXML
    private Button backHomeBTN;

    @FXML
    private Label boardNameLabel;

    @Inject
    public BoardCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) throws IOException {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public CardComponentCtrl getCurrentSelectedCard() {
        return this.currentSelectedCard;
    }

    public void setCurrentSelectedCard(CardComponentCtrl card) {
        this.currentSelectedCard = card;
    }

    public boolean getDroppedOnCard() {
        return this.droppedOnCard;
    }

    public void setDroppedOnCard(boolean droppedOnCard) {
        this.droppedOnCard = droppedOnCard;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initialize(Long boardId) {
        this.boardId = boardId;
        refresh();
        // Updates that the board controller should handle:
        // - board updates DONE
        // - list creation DONE
        // - list deletion DONE
        // - board deletion TODO
        server.registerForMessages("/topic/boards", CardList.class, cardList -> {
            if (cardList.getBoard().getId().equals(boardId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });

        server.registerForMessages("/topic/boards", Board.class, board -> {
            if (board.getId().equals(boardId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });
    }

    public void refresh() {
        var board = server.getBoard(boardId);

        //split the string with the colors
        String[] colors = board.getBoardColor().split("/"); // Split the string into two parts
        String bgColor = colors[0]; // Get the first part
        String fontColor = colors[1];
        //changing the color of the board
        outerHBox.setStyle("-fx-background-color:" + bgColor);
        toolbar.setStyle("-fx-background-color:" + bgColor);

        //changing the color of the board font
        boardNameLabel.setStyle("-fx-text-fill: " + fontColor);
        settingsBTN.setStyle("-fx-text-fill: " + fontColor);
        shareBTN.setStyle("-fx-text-fill: " + fontColor);
        newListButton.setStyle("-fx-text-fill: " + fontColor);
        backHomeBTN.setStyle("-fx-text-fill: " + fontColor);



        boardNameLabel.setText(board.getName());
        Platform.runLater(() -> innerHBox.getChildren().clear());
        for (CardList cardList : board.getLists()) {
            ListComponentCtrl listComponent = new ListComponentCtrl(mainCtrlTalio,
                    server, this, cardList.getId());
            Platform.runLater(() -> innerHBox.getChildren().add(listComponent));
        }
    }

    // TODO I believe, we shouldn't really create the list, before the user enters the title
    @FXML
    protected void addCardList() {
        server.createCardList(new CardList("Untitled", server.getBoard(boardId)));
    }

    @FXML
    protected void backToHome() {
        mainCtrlTalio.showHome();
    }

    @FXML
    protected void share() {
        mainCtrlTalio.showShareBoard(server.getBoard(boardId));
    }

    @FXML
    protected void settings() {
        mainCtrlTalio.showBoardSettings(server.getBoard(boardId));
    }

    @FXML
    protected void tags() {
        Board emptyBoard = new Board();
        emptyBoard.setId(boardId);
        mainCtrlTalio.showTagManagement(emptyBoard);
    }

    public void close() {
        innerHBox.getChildren().forEach(c -> ((ListComponentCtrl) c).close());
        server.removeUpdateEvent(updateBoard);
        server.removeUpdateEvent(updateList);
    }

    public void pressedLeft() {
        int[] position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && position[0] > 0 && listComponentCtrls.get(position[0] - 1).getCards().getChildren().size() > 0) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).removeHighlight();
            ((CardComponentCtrl) listComponentCtrls.get(position[0] - 1).getCards().getChildren().get(0)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(position[0] - 1).getCards().getChildren().get(0)).highlight();
        }
    }

    public void pressedRight() {
        int[] position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && position[0] < listComponentCtrls.size() - 1 && listComponentCtrls.get(position[0] + 1).getCards().getChildren().size() > 0) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1]))
                    .setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1]))
                    .removeHighlight();
            ((CardComponentCtrl) listComponentCtrls.get(position[0] + 1).getCards().getChildren().get(0))
                    .setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(position[0] + 1).getCards().getChildren().get(0))
                    .highlight();
        }
    }

    public void pressedDown() {
        int[] position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && !listComponentCtrls.isEmpty() && position[1] < listComponentCtrls.get(position[0]).getCards().getChildren().size() - 1) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).removeHighlight();
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1] + 1)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1] + 1)).highlight();
        }
    }

    public void pressedUp() {
        int[] position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && position[1] > 0) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).removeHighlight();
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1] - 1)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1] - 1)).highlight();
        }
    }

    public void pressedEnter() {
        int[] position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && listComponentCtrls.size() > position[0] && listComponentCtrls.get(position[0]).getCards().getChildren().size() > position[1]) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).loadPopup();
        }
    }

    public void pressedShiftC() {
        int[] position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        listComponentCtrls.get(position[0]).addCard();
    }

    public void pressedShiftL() {
        this.addCardList();
    }

    public void pressedE() {
        int[] position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        CardComponentCtrl cardComponentCtrl;

        if (position[0] != -1 && listComponentCtrls.size() > position[0] && listComponentCtrls.get(position[0]).getCards().getChildren().size() > position[1]) {
            cardComponentCtrl = (CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1]);

            cardComponentCtrl.titleField.getChildren().clear();
            cardComponentCtrl.titleField.getChildren().add(new TextField(server.getCard(cardComponentCtrl.getCardId()).getTitle()));
            cardComponentCtrl.titleField.getChildren().get(0).requestFocus();

            CardComponentCtrl finalCardComponentCtrl = cardComponentCtrl;
            cardComponentCtrl.titleField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    Card card = server.getCard(finalCardComponentCtrl.getCardId());
                    card.setTitle(((TextField) finalCardComponentCtrl.titleField.getChildren().get(0)).getText());
                    server.updateCard(card);
                    cardComponentCtrl.requestFocus();
                }
            });
        }
    }

    public void pressedDelete() {
        int[] position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && listComponentCtrls.size() > position[0] && listComponentCtrls.get(position[0]).getCards().getChildren().size() > position[1]) {
            server.deleteCard(((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).getCardId());
        }
    }

    public void pressedShiftUp() {
        int[] position = this.findPositionOfCard();

        if (position[0] != -1 && position[1] > 0) {
            List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();
            if (listComponentCtrls.size() > position[0] && listComponentCtrls.get(position[0]).getCards().getChildren().size() > position[1]) {
                ListComponentCtrl listComponentCtrl = listComponentCtrls.get(position[0]);
                List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
                for (int n = 0; n < listComponentCtrl.getCards().getChildren().size(); n++) {
                    cardComponentCtrls.add((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(n));
                }
                server.moveCardToListAfterCard(cardComponentCtrls.get(position[1] - 1).getCardId(), listComponentCtrls.get(position[0]).getListId(), cardComponentCtrls.get(position[1]).getCardId());
            }
            listComponentCtrls = this.getCardListsFromBoard();
            List<ListComponentCtrl> finalListComponentCtrls = listComponentCtrls;
            Platform.runLater(() -> {
                ((CardComponentCtrl) finalListComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).setSelected(false);
                ((CardComponentCtrl) finalListComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).removeHighlight();
                ((CardComponentCtrl) finalListComponentCtrls.get(position[0]).getCards().getChildren().get(position[1] - 1)).setSelected(true);
                ((CardComponentCtrl) finalListComponentCtrls.get(position[0]).getCards().getChildren().get(position[1] - 1)).highlight();
            });
        }

    }

    public void pressedShiftDown() {
        int[] position = this.findPositionOfCard();
        if (position[0] != -1) {
            List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();
            ListComponentCtrl listComponentCtrl = listComponentCtrls.get(position[0]);
            List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();

            for (int n = 0; n < listComponentCtrl.getCards().getChildren().size(); n++) {
                cardComponentCtrls.add((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(n));
            }

            if (position[1] < listComponentCtrl.getCards().getChildren().size() - 1 && listComponentCtrls.size() > position[0]
                    && listComponentCtrls.get(position[0]).getCards().getChildren().size() - 1 > position[1]) {
                server.moveCardToListAfterCard(cardComponentCtrls.get(position[1]).getCardId(), listComponentCtrls.get(position[0]).getListId(), cardComponentCtrls.get(position[1] + 1).getCardId());
            }
        }
    }

    // TODO put the focus on the tag creation section
    public void pressedT() {
        int[] position = this.findPositionOfCard();
        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();
        if (position[0] != -1 && listComponentCtrls.size() > position[0] && listComponentCtrls.get(position[0]).getCards().getChildren().size() > position[1]) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren().get(position[1])).loadPopup();
        }
    }

    public void pressedC() {
        int[] position = this.findPositionOfCard();
        if (position[0] != -1) {
            this.mainCtrlTalio.showBoardSettings(server.getBoard(this.boardId));
        }
    }

    public void mouseMovement(MouseEvent event) {
        if (event.getTarget().getClass().getSimpleName().equals("CardComponentCtrl")) {
            CardComponentCtrl cardComponentCtrl = (CardComponentCtrl) event.getTarget();
            List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();

            for (Object object : this.innerHBox.getChildren()) {
                listComponentCtrls.add((ListComponentCtrl) object);
                ListComponentCtrl listComponentCtrl = (ListComponentCtrl) object;

                for (Node node : listComponentCtrl.getCards().getChildren()) {
                    CardComponentCtrl cardComponentCtrl1 = (CardComponentCtrl) node;
                    cardComponentCtrl1.setSelected(false);
                    cardComponentCtrl1.removeHighlight();
                }
            }

            cardComponentCtrl.setSelected(true);
            cardComponentCtrl.highlight();
        }
    }

    public boolean checkTextField(CardComponentCtrl cardComponentCtrl) {
        VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
        HBox hBox = (HBox) vBox.getChildren().get(0);
        TitleField titleField = (TitleField) hBox.getChildren().get(0);
        if (titleField.getChildren().get(0).isFocused()) {
            return false;
        }
        return true;
    }

    public int[] findPositionOfCard() {
        int[] position = new int[2];
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            if (listComponentCtrl.titleField.getChildren().get(0).isFocused()) {
                run = false;
            }
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                if (run) {
                    run = this.checkTextField(cardComponentCtrl);
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    position = new int[]{n, k};
                }
            }
        }
        if (run) {
            return position;
        } else {
            return new int[]{-1, -1};
        }
    }

    public List<ListComponentCtrl> getCardListsFromBoard() {
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
        }
        return listComponentCtrls;
    }

    public HBox getInnerHBox() {
        return innerHBox;
    }

    public void setInnerHBox(HBox hBox) {
        this.innerHBox = hBox;
    }
}
