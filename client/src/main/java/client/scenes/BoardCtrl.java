package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.CardList;
import elements.BoardElement;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class BoardCtrl {
    private  final ServerUtils server ;
    private  MainCtrlTalio mainCtrlTalio;


    @Inject
    public BoardCtrl(MainCtrlTalio mainCtrlTalio, ServerUtils server) {
        this.mainCtrlTalio = mainCtrlTalio;
        this.server = server;
    }
    @FXML
    private AnchorPane root;
    @FXML
    private TextField boardField;

    /**
     * displays the board in the overview
     */
    public void displayBoard(){

        //instance of a board object which is given as a parameter
        Board current = new Board(false,"Example board",null,null, null, null, null );
        Card c1 = new Card (null, "Card1", null, null, null, null);
        Card c2 = new Card(null, "Card2", null, null, null, null);
        ArrayList<Card> cards1 = new ArrayList<>();
        cards1.add(c1);
        cards1.add(c2);
        CardList list1 = new CardList("example list 1", current, cards1);
        CardList list2 = new CardList("example list 2", current,cards1);
        ArrayList<CardList>cardLists = new ArrayList<>();
        cardLists.add(list1);
        cardLists.add(list2);
        current.setLists(cardLists);

        //"Long boardID = Long.parseLong(boardField.getText());
        //Board current = serverUtils.getBoardById(boardID)" can be used

        BoardElement boardElement = new BoardElement(current, mainCtrlTalio);
        root.getChildren().add(boardElement);
    }
}
