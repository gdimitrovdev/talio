package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.CardList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BoardComponentCtrl extends AnchorPane implements Initializable {
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

    public BoardComponentCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio, Board board) throws IOException {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
        this.board = board;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("BoardComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        updateOverview();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server.registerForMessages("/topic/lists", CardList.class, cardList -> {
            // TODO add the new cardlist and do a board refresh
            return;
        });
    }

    public  void updateOverview() throws IOException {
        for(CardList cardList : board.getLists()) {
            innerHBox.getChildren().add(new ListComponentCtrl(mainCtrlTalio, cardList));
        }
    }

    @FXML
    protected void addCardList() throws IOException {
        CardList newList = new CardList("Empty title",board);
        innerHBox.getChildren().add(new ListComponentCtrl(mainCtrlTalio, newList));
    }
    public void refreshBoard(Board board) throws IOException {
        innerHBox.getChildren().clear();
        this.board = board;
        this.updateOverview();
    }
}
