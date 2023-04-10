package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
    private GridPane recentBoardsPane;
    @FXML
    private Button buttonAdmin;
    private boolean nestedButtonPressed = false;
    private boolean adminMode;

    @Inject
    public HomeCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
        adminMode = false;
    }

    /**
     * the method called when the scene is displayed
     * the method displays all the boards that the user has connected to
     * if there are no boards: display 'no boards' message
     */
    public void displayBoardLabels(Set<Board> boards) {
        if (boards == null) {
            boards = new HashSet<>();
        }
        if (boards.isEmpty()) {
            Label noBoardsLabel = new Label("No recent boards");
            noBoardsLabel.setStyle(
                    "-fx-pref-width: 690; -fx-pref-height: 50; -fx-alignment: center;");
            recentBoardsPane.getChildren().add(noBoardsLabel);

        } else {
            recentBoardsPane.getChildren().clear();
            recentBoardsPane.setHgap(70);
            int i = 0;
            int j = 0;
            for (Board item : boards) {
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


                //setting the action of the buttons for removing and editing
                deleteBoardBtn.setOnAction(e -> {
                    nestedButtonPressed = true;
                    removeRecentBoard(item);
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
        displayBoardLabels(server.getAllBoards());
    }

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

    public Set<Board> getRecentBoards() {
        return mainCtrlTalio.getJoinedBoardForServer(server.getServerUrl())
                .stream().map(id -> server.getBoard(id))
                .collect(Collectors.toSet());
    }

    @FXML
    protected void adminClick() {
        if (adminMode) {
            adminMode = false;
            displayBoardLabels(getRecentBoards());
            removeStyleAdmin();
        } else {
            mainCtrlTalio.showAdminAuthentication();
        }
    }

    public void enableAdminMode() {
        adminMode = true;
        displayBoardLabels(server.getAllBoards());
        addStyleAdmin();
    }

    private void removeStyleAdmin() {

    }

    private void addStyleAdmin() {

    }
}
