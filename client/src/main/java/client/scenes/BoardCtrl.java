package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.CardList;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class BoardCtrl implements Initializable {
    private MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;
    private Long boardId;
    private CardComponentCtrl currentSelectedCard;
    private boolean droppedOnCard = false;
    private Object updateBoard, updateList;

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
    private TextField boardName;

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
        updateBoard = server.addUpdateEvent(Board.class, (board) -> {
            if (board.getId().equals(boardId)) {
                refresh();
            }
        });
        updateList = server.addUpdateEvent(CardList.class, (list) -> {
            if (list.getBoard().getId().equals(boardId)) {
                refresh();
            }
        });
    }

    public void refresh() {
        var board = server.getBoard(boardId);
        boardName.setText(board.getName());
        innerHBox.getChildren().forEach(c -> ((ListComponentCtrl) c).close());
        innerHBox.getChildren().clear();
        for (CardList cardList : board.getLists()) {
            innerHBox.getChildren().add(new ListComponentCtrl(mainCtrlTalio, server, this,
                    cardList.getId()));
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

    public void close() {
        innerHBox.getChildren().forEach(c -> ((ListComponentCtrl) c).close());
        server.removeUpdateEvent(updateBoard);
        server.removeUpdateEvent(updateList);
    }
}
