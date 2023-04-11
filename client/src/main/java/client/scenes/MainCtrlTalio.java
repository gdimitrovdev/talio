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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
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

    public BoardCtrl getBoardComponentCtrl() {
        return boardComponentCtrl;
    }

    private BoardCtrl boardComponentCtrl;
    private ShareBoardCtrl shareBoardCtrl;
    private TagManagementCtrl tagManagementCtrl;
    private AdminAuthenticationCtrl adminAuthenticationCtrl;
    private ServerUtils server;

    public void setJoinedBoards(Map<String, Set<Pair<Long, String>>> joinedBoards) {
        this.joinedBoards = joinedBoards;
    }

    public Map<String, Set<Pair<Long, String>>> getJoinedBoards() {
        return joinedBoards;
    }

    private Map<String, Set<Pair<Long, String>>> joinedBoards;
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

        this.tagManagementCtrl = tagManagementPair.getKey();
        this.tagManagement = new Scene(tagManagementPair.getValue());

        this.adminAuthenticationCtrl = adminPair.getKey();
        this.adminAuthentication = new Scene(adminPair.getValue());

        this.showServerConnection();

        primaryStageTalio.show();
    }

    public Set<Long> getJoinedBoardsForServer(String serverUrl) {
        var boardsToRemove = new ArrayList<>();
        if (!joinedBoards.containsKey(serverUrl)) {
            return new HashSet<>();
        }
        for (var board : joinedBoards.get(serverUrl)) {
            Long boardId = board.getKey();
            try {
                server.getBoard(boardId);
            } catch (Exception ignored) {
                boardsToRemove.add(boardId);
            }
        }
        joinedBoards.get(serverUrl).removeIf(b -> boardsToRemove.contains(b.getKey()));
        if (!boardsToRemove.isEmpty()) {
            writeToLocalData();
        }
        return joinedBoards.get(serverUrl).stream().map(Pair::getKey).collect(Collectors.toSet());
    }

    public boolean hasAuthenticationForBoard(Long boardId) {
        if (homeCtrl.isAdmin()) {
            return true;
        }
        var b = joinedBoards.get(server.getServerUrl()).stream()
                .filter(p -> p.getKey().equals(boardId)).findFirst();
        String pwd = server.getBoard(boardId).getReadOnlyCode();
        if (pwd.equals("")) {
            return true;
        }
        return b.isPresent() && b.get().getValue().equals(pwd);
    }

    public void savePasswordForBoard(Long boardId, String newPassword) {
        Optional<Pair<Long, String>> b = joinedBoards.get(server.getServerUrl()).stream()
                .filter(p -> p.getKey().equals(boardId)).findFirst();
        if (b.isPresent()) {
            joinedBoards.get(server.getServerUrl()).remove(b.get());
            joinedBoards.get(server.getServerUrl()).add(new Pair<>(boardId, newPassword));
            writeToLocalData();
        }
    }

    public void addJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.containsKey(serverUrl)) {
            joinedBoards.put(serverUrl, new HashSet<>());
        }

        joinedBoards.get(serverUrl).add(new Pair<>(boardId, ""));

        writeToLocalData();
    }

    public void removeJoinedBoard(String serverUrl, Long boardId) {
        if (!joinedBoards.containsKey(serverUrl)) {
            return;
        }

        if (joinedBoards.get(serverUrl).stream().noneMatch(p -> p.getKey().equals(boardId))) {
            return;
        }

        joinedBoards.get(serverUrl).removeIf(p -> p.getKey().equals(boardId));
        writeToLocalData();
    }

    @SuppressWarnings("unchecked")
    public void readFromLocalData() {
        File toRead = new File(".local_data");

        try (
                FileInputStream fis = new FileInputStream(toRead);
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            this.joinedBoards = (HashMap<String, Set<Pair<Long, String>>>) ois.readObject();
            for (var s : joinedBoards.keySet()) {
                for (var b : joinedBoards.get(s)) {
                    if (b == null) {
                        throw new Exception();
                    }
                }
            }

        } catch (Exception e) {
            this.joinedBoards = new HashMap<>();
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
        } catch (Exception e) {
            e.printStackTrace();
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
        this.listenForQuestionMarkPressed();
    }

    public void showJoinBoardCode() {
        primaryStageTalio.setTitle("Talio: Join an Existing Board");
        primaryStageTalio.setScene(joinBoard);
        joinBoardCodeCtrl.refreshFieldBoardCode();
        this.listenForQuestionMarkPressed();
    }

    public void showCreateBoard() {
        primaryStageTalio.setTitle("Talio: Create a New Board");
        primaryStageTalio.setScene(createBoard);
        createBoardCtrl.refreshFieldBoardName();
        this.listenForQuestionMarkPressed();
    }

    public void showServerConnection() {
        primaryStageTalio.setTitle("Talio: Connect to a Server");
        primaryStageTalio.setScene(serverConnection);
        serverConnectionCtrl.refreshServerAddress();
        this.listenForQuestionMarkPressed();
    }

    public void showBoardSettings(Board board) {
        Stage stage = new Stage();
        var boardSettingsCtrl = new BoardSettingsCtrl(this, server, stage, board);
        stage.setTitle("Talio: Board Settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(boardSettingsCtrl));
        stage.show();
        this.listenForQuestionMarkPressed();
        stage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

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
        this.listenForQuestionMarkPressed();
    }

    public void showBoard(Board board) {
        try {
            server.subscribeToBoard(board.getId());
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
            if ((e.getCode() == KeyCode.SLASH && e.isShiftDown())) {
                Stage stage = new Stage();
                stage.setTitle("Talio: Help");
                TilePane tilepane = new TilePane();
                Text text = new Text(" The available key shortcuts are: \n"
                        + " Press ? anywhere in the application to open/close this help screen \n \n"
                        + " The following shortcuts can be used on a task which is highlighted: \n"
                        + " - Task highlight can be moved with arrow keys (Up/Down/Left/Right) \n"
                        + " - Task can be moved up/down within a list by pressing Shift+Up/Down \n"
                        + " - Task name can be edited in the board overview by pressing E \n"
                        + " - Task can be deleted from the board overview by fressing delete or backspace \n"
                        + " - Task editing pop-up can be opened by pressing enter \n"
                        + " - Task editing pop-up can be closed by pressing esc");
                tilepane.getChildren().add(text);
                Scene scene = new Scene(tilepane, 730, 170);
                stage.setScene(scene);
                stage.show();
                scene.setOnKeyPressed(e1 -> {
                    if ((e1.getCode() == KeyCode.SLASH && e1.isShiftDown())
                            || e.getCode() == KeyCode.ESCAPE) {
                        stage.close();
                    }
                });
            }
        });
    }

    public void listenForKeyPressedForBoard() {
        this.boardComponent.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case RIGHT -> {
                    boardComponentCtrl.pressedRight();
                }
                case LEFT -> {
                    boardComponentCtrl.pressedLeft();
                }
                case UP -> {
                    if (e.isShiftDown()) {
                        boardComponentCtrl.pressedShiftUp();
                    } else {
                        boardComponentCtrl.pressedUp();
                    }
                }
                case DOWN -> {
                    if (e.isShiftDown()) {
                        boardComponentCtrl.pressedShiftDown();
                    } else {
                        boardComponentCtrl.pressedDown();
                    }
                }
                case ENTER -> {
                    boardComponentCtrl.pressedEnter();
                }
                case E -> {
                    boardComponentCtrl.pressedE();
                }
                case BACK_SPACE, DELETE -> {
                    boardComponentCtrl.pressedDelete();
                }
                case T -> {
                    boardComponentCtrl.pressedT();
                }
                case C -> {
                    if (e.isShiftDown()) {
                        boardComponentCtrl.pressedShiftC();
                    } else {
                        boardComponentCtrl.pressedC();
                    }
                }
                case L -> {
                    if (e.isShiftDown()) {
                        boardComponentCtrl.pressedShiftL();
                    }
                }
            }
        });
    }

    public void listenToMouse() {
        this.boardComponent.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            boardComponentCtrl.mouseMovement(e);
        });
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
