package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import java.io.*;
import java.util.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.inject.Inject;

public class MainCtrlTalio {
    private Stage primaryStageTalio;
    private Scene home, joinBoard, createBoard, serverConnection, boardComponent, shareBoard,
            boardSettings, tagManagement;
    private HomeCtrl homeCtrl;
    private JoinBoardCtrl joinBoardCodeCtrl;
    private CreateBoardCtrl createBoardCtrl;
    private ServerConnectionCtrl serverConnectionCtrl;
    private BoardCtrl boardComponentCtrl;
    private ShareBoardCtrl shareBoardCtrl;
    private BoardSettingsCtrl boardSettingsCtrl;
    private TagManagementCtrl tagManagementCtrl;
    private ServerUtils serverUtils;
    private Map<String, Set<Long>> joinedBoards;

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
            Pair<BoardSettingsCtrl, Parent> boardSettingsPair,
            Pair<TagManagementCtrl, Parent> tagManagementPair) {

        try {
            File toRead = new File("local_data");
            FileInputStream fis = new FileInputStream(toRead);
            ObjectInputStream ois = new ObjectInputStream(fis);

            this.joinedBoards = (HashMap<String, Set<Long>>) ois.readObject();

            ois.close();
            fis.close();
        } catch (Exception e) {
            this.joinedBoards = new HashMap<String, Set<Long>>();
        }

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

        this.tagManagementCtrl = tagManagementPair.getKey();
        this.tagManagement = new Scene(tagManagementPair.getValue());

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

        try {
            File localData = new File("local_data");
            FileOutputStream fos = new FileOutputStream(localData);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(joinedBoards);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) { }
    }

    public void removeJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.keySet().contains(serverUrl)) {
            return;
        }

        if (!joinedBoards.get(serverUrl).contains(boardId)) {
            return;
        }

        joinedBoards.get(serverUrl).remove(boardId);
        try {
            File localData = new File("local_data");
            FileOutputStream fos = new FileOutputStream(localData);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(joinedBoards);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) { }
    }

    public void showHome() {
        primaryStageTalio.setTitle("Talio: Overview");
        primaryStageTalio.setScene(home);
        homeCtrl.displayBoardLabels();
    }

    public void showJoinBoardCode() {
        primaryStageTalio.setTitle("Talio: Join an Existing Board");
        primaryStageTalio.setScene(joinBoard);
    }

    public void showCreateBoard() {
        primaryStageTalio.setTitle("Talio: Create a New Board");
        primaryStageTalio.setScene(createBoard);
    }

    public void showServerConnection() {
        primaryStageTalio.setTitle("Talio: Connect to a Server");
        primaryStageTalio.setScene(serverConnection);
        serverConnectionCtrl.refreshServerAddress();
    }

    public void showBoardSettings(Board board) {
        boardSettingsCtrl.initialize(board);
        Stage stage = new Stage();
        stage.setTitle("Talio: Board Settings");
        stage.setScene(boardSettings);
        stage.show();
    }

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

    public void showTagManagement() {
        Stage stage = new Stage();
        stage.setTitle("Talio: Manage Your Tags");
        stage.setScene(tagManagement);
        stage.show();
    }
}
