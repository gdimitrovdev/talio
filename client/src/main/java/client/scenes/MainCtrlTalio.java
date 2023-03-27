package client.scenes;

import commons.Board;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class MainCtrlTalio {
    private Stage primaryStageTalio;
    private Scene home, joinBoard, createBoard, serverConnection,boardComponent;
    private HomeCtrl homeCtrl;
    private JoinBoardCtrl joinBoardCodeCtrl;
    private CreateBoardCtrl createBoardCtrl;
    private ServerConnectionCtrl serverConnectionCtrl;
    private BoardCtrl boardComponentCtrl;

    public void initialize(
        Stage primaryStageTalio,
        Pair<HomeCtrl, Parent> homePair,
        Pair<JoinBoardCtrl, Parent> joinBoardPair,
        Pair<CreateBoardCtrl, Parent> createBoardPair,
        Pair<ServerConnectionCtrl, Parent> serverConnectionPair,
        Pair<BoardCtrl, Parent> boardComponentPair)
    {
        this.primaryStageTalio = primaryStageTalio;

        this.homeCtrl = homePair.getKey();
        this.home = new Scene(homePair.getValue());

        this.joinBoardCodeCtrl = joinBoardPair.getKey();
        this.joinBoard = new Scene(joinBoardPair.getValue());

        this.createBoardCtrl = createBoardPair.getKey();
        this.createBoard = new Scene(createBoardPair.getValue());

        this.serverConnectionCtrl = serverConnectionPair.getKey();
        this.serverConnection = new Scene(serverConnectionPair.getValue());

        this.boardComponentCtrl= boardComponentPair.getKey();
        this.boardComponent=new Scene(boardComponentPair.getValue());

        showHome();
        primaryStageTalio.show();

    }
    public void showHome(){
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
    }

    //TODO: this method needs to be finished after someone does the settings
    public void showBoardSettings(){
        primaryStageTalio.setTitle("Talio: Board Settings");
        //primaryStageTalio.setScene(type here);
    }
    //TODO: this method needs to be finished after someone does the shareboard popup
    public void showShareBoard(){
        primaryStageTalio.setTitle("Talio: Share a board");
        //primaryStageTalio.setScene(type here);
    }
    public void showBoard(Board board) throws IOException {
        boardComponentCtrl.initialize(board);
        primaryStageTalio.setTitle("Talio: Board");
        primaryStageTalio.setScene(boardComponent);
    }
}
