package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import java.util.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    }

    public void showJoinBoardCode() {
        primaryStageTalio.setTitle("Talio: Join an Existing Board");
        primaryStageTalio.setScene(joinBoard);
        joinBoardCodeCtrl.refreshFieldBoardCode();
    }

    public void showCreateBoard() {
        primaryStageTalio.setTitle("Talio: Create a New Board");
        primaryStageTalio.setScene(createBoard);
        createBoardCtrl.refreshFieldBoardName();
    }

    public void showServerConnection() {
        primaryStageTalio.setTitle("Talio: Connect to a Server");
        primaryStageTalio.setScene(serverConnection);
        serverConnectionCtrl.refreshServerAddress();
    }

    //TODO: this method needs to be finished after someone does the settings
    public void showBoardSettings(Board board) {
        boardSettingsCtrl.initialize(board);
        Stage stage = new Stage();
        stage.setTitle("Talio: Board Settings");
        stage.setScene(boardSettings);
        stage.show();
    }

    //TODO: this method needs to be finished after someone does the shareboard popup
    public void showShareBoard(Board board) {
        shareBoardCtrl.initialize(board);
        Stage stage = new Stage();
        stage.setTitle("Talio: Share a board");
        stage.setScene(shareBoard);
        stage.show();
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
    }
}
