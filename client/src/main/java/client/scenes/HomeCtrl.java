package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

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
    @FXML
    private Label labelBoards;
    private boolean nestedButtonPressed = false;
    private boolean adminMode;

    public boolean isAdmin() {
        return adminMode;
    }

    @Inject
    public HomeCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
        adminMode = false;

        server.registerForBoardDeletion(board -> {
            Platform.runLater(() -> {
                server.unsubscribeIfDeleted(board.getId());

                mainCtrlTalio.readFromLocalData();
                Map<String, Set<Pair<Long, String>>> joinedBoards = mainCtrlTalio.getJoinedBoards();

                if (joinedBoards.get(server.getServerUrl()) != null) {
                    joinedBoards.get(server.getServerUrl()).removeIf(b -> b.getKey().equals(board.getId()));
                }

                mainCtrlTalio.writeToLocalDataRefresh(joinedBoards);
                refreshBoards();
            });
        });
    }

    /**
     * the method called when the scene is displayed
     * the method displays all the boards that the user has connected to
     * if there are no boards: display 'no boards' message
     */
    public void displayBoardLabels(Set<Board> boards) {
        //removes all children from the FlowPane and then
        recentBoardsPane.getChildren().clear();

        if (boards == null) {
            boards = new HashSet<>();
        }
        if (boards.isEmpty()) {
            String noBoards = adminMode ? "No boards in server" : "No recent boards";
            Label noBoardsLabel = new Label(noBoards);
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

                String boardBtnStyle = "-fx-pref-width: 300; -fx-pref-height: 50";
                if (adminMode) {
                    boardBtnStyle += "; -fx-border-color: yellow";
                    Image img = new Image(getClass().getResource("../images/bin.png").toString());
                    ImageView imgView = new ImageView(img);
                    imgView.setFitWidth(15);
                    imgView.setFitHeight(15);
                    imgView.setPickOnBounds(true);
                    imgView.setPreserveRatio(true);
                    imgView.setSmooth(true);
                    deleteBoardBtn.setText("");
                    deleteBoardBtn.setGraphic(imgView);
                } else {
                    deleteBoardBtn.setText("x");
                }
                boardButton.setStyle(boardBtnStyle);

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
                    handleBoardDelete(item);
                });
                boardSettingBtn.setOnAction(e -> {
                    nestedButtonPressed = true;
                    openBoardSetting(item);
                });

                if (!adminMode) {
                    boardSettingBtn.setManaged(false);
                    boardSettingBtn.setVisible(false);
                }

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

    private void handleBoardDelete(Board item) {
        String text = adminMode ? "Delete this board permanently?" : "Disconnect from this board?";
        Alert confirmationDialogue =
                new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.YES, ButtonType.NO);
        confirmationDialogue.showAndWait();

        if (confirmationDialogue.getResult() == ButtonType.YES) {
            //remove board from hashset and call displayBoardLabels method again
            mainCtrlTalio.removeJoinedBoard(server.getServerUrl(), item.getId());

            if (adminMode) {
                //Delete board permanently from server
                server.deleteBoard(item.getId());
                displayBoardLabels(server.getAllBoards());

            } else {
                displayBoardLabels(getRecentBoards());
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
        Set<Board> set = new HashSet<>();
        for (Pair<Long, String> pair :
                mainCtrlTalio.getJoinedBoards().getOrDefault(
                        server.getServerUrl(), new HashSet<Pair<Long, String>>())
        ) {
            try {
                Board board = server.getBoard(pair.getKey());
                set.add(board);
            } catch (Exception e) {

            }
        }
        return set;
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

    //Sets it back to the original state
    private void removeStyleAdmin() {
        buttonAdmin.setText("Enable Admin Mode");
        buttonAdmin.setStyle("-fx-background-color: #bababa");
        labelBoards.setText("Your Boards:");
    }

    //Adds some styling to indicate admin mode
    private void addStyleAdmin() {
        buttonAdmin.setText("Disable Admin Mode");
        buttonAdmin.setStyle("-fx-background-color: yellow");
        labelBoards.setText("All Server Boards:");
    }

    public void refreshBoards() {
        Set<Board> boards = adminMode ? server.getAllBoards() : getRecentBoards();
        displayBoardLabels(boards);
    }

    public ServerUtils getServer() {
        return server;
    }

    public MainCtrlTalio getMainCtrlTalio() {
        return mainCtrlTalio;
    }

    public void stop() {
        server.stop();
    }
}
