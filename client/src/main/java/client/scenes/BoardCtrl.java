package client.scenes;

import client.components.TitleField;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.CardList;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

    public void close() {
        innerHBox.getChildren().forEach(c -> ((ListComponentCtrl) c).close());
        server.removeUpdateEvent(updateBoard);
        server.removeUpdateEvent(updateList);
    }

    public void switchLeft() {
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        int posList = 0;
        int posCard = 0;
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    posList = n;
                    posCard = k;
                }
            }
        }
        if (posList > 0 && listComponentCtrls.get(posList).getCards().getChildren().size() > 0 && run) {
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard)).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard)).removeHighlight();
            ((CardComponentCtrl) listComponentCtrls.get(posList - 1).getCards().getChildren().get(0)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(posList - 1).getCards().getChildren().get(0)).highlight();
        }
    }

    public void switchRight() {
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        int posList = 0;
        int posCard = 0;
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    posList = n;
                    posCard = k;
                }
            }
        }
        if (posList < listComponentCtrls.size() - 1 && listComponentCtrls.get(posList).getCards().getChildren().size() > 0 && run) {
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard))
                    .setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard))
                    .removeHighlight();
            ((CardComponentCtrl) listComponentCtrls.get(posList + 1).getCards().getChildren().get(0))
                    .setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(posList + 1).getCards().getChildren().get(0))
                    .highlight();
        }
    }

    public void moveDown() {
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        int posList = 0;
        int posCard = 0;
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    posList = n;
                    posCard = k;
                }
            }
        }
        if (!listComponentCtrls.isEmpty() && posCard < listComponentCtrls.get(posList).getCards().getChildren().size() - 1 && run) {
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard)).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard)).removeHighlight();
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard + 1)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard + 1)).highlight();
        }
    }

    public void moveUp() {
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        int posList = 0;
        int posCard = 0;
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    posList = n;
                    posCard = k;
                }
            }
        }
        if (posCard > 0) {
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard)).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard)).removeHighlight();
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard - 1)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(posList).getCards().getChildren().get(posCard - 1)).highlight();
        }
    }

    public void pressedEnter() {
        boolean run = true;
        int listpos = 0;
        int cardpos = 0;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrls.get(n).getCards().getChildren().get(k)).getSelected()) {
                    listpos = n;
                    cardpos = k;
                }
            }
        }
        if (run) {
            ((CardComponentCtrl) listComponentCtrls.get(listpos).getCards().getChildren().get(cardpos)).loadPopup();
        }
    }

    public void pressedE() {
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        CardComponentCtrl cardComponentCtrl = null;
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl1 = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl1.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    cardComponentCtrl = (CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k);
                }
            }
        }
        if (cardComponentCtrl != null && run) {
            System.out.println("lol");
            VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
            HBox hBox = (HBox) vBox.getChildren().get(0);
            TitleField titleField = (TitleField) hBox.getChildren().get(0);
            titleField.getChildren().get(0).requestFocus();
        }
    }

    public void pressedDelete() {
        boolean run = true;
        int listpos = 0;
        int cardpos = 0;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    listpos = n;
                    cardpos = k;
                }
            }
        }
        if (run) {
            server.deleteCard(((CardComponentCtrl) listComponentCtrls.get(listpos).getCards().getChildren().get(cardpos)).getCardId());
        }
    }

    public void pressedShiftUp() {
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        int posList = 0;
        int posCard = 0;
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    posList = n;
                    posCard = k;
                }
            }
        }
        ListComponentCtrl listComponentCtrl = listComponentCtrls.get(posList);
        List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
        for (int n = 0; n < listComponentCtrl.getCards().getChildren().size(); n++) {
            cardComponentCtrls.add((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(n));
        }

        if (posCard > 0 && run) {
            CardComponentCtrl temp = cardComponentCtrls.get(posCard - 1);
            cardComponentCtrls.set(posCard - 1, cardComponentCtrls.get(posCard));
            cardComponentCtrls.set(posCard, temp);

            listComponentCtrl.getCards().getChildren().clear();
            for (CardComponentCtrl cardComponentCtrl : cardComponentCtrls) {
                listComponentCtrl.getCards().getChildren().add(cardComponentCtrl);
            }
        }
    }

    public void pressedShiftDown() {
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        int posList = 0;
        int posCard = 0;
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    posList = n;
                    posCard = k;
                }
            }
        }
        ListComponentCtrl listComponentCtrl = listComponentCtrls.get(posList);
        List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();

        for (int n = 0; n < listComponentCtrl.getCards().getChildren().size(); n++) {
            cardComponentCtrls.add((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(n));
        }

        if (posCard < listComponentCtrl.getCards().getChildren().size() - 1 && run) {
            CardComponentCtrl temp = cardComponentCtrls.get(posCard + 1);
            cardComponentCtrls.set(posCard + 1, cardComponentCtrls.get(posCard));
            cardComponentCtrls.set(posCard, temp);

            listComponentCtrl.getCards().getChildren().clear();
            for (CardComponentCtrl cardComponentCtrl : cardComponentCtrls) {
                listComponentCtrl.getCards().getChildren().add(cardComponentCtrl);
            }
        }
    }

    // TODO put the focus on the tag creation section
    public void pressedT() {
        boolean run = true;
        int listpos = 0;
        int cardpos = 0;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()
                        && (listComponentCtrl.getCards().getChildren().get(k)).isFocusWithin()) {
                    listpos = n;
                    cardpos = k;
                }
            }
        }
        if (run) {
            ((CardComponentCtrl) listComponentCtrls.get(listpos).getCards().getChildren().get(cardpos)).loadPopup();
        }
    }

    public void pressedC() {
        boolean run = true;
        int listpos = 0;
        int cardpos = 0;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        CardComponentCtrl cardComponentCtrl = null;
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl = (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl1 = ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                VBox vBox = (VBox) cardComponentCtrl1.getChildren().get(0);
                HBox hBox = (HBox) vBox.getChildren().get(0);
                TitleField titleField = (TitleField) hBox.getChildren().get(0);
                if (titleField.getChildren().get(0).isFocused()) {
                    run = false;
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k)).getSelected()) {
                    listpos = n;
                    cardpos = k;
                }
            }
        }
        cardComponentCtrl = (CardComponentCtrl) listComponentCtrls.get(listpos).getCards().getChildren().get(cardpos);

        if (run) {
            Stage stage = new Stage();
            stage.setTitle("Talio: Pick Card color");
            TilePane tilepane = new TilePane();

            // TODO add the color presets to the window

            Scene scene = new Scene(tilepane, 730, 150);
            stage.setScene(scene);
            stage.show();
            Card card = server.getCard(cardComponentCtrl.getCardId());
            scene.setOnKeyPressed(e1 -> {
                if (e1.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });
        }
    }

    public void mouseMovement(MouseEvent event) {
        if (event.getTarget().getClass().getSimpleName().equals("CardComponentCtrl")) {
            CardComponentCtrl cardComponentCtrl = (CardComponentCtrl) event.getTarget();
            List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
            for (Object object : this.innerHBox.getChildren()) {
                listComponentCtrls.add((ListComponentCtrl) object);
                ListComponentCtrl listComponentCtrl = (ListComponentCtrl) object;
                for (Node node : listComponentCtrl.getCards().getChildren()) {
                    CardComponentCtrl cardComponentCtrl1 = (CardComponentCtrl) node;
                    cardComponentCtrl1.setSelected(false);
                    cardComponentCtrl1.removeHighlight();
                }
            }
            cardComponentCtrl.setSelected(true);
            cardComponentCtrl.highlight();
        }
    }
}
