package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.CardList;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class JoinBoardCtrl {
    private final ServerUtils server;
    private final MainCtrlTalio mainCtrlTalio;
    @FXML
    private TextField fieldBoardCode;

    @FXML
    private Button buttonJoin;

    @Inject
    public JoinBoardCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void clickJoinBoard() throws IOException {
        // Button click

        String code = fieldBoardCode.getText();
        Board newBoard = server.retrieveBoard(code);
        mainCtrlTalio.showBoard(newBoard);
        System.out.printf("Code: %s", code);
    }
    
    private void generateExampleBoard() {
        //"Long boardID = Long.parseLong(boardField.getText());
        //Board current = serverUtils.getBoardById(boardID)" can be used
        // Note: server utils is in MainCtrlTalio now so that we have only one instance of it

        this.board = new Board(false, "Example board", "pwd", "hash_893290840923904", "#333333");

        CardList list1 = new CardList("example list 1", null);
        CardList list2 = new CardList("example list 2", null);

        Card c1 = new Card("Title1", "Card1", "#FF0000", null);
        Card c2 = new Card("Title2", "Card2", "#FF0000", null);

        list1.addCard(c1);
        list1.addCard(c2);

        this.board.addCardList(list1);
        this.board.addCardList(list2);
    }

    public void clickBackHome() {
        mainCtrlTalio.showHome();
    }
}
