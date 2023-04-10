package client.scenes;

import client.components.CardComponentCtrl;
import client.components.ListComponentCtrl;
import client.components.TitleField;
import client.utils.ServerUtils;
import client.utils.Utils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Topics;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.messaging.simp.stomp.StompSession;

public class BoardCtrl implements Initializable {
    @FXML
    private Button tagsManagementBTN;
    private MainCtrlTalio mainCtrlTalio;
    private ServerUtils server;
    private Long boardId;
    private CardComponentCtrl currentSelectedCard;
    private boolean droppedOnCard = false;
    private Object updateBoard, updateList;
    private StompSession.Subscription deleteSubscription;

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

    private int[] position = new int[] {-1, -1};

    private String operation = "";
    @FXML
    private Button lockButton;

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
    }

    public void refresh() {
        server.registerForMessages(Topics.BOARDS.toString(), CardList.class, cardList -> {
            if (cardList.getBoard().getId().equals(boardId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });

        server.registerForMessages(Topics.BOARDS.toString(), Board.class, board -> {
            if (board.getId().equals(boardId)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });

        server.registerForMessages("/topic/boards/deleted", Board.class, board -> {
            Platform.runLater(() -> {
                if (board.getId().equals(boardId)) {
                    mainCtrlTalio.showHome();

                    Alert box = new Alert(Alert.AlertType.ERROR);
                    box.setTitle("Board deleted");
                    box.setContentText("A board has been deleted!");
                    box.showAndWait();
                }
            });
        });

        var board = server.getBoard(boardId);

        String bgColor = Utils.getBackgroundColor(board.getBoardColor());
        String fontColor = Utils.getForegroundColor(board.getBoardColor());

        outerHBox.setStyle("-fx-background-color:" + bgColor);
        toolbar.setStyle("-fx-background-color:" + bgColor);

        boardNameLabel.setStyle("-fx-text-fill: " + fontColor);
        boardNameLabel.setText(board.getName());

        settingsBTN.setStyle("-fx-background-color: " + bgColor);
        shareBTN.setStyle("-fx-background-color:" + bgColor);
        tagsManagementBTN.setStyle("-fx-background-color:" + bgColor);
        shareBTN.setStyle("-fx-background-color:" + bgColor);
        backHomeBTN.setStyle("-fx-background-color: " + bgColor);
        lockButton.setStyle("-fx-background-color: " + bgColor);

        var lockIcon = new ImageView();
        lockIcon.setFitWidth(30);
        lockIcon.setFitHeight(30);
        if (mainCtrlTalio.hasAuthenticationForBoard(boardId)) {
            lockIcon.setImage(new Image("/client/images/unlocked.png"));
            newListButton.setOnAction(e -> addCardList());
            settingsBTN.setOnAction(e -> settings());
            tagsManagementBTN.setOnAction(e -> tags());
        } else {
            lockIcon.setImage(new Image("/client/images/locked.png"));
            newListButton.setOnAction(e -> lock());
            settingsBTN.setOnAction(e -> lock());
            tagsManagementBTN.setOnAction(e -> lock());
        }
        lockButton.setGraphic(lockIcon);

        /*Platform.runLater(() -> innerHBox.getChildren().clear());
        for (CardList cardList : board.getLists()) {
            ListComponentCtrl listComponent = new ListComponentCtrl(mainCtrlTalio,
                    server, this, cardList.getId());
            Platform.runLater(() -> innerHBox.getChildren().add(listComponent));
        }*/

        innerHBox.getChildren().clear();
        for (CardList cardList : board.getLists()) {
            ListComponentCtrl listComponent = new ListComponentCtrl(mainCtrlTalio,
                    server, this, cardList.getId());
            innerHBox.getChildren().add(listComponent);
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

    public void lock() {
        Dialog<String> dialog = new Dialog<>();

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        pwd.setPromptText("Enter password");

        VBox content = new VBox();
        content.setSpacing(5);
        content.getChildren().add(pwd);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return pwd.getText();
            }
            return null;
        });

        if (mainCtrlTalio.hasAuthenticationForBoard(boardId)) {
            PasswordField pwdConfirm = new PasswordField();
            pwdConfirm.setPromptText("Confirm password");
            content.getChildren().add(pwdConfirm);
            if (server.getBoard(boardId).getReadOnlyCode().equals("")) {
                // There is no password for this board
                dialog.setTitle("Talio: Lock Board");
                dialog.setHeaderText("This board has no password.\nWould you like to set one?");
                dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
                pwd.textProperty().addListener((obs, oldVal, newVal) -> {
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(
                            newVal.equals("") || !newVal.equals(pwdConfirm.getText()));
                });
                pwdConfirm.textProperty().addListener((obs, oldVal, newVal) -> {
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(
                            !newVal.equals(pwd.getText()));
                });
                Board board = server.getBoard(boardId);
                pwd.setOnAction(e -> {
                    if (!dialog.getDialogPane().lookupButton(ButtonType.OK).isDisable()) {
                        board.setReadOnlyCode(pwd.getText());
                        server.updateBoard(board);
                        mainCtrlTalio.savePasswordForBoard(boardId, board.getReadOnlyCode());
                        dialog.close();
                    }
                });
                pwdConfirm.setOnAction(e -> {
                    if (!dialog.getDialogPane().lookupButton(ButtonType.OK).isDisable()) {
                        board.setReadOnlyCode(pwd.getText());
                        server.updateBoard(board);
                        mainCtrlTalio.savePasswordForBoard(boardId, board.getReadOnlyCode());
                        dialog.close();
                    }
                });
                try {
                    board.setReadOnlyCode(dialog.showAndWait().get());
                    server.updateBoard(board);
                    mainCtrlTalio.savePasswordForBoard(boardId, board.getReadOnlyCode());
                } catch (Exception ignored) {
                }
            } else {
                // There is a password and we have it
                dialog.setTitle("Talio: Lock Board");
                dialog.setHeaderText("Would you like to change the password of this board?\n"
                        + "Leaving this empty would remove the password from the board.");
                pwd.textProperty().addListener((obs, oldVal, newVal) -> {
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(
                            !newVal.equals(pwdConfirm.getText()));
                });
                pwdConfirm.textProperty().addListener((obs, oldVal, newVal) -> {
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(
                            !newVal.equals(pwd.getText()));
                });
                Board board = server.getBoard(boardId);
                pwd.setOnAction(e -> {
                    if (!dialog.getDialogPane().lookupButton(ButtonType.OK).isDisable()) {
                        board.setReadOnlyCode(pwd.getText());
                        server.updateBoard(board);
                        mainCtrlTalio.savePasswordForBoard(boardId, board.getReadOnlyCode());
                        dialog.close();
                    }
                });
                pwdConfirm.setOnAction(e -> {
                    if (!dialog.getDialogPane().lookupButton(ButtonType.OK).isDisable()) {
                        board.setReadOnlyCode(pwd.getText());
                        server.updateBoard(board);
                        mainCtrlTalio.savePasswordForBoard(boardId, board.getReadOnlyCode());
                        dialog.close();
                    }
                });
                try {
                    board.setReadOnlyCode(dialog.showAndWait().get());
                    server.updateBoard(board);
                    mainCtrlTalio.savePasswordForBoard(boardId, board.getReadOnlyCode());
                } catch (Exception ignored) {
                }
            }
        } else {
            // There is a password, but we don't have it
            dialog.setTitle("Talio: Unlock Board");
            dialog.setHeaderText("This board is password-protected.\nPlease provide a password to "
                    + "unlock "
                    + "it.");
            dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            pwd.textProperty().addListener((obs, oldVal, newVal) -> {
                dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(newVal.equals(""));
            });
            pwd.setOnAction(e -> {
                if (!dialog.getDialogPane().lookupButton(ButtonType.OK).isDisable()) {
                    if (server.getBoard(boardId).getReadOnlyCode().equals(pwd.getText())) {
                        mainCtrlTalio.savePasswordForBoard(boardId, pwd.getText());
                        refresh();
                    } else {
                        Alert box = new Alert(Alert.AlertType.WARNING);
                        box.setTitle("Talio: Wrong Password");
                        box.setContentText("You have entered a wrong password");
                        box.setOnCloseRequest(event -> {
                            box.close();
                            Platform.runLater(this::lock);
                        });
                        box.showAndWait();
                    }
                    dialog.close();
                }
            });
            try {
                String res = dialog.showAndWait().get();
                if (server.getBoard(boardId).getReadOnlyCode().equals(res)) {
                    mainCtrlTalio.savePasswordForBoard(boardId, res);
                    refresh();
                } else {
                    Alert box = new Alert(Alert.AlertType.WARNING);
                    box.setTitle("Talio: Wrong Password");
                    box.setContentText("You have entered a wrong password");
                    box.setOnCloseRequest(e -> {
                        box.close();
                        Platform.runLater(this::lock);
                    });
                    box.showAndWait();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void close() {
        innerHBox.getChildren().forEach(c -> ((ListComponentCtrl) c).close());
        server.removeUpdateEvent(updateBoard);
        server.removeUpdateEvent(updateList);
    }

    public void pressedLeft() {
        position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && position[0] > 0
                && listComponentCtrls.get(position[0] - 1).getCards().getChildren().size() > 0) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1])).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1])).removeHighlightShortcut();
            ((CardComponentCtrl) listComponentCtrls.get(position[0] - 1).getCards().getChildren()
                    .get(0)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(position[0] - 1).getCards().getChildren()
                    .get(0)).highlightShortcut();
            operation = "";
        }
    }

    public void pressedRight() {
        position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && position[0] < listComponentCtrls.size() - 1
                && listComponentCtrls.get(position[0] + 1).getCards().getChildren().size() > 0) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1]))
                    .setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1]))
                    .removeHighlightShortcut();
            ((CardComponentCtrl) listComponentCtrls.get(position[0] + 1).getCards().getChildren()
                    .get(0))
                    .setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(position[0] + 1).getCards().getChildren()
                    .get(0))
                    .highlightShortcut();
            operation = "";
        }
    }

    public void pressedDown() {
        position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && !listComponentCtrls.isEmpty() && position[1]
                < listComponentCtrls.get(position[0]).getCards().getChildren().size() - 1) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1])).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1])).removeHighlightShortcut();
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1] + 1)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1] + 1)).highlightShortcut();
            operation = "";
        }
    }

    public void pressedUp() {
        position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && position[1] > 0) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1])).setSelected(false);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1])).removeHighlightShortcut();
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1] - 1)).setSelected(true);
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1] - 1)).highlightShortcut();
            operation = "";
        }
    }

    public void pressedEnter() {
        System.out.println("popup");
        position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && listComponentCtrls.size() > position[0]
                && listComponentCtrls.get(position[0]).getCards().getChildren().size()
                > position[1]) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1])).loadPopup();
            operation = "";
        }
    }

    public void pressedShiftC() {
        position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        listComponentCtrls.get(position[0]).addCard();
        operation = "";
    }

    public void pressedShiftL() {
        this.addCardList();
        operation = "";
    }

    public void pressedE() {
        position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        CardComponentCtrl cardComponentCtrl;

        if (position[0] != -1 && listComponentCtrls.size() > position[0]
                && listComponentCtrls.get(position[0]).getCards().getChildren().size()
                > position[1]) {
            cardComponentCtrl =
                    (CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                            .get(position[1]);

            cardComponentCtrl.titleField.getChildren().clear();
            cardComponentCtrl.titleField.getChildren()
                    .add(new TextField(server.getCard(cardComponentCtrl.getCardId()).getTitle()));
            cardComponentCtrl.titleField.getChildren().get(0).requestFocus();

            CardComponentCtrl finalCardComponentCtrl = cardComponentCtrl;
            cardComponentCtrl.titleField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    Card card = server.getCard(finalCardComponentCtrl.getCardId());
                    card.setTitle(((TextField) finalCardComponentCtrl.titleField.getChildren()
                            .get(0)).getText());
                    server.updateCard(card);
                    cardComponentCtrl.requestFocus();
                }
            });
        }
        operation = "";
    }

    public void pressedDelete() {
        position = this.findPositionOfCard();

        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();

        if (position[0] != -1 && listComponentCtrls.size() > position[0]
                && listComponentCtrls.get(position[0]).getCards().getChildren().size()
                > position[1]) {
            server.deleteCard(((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards()
                    .getChildren().get(position[1])).getCardId());
        }
        operation = "d";
    }

    public void pressedShiftUp() {
        position = this.findPositionOfCard();

        if (position[0] != -1 && position[1] > 0) {
            List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();
            if (listComponentCtrls.size() > position[0]
                    && listComponentCtrls.get(position[0]).getCards().getChildren().size()
                    > position[1]) {
                ListComponentCtrl listComponentCtrl = listComponentCtrls.get(position[0]);
                List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();
                for (int n = 0; n < listComponentCtrl.getCards().getChildren().size(); n++) {
                    cardComponentCtrls.add(
                            (CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(n));
                }
                server.moveCardToListAfterCard(cardComponentCtrls.get(position[1] - 1).getCardId(),
                        listComponentCtrls.get(position[0]).getListId(),
                        cardComponentCtrls.get(position[1]).getCardId());
            }
            operation = "su";
        }

    }

    public void pressedShiftDown() {
        position = this.findPositionOfCard();
        if (position[0] != -1) {
            List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();
            ListComponentCtrl listComponentCtrl = listComponentCtrls.get(position[0]);
            List<CardComponentCtrl> cardComponentCtrls = new ArrayList<>();

            for (int n = 0; n < listComponentCtrl.getCards().getChildren().size(); n++) {
                cardComponentCtrls.add(
                        (CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(n));
            }

            if (position[1] < listComponentCtrl.getCards().getChildren().size() - 1
                    && listComponentCtrls.size() > position[0]
                    && listComponentCtrls.get(position[0]).getCards().getChildren().size() - 1
                    > position[1]) {
                server.moveCardToListAfterCard(cardComponentCtrls.get(position[1]).getCardId(),
                        listComponentCtrls.get(position[0]).getListId(),
                        cardComponentCtrls.get(position[1] + 1).getCardId());
            }
            operation = "sd";
        }
    }

    // TODO put the focus on the tag creation section
    public void pressedT() {
        position = this.findPositionOfCard();
        List<ListComponentCtrl> listComponentCtrls = this.getCardListsFromBoard();
        if (position[0] != -1 && listComponentCtrls.size() > position[0]
                && listComponentCtrls.get(position[0]).getCards().getChildren().size()
                > position[1]) {
            ((CardComponentCtrl) listComponentCtrls.get(position[0]).getCards().getChildren()
                    .get(position[1])).loadPopup();
        }
        operation = "";
    }

    public void pressedC() {
        position = this.findPositionOfCard();
        if (position[0] != -1) {
            this.mainCtrlTalio.showBoardSettings(server.getBoard(this.boardId));
        }
        operation = "";
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
                    cardComponentCtrl1.removeHighlightShortcut();
                }
            }

            cardComponentCtrl.setSelected(true);
            cardComponentCtrl.highlightShortcut();
        }
    }

    public boolean checkTextField(CardComponentCtrl cardComponentCtrl) {
        VBox vBox = (VBox) cardComponentCtrl.getChildren().get(0);
        HBox hBox = (HBox) vBox.getChildren().get(0);
        TitleField titleField = (TitleField) hBox.getChildren().get(0);
        if (titleField.getChildren().get(0).isFocused()) {
            return false;
        }
        return true;
    }

    public int[] findPositionOfCard() {
        int[] position = new int[2];
        boolean run = true;
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl =
                    (ListComponentCtrl) innerHBox.getChildren().get(n);
            if (listComponentCtrl.titleField.getChildren().get(0).isFocused()) {
                run = false;
            }
            listComponentCtrls.add(listComponentCtrl);
            for (int k = 0; k < listComponentCtrl.getCards().getChildren().size(); k++) {
                CardComponentCtrl cardComponentCtrl =
                        ((CardComponentCtrl) listComponentCtrl.getCards().getChildren().get(k));
                if (run) {
                    run = this.checkTextField(cardComponentCtrl);
                }
                if (((CardComponentCtrl) listComponentCtrl.getCards().getChildren()
                        .get(k)).getSelected()) {
                    position = new int[] {n, k};
                }
            }
        }
        if (run) {
            return position;
        } else {
            return new int[] {-1, -1};
        }
    }

    public List<ListComponentCtrl> getCardListsFromBoard() {
        List<ListComponentCtrl> listComponentCtrls = new ArrayList<>();
        for (int n = 0; n < this.innerHBox.getChildren().size(); n++) {
            ListComponentCtrl listComponentCtrl =
                    (ListComponentCtrl) innerHBox.getChildren().get(n);
            listComponentCtrls.add(listComponentCtrl);
        }
        return listComponentCtrls;
    }

    public HBox getInnerHBox() {
        return innerHBox;
    }

    public void setInnerHBox(HBox hBox) {
        this.innerHBox = hBox;
    }

    public void color(ListComponentCtrl listComponentCtrl) {
        switch (operation) {
            case "d":
                if (position[1] > 0) {
                    ((CardComponentCtrl) listComponentCtrl
                            .getCards().getChildren().get(position[1] - 1)).setSelected(true);
                    ((CardComponentCtrl) listComponentCtrl
                            .getCards().getChildren().get(position[1] - 1)).highlightShortcut();
                }
                break;
            case "su":
                if (position[1] > 0) {
                    ((CardComponentCtrl) listComponentCtrl.getCards().getChildren()
                            .get(position[1] - 1)).setSelected(true);
                    ((CardComponentCtrl) listComponentCtrl.getCards().getChildren()
                            .get(position[1] - 1)).highlightShortcut();
                }
                break;
            case "sd":
                if (position[1] < listComponentCtrl.getCards().getChildren().size() - 1) {
                    ((CardComponentCtrl) listComponentCtrl.getCards().getChildren()
                            .get(position[1] + 1)).setSelected(true);
                    ((CardComponentCtrl) listComponentCtrl.getCards().getChildren()
                            .get(position[1] + 1)).highlightShortcut();
                }
                break;
            default:
        }
    }

    public int[] getPosition() {
        return position;
    }
}
