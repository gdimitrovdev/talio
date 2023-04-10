package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.CardList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class HomeCtrl {

    private final ServerUtils server;
    private final MainCtrlTalio mainCtrlTalio;

    //this set will contain all the boards that have been opened by the 'join a board' and
    //'create a board' method
    //in the 2 methods the created/joined board should be added to the hashset
    @FXML
    Button joinBoardBtn;
    @FXML
    Button createBoardBtn;
    @FXML
    private Button changeServerBtn;
    @FXML
    private AnchorPane root;
    @FXML
    private ScrollPane outerContainer;
    @FXML
    public GridPane recentBoardsPane;
    private boolean nestedButtonPressed = false;

    @Inject
    public HomeCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
        /*for (int i = 0; i < 15; i++) {
            this.generateExampleBoard((long) i);
        }*/
    }

    private void generateExampleBoard(Long id) {
        //"Long boardID = Long.parseLong(boardField.getText());
        //Board current = serverUtils.getBoardById(boardID)" can be used
        // Note: server utils is in MainCtrlTalio now so that we have only one instance of it

        List<String> defaultPresets = new ArrayList<>();
        defaultPresets.add("#ffffff/#000000");
        defaultPresets.add("#ff0008/#000000");
        defaultPresets.add("#abffc3/#004714");
        Board board = new Board("Example board", "pwd", "hash_893290840923904", "#bababa/#000000", "#dedede/#000000", defaultPresets, 1);

        CardList list1 = new CardList("example list 1", null);
        CardList list2 = new CardList("example list 2", null);

        Card c1 = new Card("Title1", "Card1", null, 1);
        Card c2 = new Card("Title2", "Card2", null, 1);

        list1.addCard(c1);
        list1.addCard(c2);

        board.addCardList(list1);
        board.addCardList(list2);


        //adding the board to the hashset, so it will be displayed
        //in 'your boards' on the home scene
        board.setId(id);
    }

    /**
     * the method called when the scene is displayed
     * the method displays all the boards that the user has connected to
     * if there are no boards: display 'no boards' message
     */
    public void displayBoardLabels() {
        Set<Long> recentBoards = mainCtrlTalio.getJoinedBoardForServer(server.getServerUrl());
        if (recentBoards == null) {
            recentBoards = new HashSet<>();
        }
        if (recentBoards.isEmpty()) {
            Label noBoardsLabel = new Label("No recent boards");
            noBoardsLabel.setStyle(
                    "-fx-pref-width: 690; -fx-pref-height: 50; -fx-alignment: center;");
            recentBoardsPane.getChildren().add(noBoardsLabel);

        } else {
            recentBoardsPane.getChildren().clear();
            recentBoardsPane.setHgap(70);
            int i = 0;
            int j = 0;
            for (Long itemId : recentBoards) {
                Board item = server.getBoard(itemId);
                //initializing the button and its content
                Button boardButton = new Button();
                AnchorPane innerPane = new AnchorPane();
                Label boardNameLbl = new Label(item.getName());
                Button boardSettingBtn = new Button("...");
                Button deleteBoardBtn = new Button("x");
                innerPane.getChildren().addAll(boardNameLbl, boardSettingBtn, deleteBoardBtn);
                boardButton.setGraphic(innerPane);

                //formatting the button and its content
                boardSettingBtn.setStyle("-fx-pref-width: 30; -fx-pref-height: 30");
                deleteBoardBtn.setStyle("-fx-pref-width: 30; -fx-pref-height: 30");
                boardButton.setStyle("-fx-pref-width: 300; -fx-pref-height: 50 ");
                innerPane.setStyle("-fx-min-width: 219; -fx-min-height: 50");
                boardNameLbl.setAlignment(Pos.CENTER);

                AnchorPane.setRightAnchor(boardSettingBtn, 10d);
                AnchorPane.setRightAnchor(deleteBoardBtn, 50d);
                AnchorPane.setTopAnchor(boardSettingBtn, 10d);
                AnchorPane.setTopAnchor(deleteBoardBtn, 10d);
                AnchorPane.setTopAnchor(boardNameLbl, 16d);
                AnchorPane.setLeftAnchor(boardNameLbl, 10d);

                nestedButtonPressed = true;
                //setting the action of the buttons for removing and editing
                deleteBoardBtn.setOnAction(e -> {
                    Alert confirmationDialogue = new Alert(Alert.AlertType.CONFIRMATION, "Disconnect from this board?", ButtonType.YES, ButtonType.NO);
                    confirmationDialogue.showAndWait();
                    if (confirmationDialogue.getResult() == ButtonType.YES) {
                        removeRecentBoard(item);
                    }


                });
                boardSettingBtn.setOnAction(e -> {
                    nestedButtonPressed = true;
                    openBoardSetting(item);
                });
                boardButton.setOnAction(e -> {
                    try {
                        displayBoard(item);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                GridPane.setMargin(boardButton, new Insets(10, 10, 10, 10));
                //adding the button to the GridPane
                recentBoardsPane.add(boardButton, i, j);
                ++i;
                if (i >= 2) {
                    i = 0;
                    ++j;
                }
            }
        }
    }

    /**
     * method that is called by the changeServerBtn
     * it uses a method from the Main Controller
     */

    // TODO: FIX THESE THREE METHODS.
    public void openServerScene() {
        mainCtrlTalio.showServerConnection();
    }

    /**
     * used by the createBoardBtn and displays the pop-up for creating a board
     */
    public void displayCreatePopUp() {
        mainCtrlTalio.showCreateBoard();
    }

    /**
     * used by the joinBoardBtn and displays the pop-up for joining a board
     */
    public void displayJoinPopUp() {
        mainCtrlTalio.showJoinBoardCode();
    }

    /**
     * @param board - the board that should be removed
     *              This method is called as the event of the button deleteBoardBtn
     */
    public void removeRecentBoard(Board board) {
        //removes all children from the FlowPane and then
        //remove board from hashset and call displayBoardLabels method again// server.deleteBoard(board.getId());
        mainCtrlTalio.removeJoinedBoard(server.getServerUrl(), board.getId());
        recentBoardsPane.getChildren().clear();
        displayBoardLabels();
    }

    //TODO: integrate pop up once it is ready

    /**
     * @param board - the board for which a pop-up should be opened
     *              this pop-up lets you edit the board's settings
     */
    public void openBoardSetting(Board board) {
        mainCtrlTalio.showBoardSettings(board);
    }

    /**
     * displays the board in the overview
     */
    public void displayBoard(Board board) throws IOException {

        if (nestedButtonPressed) {
            nestedButtonPressed = false;
            return;
        }

        mainCtrlTalio.showBoard(board);


    }

    public ServerUtils getServer() {
        return server;
    }

    public MainCtrlTalio getMainCtrlTalio() {
        return mainCtrlTalio;
    }
}
