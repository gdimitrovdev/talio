package client.scenes;

import client.components.CardComponentCtrl;
import client.components.ListComponentCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.CardList;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

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
        System.out.println("BOARD COLOR " + board.getBoardColor());
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
}
