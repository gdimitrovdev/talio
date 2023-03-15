package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.CardList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;
import java.util.ArrayList;

public class JoinBoardCtrl {
    private  MainCtrlTalio mainCtrlTalio;

    private Board board;

    @FXML
    private AnchorPane root;
    @FXML
    private TextField boardField;


    @Inject
    public JoinBoardCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.generateExampleBoard();
    }


    private void generateExampleBoard() {
        //"Long boardID = Long.parseLong(boardField.getText());
        //Board current = serverUtils.getBoardById(boardID)" can be used
        // Note: server utils is in MainCtrlTalio now so that we have only one instance of it

        this.board = new Board(false,"Example board",null,null, null, null, null );
        Card c1 = new Card (null, "Card1", null, null, null, null);
        Card c2 = new Card(null, "Card2", null, null, null, null);
        ArrayList<Card> cards1 = new ArrayList<>();
        cards1.add(c1);
        cards1.add(c2);
        CardList list1 = new CardList("example list 1", this.board, cards1);
        CardList list2 = new CardList("example list 2", this.board,cards1);
        ArrayList<CardList>cardLists = new ArrayList<>();
        cardLists.add(list1);
        cardLists.add(list2);
        this.board.setLists(cardLists);
    }

    /**
     * displays the board in the overview
     */
    public void displayBoard() throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("BoardOverview.fxml"));
        root.getChildren().add(loader.load());

    }
}
