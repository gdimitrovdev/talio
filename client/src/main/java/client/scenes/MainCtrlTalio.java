package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.inject.Inject;

public class MainCtrlTalio {
    private Stage primaryStageTalio;
    private Scene home, joinBoard, createBoard, serverConnection, boardComponent, shareBoard,
            boardSettings, tagManagement, adminAuthentication;
    private HomeCtrl homeCtrl;
    private JoinBoardCtrl joinBoardCodeCtrl;
    private CreateBoardCtrl createBoardCtrl;
    private ServerConnectionCtrl serverConnectionCtrl;
    private BoardCtrl boardComponentCtrl;
    private ShareBoardCtrl shareBoardCtrl;
    private BoardSettingsCtrl boardSettingsCtrl;
    private TagManagementCtrl tagManagementCtrl;
    private AdminAuthenticationCtrl adminAuthenticationCtrl;
    private ServerUtils serverUtils;

    public void setJoinedBoards(Map<String, Set<Long>> joinedBoards) {
        this.joinedBoards = joinedBoards;
    }

    public Map<String, Set<Long>> getJoinedBoards() {
        return joinedBoards;
    }

    private Map<String, Set<Long>> joinedBoards;
    private Stage adminAuthenticationStage;

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
            Pair<TagManagementCtrl, Parent> tagManagementPair,
            Pair<AdminAuthenticationCtrl, Parent> adminPair) {

        readFromLocalData();

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

        this.adminAuthenticationCtrl = adminPair.getKey();
        this.adminAuthentication = new Scene(adminPair.getValue());

        // showHome();
        this.showServerConnection();

        primaryStageTalio.show();

    }

    public Set<Long> getJoinedBoardForServer(String serverUrl) {
        return joinedBoards.getOrDefault(serverUrl, new HashSet<Long>());
    }

    public void addJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.keySet().contains(serverUrl)) {
            joinedBoards.put(serverUrl, new HashSet<Long>());
        }

        if (!joinedBoards.get(serverUrl).contains(boardId)) {
            joinedBoards.get(serverUrl).add(boardId);
        }

        writeToLocalData();
    }

    public void removeJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.keySet().contains(serverUrl)) {
            return;
        }

        if (!joinedBoards.get(serverUrl).contains(boardId)) {
            return;
        }

        joinedBoards.get(serverUrl).remove(boardId);
        writeToLocalData();
    }

    public void readFromLocalData() {
        File toRead = new File(".local_data");

        try (
                FileInputStream fis = new FileInputStream(toRead);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ) {
            this.joinedBoards = (HashMap<String, Set<Long>>) ois.readObject();
        } catch (Exception e) {
            this.joinedBoards = new HashMap<String, Set<Long>>();
        }
    }

    public void writeToLocalData() {
        File localData = new File(".local_data");

        try (
                FileOutputStream fos = new FileOutputStream(localData);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                ) {
            oos.writeObject(joinedBoards);
            oos.flush();
        } catch (Exception e) { }
    }

    public void alert(String title, String message) {
        Alert box = new Alert(Alert.AlertType.WARNING);
        box.setTitle(title);
        box.setContentText(message);
        box.showAndWait();
    }

    public void showHome() {
        primaryStageTalio.setTitle("Talio: Overview");
        primaryStageTalio.setScene(home);
        homeCtrl.refreshBoards();
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

    public void showBoardSettings(Board board) {
        boardSettingsCtrl.initialize(board);
        Stage stage = new Stage();
        stage.setTitle("Talio: Board Settings");
        stage.setScene(boardSettings);
        stage.show();

        stage.setOnCloseRequest(event -> {
            if (boardSettingsCtrl.isHasUnsavedChanges() == true) {

                event.consume();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Any unsaved changes will be lost. Do you want to discard them?");
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

                alert.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.YES) {
                        stage.close();
                    }
                });

            }

        });


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

    public void showTagManagement(Board board) {
        tagManagementCtrl.initialize(board);
        Stage stage = new Stage();
        stage.setTitle("Talio: Manage Your Tags");
        stage.setScene(tagManagement);
        stage.show();

    }

    public void showAdminAuthentication() {
        adminAuthenticationCtrl.initialize();
        adminAuthenticationStage = new Stage();
        adminAuthenticationStage.setTitle("Talio: Admin Authentication");
        adminAuthenticationStage.setScene(adminAuthentication);
        adminAuthenticationStage.show();
    }

    public void enableAdminMode() {
        if (adminAuthenticationStage != null) {
            adminAuthenticationStage.close();
        }
        homeCtrl.enableAdminMode();
    }
}
