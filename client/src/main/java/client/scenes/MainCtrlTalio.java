package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
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
    private TagManagementCtrl tagManagementCtrl;
    private AdminAuthenticationCtrl adminAuthenticationCtrl;
    private ServerUtils server;

    public void setJoinedBoards(Map<String, Set<Long>> joinedBoards) {
        this.joinedBoards = joinedBoards;
    }

    public Map<String, Set<Long>> getJoinedBoards() {
        return joinedBoards;
    }

    private Map<String, Set<Long>> joinedBoards;
    private Stage adminAuthenticationStage;

    @Inject
    public MainCtrlTalio(ServerUtils server) {
        this.server = server;
    }

    public void initialize(
            Stage primaryStageTalio,
            Pair<HomeCtrl, Parent> homePair,
            Pair<JoinBoardCtrl, Parent> joinBoardPair,
            Pair<CreateBoardCtrl, Parent> createBoardPair,
            Pair<ServerConnectionCtrl, Parent> serverConnectionPair,
            Pair<BoardCtrl, Parent> boardComponentPair,
            Pair<ShareBoardCtrl, Parent> shareBoardPair,
            Pair<TagManagementCtrl, Parent> tagManagementPair,
            Pair<AdminAuthenticationCtrl, Parent> adminPair,
            ServerUtils server
    ) {

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

        this.tagManagementCtrl = tagManagementPair.getKey();
        this.tagManagement = new Scene(tagManagementPair.getValue());

        this.adminAuthenticationCtrl = adminPair.getKey();
        this.adminAuthentication = new Scene(adminPair.getValue());

        this.server = server;

        this.showServerConnection();

        primaryStageTalio.show();

    }

    public Set<Long> getJoinedBoardsForServer(String serverUrl) {
        var boardsToRemove = new ArrayList<Long>();
        if (!joinedBoards.containsKey(serverUrl)) {
            return new HashSet<>();
        }
        for (Long boardId : joinedBoards.get(serverUrl)) {
            try {
                server.getBoard(boardId);
            } catch (Exception ignored) {
                boardsToRemove.add(boardId);
            }
        }
        boardsToRemove.forEach(joinedBoards.get(serverUrl)::remove);
        if (!boardsToRemove.isEmpty()) {
            writeToLocalData();
        }
        return joinedBoards.get(serverUrl);
    }

    public void addJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.containsKey(serverUrl)) {
            joinedBoards.put(serverUrl, new HashSet<Long>());
        }

        joinedBoards.get(serverUrl).add(boardId);

        writeToLocalData();
    }

    public void removeJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.containsKey(serverUrl)) {
            return;
        }

        if (!joinedBoards.get(serverUrl).contains(boardId)) {
            return;
        }

        joinedBoards.get(serverUrl).remove(boardId);
        writeToLocalData();
    }

    @SuppressWarnings("unchecked")
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
        } catch (Exception ignored) {
        }
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
        Stage stage = new Stage();
        var boardSettingsCtrl = new BoardSettingsCtrl(this, server, stage, board);
        stage.setTitle("Talio: Board Settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(boardSettingsCtrl));
        stage.show();

        stage.setOnCloseRequest(event -> {
            if (boardSettingsCtrl.isHasUnsavedChanges()) {

                event.consume();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText(
                        "Any unsaved changes will be lost. Do you want to discard them?");
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
        System.out.println("SHOWING BOARD");
        try {
            server.subscribeToBoard(board.getId());
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
