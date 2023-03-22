package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrlTalio {

    //initialize the stage
    private Stage primaryStageTalio;
    private Scene board, joinBoard, createBoard, serverConnection;
    private JoinBoardCtrl joinBoardCtrl;
    private JoinBoardCodeCtrl joinBoardCodeCtrl;
    private CreateBoardCtrl createBoardCtrl;

    private ServerConnectionCtrl serverConnectionCtrl;

    private final ServerUtils server ;

    @Inject
    public MainCtrlTalio(ServerUtils server) {
        this.server = server;
    }

    public void initialize(
        Stage primaryStageTalio,
        Pair<JoinBoardCtrl, Parent> boardPair,
        Pair<JoinBoardCodeCtrl, Parent> joinBoardPair,
        Pair<CreateBoardCtrl, Parent> createBoardPair,
        Pair<ServerConnectionCtrl, Parent> serverConnectionPair)
    {
        this.primaryStageTalio=primaryStageTalio;

        this.joinBoardCtrl =boardPair.getKey();
        this.board=new Scene(boardPair.getValue());
        this.joinBoardCodeCtrl = joinBoardPair.getKey();
        this.joinBoard = new Scene(joinBoardPair.getValue());
        this.createBoardCtrl = createBoardPair.getKey();
        this.createBoard = new Scene(createBoardPair.getValue());
        this.serverConnectionCtrl = serverConnectionPair.getKey();
        this.serverConnection = new Scene(serverConnectionPair.getValue());

        showOverview();
        primaryStageTalio.show();

    }
    public void showOverview(){
        primaryStageTalio.setTitle("Talio: Overview");
        primaryStageTalio.setScene(board);
    }





    //initialize the controller for the main board scene

    //initialize the 'main board' scene

    //initialize other scenes and their controllers

}
