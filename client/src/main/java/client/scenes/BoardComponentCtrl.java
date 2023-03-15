package client.scenes;

import commons.Board;
import commons.CardList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class BoardComponentCtrl extends AnchorPane {
    private MainCtrlTalio mainCtrlTalio;

    private Board board;
    @FXML
    private ScrollPane root;

    @FXML
    private HBox outerHBox;

    @FXML
    private HBox innerHBox;

    @FXML
    private Button newListButton;

    public BoardComponentCtrl(MainCtrlTalio mainCtrlTalio, Board board) throws IOException {
        this.mainCtrlTalio = mainCtrlTalio;
        this.board = board;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("BoardComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        updateOverview();
    }

    private void updateOverview() {
        for(CardList cardList : board.getLists()) {
            //innerHBox.getChildren().add(new ListComponentCtrl(mainCtrlTalio, cardList));
        }
    }

    @FXML
    protected void addCardList() {
        CardList newList = new CardList("Empty title",board);

        //innerHBox.getChildren().add(new ListComponentCtrl(mainCtrlTalio, newList));
    }
}
