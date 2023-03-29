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

    private Board board;
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
    private CardComponentCtrl currentSelectedCard;

    private boolean droppedOnCard = false;

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
        server.registerForMessages("/topic/lists", CardList.class, cardList -> {
            // TODO add the new cardlist and do a board refresh
            return;
        });
    }

    public void initialize(Board board) throws IOException {
        this.board = board;

        updateOverview();

        boardName.setText(board.getName());
    }

    public void updateOverview() throws IOException {
        for (CardList cardList : board.getLists()) {
            innerHBox.getChildren()
                    .add(new ListComponentCtrl(mainCtrlTalio, server, this, cardList));
        }
    }

    @FXML
    protected void addCardList() throws IOException {
        CardList newList = new CardList("Empty title", board);
        innerHBox.getChildren().add(new ListComponentCtrl(mainCtrlTalio, server, this, newList));
    }

    public void refreshBoard(Board board) throws IOException {
        innerHBox.getChildren().clear();
        this.board = board;
        this.updateOverview();
    }

    //return to home method
    @FXML
    protected void backToHome() throws IOException {
        mainCtrlTalio.showHome();
        // mainCtrlTalio.getPrimaryStage().setScene(scene);
    }

    //open share board method
    @FXML
    protected void share() throws IOException {
        mainCtrlTalio.showShareBoard(board);
    }

    //open the settings method
    @FXML
    protected void settings() throws IOException {
        mainCtrlTalio.showBoardSettings();
    }

}
