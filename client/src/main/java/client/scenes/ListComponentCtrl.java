package client.scenes;
import commons.Card;
import commons.CardList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

    //not sure about this yet
    //@FXML
   // ScrollPane scrollPane = new ScrollPane();


    public ListComponentCtrl(MainCtrlTalio mainCtrlTalio, CardList list) throws IOException {
        this.mainCtrlTalio = mainCtrlTalio;
        this.list = list;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ListComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        updateOverview();
        //scrollPane.setContent(this);

    }

    public void updateOverview() throws IOException {
        for (Card card : list.getCards()) {
            cards.getChildren().add(new CardComponentCtrl(mainCtrlTalio, card));
        }
    }

    @FXML
    private void deleteList() {
        // Get the parent of the list component, which is the board
        Parent parent = this.getParent();
        // Remove the list component from the parent
        if (parent instanceof Pane) {
            Pane parentPane = (Pane) parent;
            parentPane.getChildren().remove(this);
        }
    }
    @FXML
    protected void addCard() throws IOException {
        Card card = new Card("Enter title", "", "", list);
        cards.getChildren().add(new CardComponentCtrl(mainCtrlTalio, card));
    }

    public void refreshList(CardList list) throws IOException {
        cards.getChildren().clear();
        this.list = list;
        this.updateOverview();
    }


}
