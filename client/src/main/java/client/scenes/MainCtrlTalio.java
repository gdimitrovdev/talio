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
    private Scene board;
    private JoinBoardCtrl joinBoardCtrl;
    private final ServerUtils server ;

    @Inject
    public MainCtrlTalio(ServerUtils server) {
        this.server = server;
    }

    public void initialize(
        Stage primaryStageTalio,
        Pair<JoinBoardCtrl, Parent> boardPair)
    {
        this.primaryStageTalio=primaryStageTalio;
        this.joinBoardCtrl =boardPair.getKey();
        this.board=new Scene(boardPair.getValue());
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
