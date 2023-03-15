package client.scenes;

import commons.Card;
import commons.CardList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ListComponentCtrl extends VBox {
    private MainCtrlTalio mainCtrlTalio;

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

    public ListComponentCtrl(MainCtrlTalio mainCtrlTalio, CardList list) throws IOException {
        this.mainCtrlTalio = mainCtrlTalio;
        this.list = list;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ListComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        updateOverview();
    }

    public void updateOverview() {
        for (Card card : list.getCards()) {

        }
    }

    @FXML
    protected void deleteList() {

    }

    @FXML
    protected void addCard() {

    }
}
