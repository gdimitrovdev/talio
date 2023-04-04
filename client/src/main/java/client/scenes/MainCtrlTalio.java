package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import java.util.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import javax.inject.Inject;

public class MainCtrlTalio {
    private Stage primaryStageTalio;
    private Scene home, joinBoard, createBoard, serverConnection, boardComponent, shareBoard, boardSettings;
    private HomeCtrl homeCtrl;
    private JoinBoardCtrl joinBoardCodeCtrl;
    private CreateBoardCtrl createBoardCtrl;
    private ServerConnectionCtrl serverConnectionCtrl;
    private BoardCtrl boardComponentCtrl;
    private ShareBoardCtrl shareBoardCtrl;
    private BoardSettingsCtrl boardSettingsCtrl;
    private ServerUtils serverUtils;
    private Map<String, Set<Long>> joinedBoards = new HashMap<>();

    @Inject
    public MainCtrlTalio(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    public void initialize(
            Stage primaryStageTalio,
            Pair<HomeCtrl, Parent> homePair,
            Pair<JoinBoardCtrl, Parent> joinBoardPair,
            Pair<CreateBoardCtrl, Parent> createBoardPair,
            Pair<ServerConnectionCtrl, Parent> serverConnectionPair,
            Pair<BoardCtrl, Parent> boardComponentPair,
            Pair<ShareBoardCtrl, Parent> shareBoardPair,
            Pair<BoardSettingsCtrl, Parent> boardSettingsPair) {
        this.primaryStageTalio = primaryStageTalio;

        this.homeCtrl = homePair.getKey();
        this.home = new Scene(homePair.getValue());

        this.joinBoardCodeCtrl = joinBoardPair.getKey();
        this.joinBoard = new Scene(joinBoardPair.getValue());

        this.createBoardCtrl = createBoardPair.getKey();
        this.createBoard = new Scene(createBoardPair.getValue());

        this.serverConnectionCtrl = serverConnectionPair.getKey();
        this.serverConnection = new Scene(serverConnectionPair.getValue());

        this.boardComponentCtrl = boardComponentPair.getKey();
        this.boardComponent = new Scene(boardComponentPair.getValue());

        this.shareBoardCtrl = shareBoardPair.getKey();
        this.shareBoard = new Scene(shareBoardPair.getValue());

        this.boardSettingsCtrl = boardSettingsPair.getKey();
        this.boardSettings = new Scene(boardSettingsPair.getValue());

        // showHome();
        this.showServerConnection();

        primaryStageTalio.show();
    }

    public Set<Long> getJoinedBoardForServer(String serverUrl) {
        return joinedBoards.get(serverUrl);
    }

    public void addJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.keySet().contains(serverUrl)) {
            joinedBoards.put(serverUrl, new HashSet<Long>());
        }

        if (!joinedBoards.get(serverUrl).contains(boardId)) {
            joinedBoards.get(serverUrl).add(boardId);
        }
    }

    public void removeJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.keySet().contains(serverUrl)) {
            return;
        }

        if (!joinedBoards.get(serverUrl).contains(boardId)) {
            return;
        }

        joinedBoards.get(serverUrl).remove(boardId);
    }

    public void showHome() {
        primaryStageTalio.setTitle("Talio: Overview");
        primaryStageTalio.setScene(home);
        homeCtrl.displayBoardLabels();
        this.listenForQuestionMarkPressed();
    }

    public void showJoinBoardCode() {
        primaryStageTalio.setTitle("Talio: Join an Existing Board");
        primaryStageTalio.setScene(joinBoard);
        this.listenForQuestionMarkPressed();
    }

    public void showCreateBoard() {
        primaryStageTalio.setTitle("Talio: Create a New Board");
        primaryStageTalio.setScene(createBoard);
        this.listenForQuestionMarkPressed();
    }

    public void showServerConnection() {
        primaryStageTalio.setTitle("Talio: Connect to a Server");
        primaryStageTalio.setScene(serverConnection);
        serverConnectionCtrl.refreshServerAddress();
        this.listenForQuestionMarkPressed();
    }

    //TODO: this method needs to be finished after someone does the settings
    public void showBoardSettings(Board board) {
        boardSettingsCtrl.initialize(board);
        Stage stage = new Stage();
        stage.setTitle("Talio: Board Settings");
        stage.setScene(boardSettings);
        stage.show();
        this.listenForQuestionMarkPressed();
    }

    //TODO: this method needs to be finished after someone does the shareboard popup
    public void showShareBoard(Board board) {
        shareBoardCtrl.initialize(board);
        Stage stage = new Stage();
        stage.setTitle("Talio: Share a board");
        stage.setScene(shareBoard);
        stage.show();
        this.listenForQuestionMarkPressed();
    }

    public void showBoard(Board board) {
        try {
            serverUtils.subscribeToBoard(board.getId());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        boardComponentCtrl.initialize(board.getId());
        primaryStageTalio.setTitle("Talio: Board");
        primaryStageTalio.setScene(boardComponent);
        this.listenForQuestionMarkPressed();
        this.listenForKeyPressedForBoard();
        this.listenToMouse();
    }

    public void listenForQuestionMarkPressed() {
        this.primaryStageTalio.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SLASH && e.isShiftDown()) {
                Stage stage = new Stage();
                stage.setTitle("Talio: Help");
                TilePane tilepane = new TilePane();
                Text text = new Text(" The available key shortcuts are: \n"
                        + " Press ? anywhere in the application to open/close this help screen \n \n"
                        + " The following shortcuts can be used on a task which is highlighted: "
                        + " - Task highlight can be moved with arrow keys (Up/Down/Left/Right) \n"
                        + " - Task can be moved up/down within a list by pressing Shift+Up/Down \n"
                        + " - Task name can be edited in the board overview by pressing E \n"
                        + " - Task can be deleted from the board overview by fressing delete or backspace \n"
                        + " - Task editing pop-up can be opened by pressing enter \n"
                        + " - Task editing pop-up can be closed by pressing esc");
                tilepane.getChildren().add(text);
                Scene scene = new Scene(tilepane, 730, 150);
                stage.setScene(scene);
                stage.show();
                scene.setOnKeyPressed(e1 -> {
                    if (e1.getCode() == KeyCode.SLASH && e1.isShiftDown()) {
                        stage.close();
                    }
                });
            }
        });
    }

    public void listenForKeyPressedForBoard() {
        this.boardComponent.getRoot().addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            switch (e.getCode()) {
                case RIGHT -> {
                    System.out.println("Move right");
                    boardComponentCtrl.switchRight();
                }
                case LEFT -> {
                    System.out.println("Move left");
                    boardComponentCtrl.switchLeft();
                }
                case UP -> {
                    if (e.isShiftDown()) {
                        System.out.println("Move card up");
                        boardComponentCtrl.pressedShiftUp();
                    } else {
                        System.out.println("Move up");
                        boardComponentCtrl.moveUp();
                    }
                }
                case DOWN -> {
                    if (e.isShiftDown()) {
                        System.out.println("Move card down");
                        boardComponentCtrl.pressedShiftDown();
                    } else {
                        System.out.println("Move down");
                        boardComponentCtrl.moveDown();
                    }
                }
                case ENTER -> {
                    System.out.println("Open popup");
                    boardComponentCtrl.pressedEnter();
                }
                case E -> {
                    System.out.println("Edit card title");
                    boardComponentCtrl.pressedE();
                }
                case BACK_SPACE, DELETE -> {
                    System.out.println("Delete the card");
                    boardComponentCtrl.pressedDelete();
                }
                case T -> {
                    System.out.println("Open popup for adding tags");
                    boardComponentCtrl.pressedT();
                }
                case C -> {
                    System.out.println("Open popup for color preset selection");
                    boardComponentCtrl.pressedC();
                }
            }
        });
    }

    public void listenToMouse() {
        this.boardComponent.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            boardComponentCtrl.mouseMovement(e);
        });
    }
}
