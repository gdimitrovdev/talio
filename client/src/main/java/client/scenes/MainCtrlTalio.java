package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrlTalio {

    //initialize the stage
    private Stage primaryStageTalio;
    private Scene home, joinBoard, createBoard, serverConnection;
    private HomeCtrl homeCtrl;
    private JoinBoardCodeCtrl joinBoardCodeCtrl;
    private CreateBoardCtrl createBoardCtrl;
    private ServerConnectionCtrl serverConnectionCtrl;

    public void initialize(
        Stage primaryStageTalio,
        Pair<HomeCtrl, Parent> homePair,
        Pair<JoinBoardCodeCtrl, Parent> joinBoardPair,
        Pair<CreateBoardCtrl, Parent> createBoardPair,
        Pair<ServerConnectionCtrl, Parent> serverConnectionPair)
    {
        this.primaryStageTalio=primaryStageTalio;

        this.homeCtrl = homePair.getKey();
        this.home = new Scene(homePair.getValue());

        this.joinBoardCodeCtrl = joinBoardPair.getKey();
        this.joinBoard = new Scene(joinBoardPair.getValue());

        this.createBoardCtrl = createBoardPair.getKey();
        this.createBoard = new Scene(createBoardPair.getValue());

        this.serverConnectionCtrl = serverConnectionPair.getKey();
        this.serverConnection = new Scene(serverConnectionPair.getValue());

        showHome();
        primaryStageTalio.show();

    }
    public void showHome(){
        primaryStageTalio.setTitle("Talio: Overview");
        primaryStageTalio.setScene(home);
        homeCtrl.displayBoardLabels();
    }

    //initialize the controller for the main board scene

    //initialize the 'main board' scene

    //initialize other scenes and their controllers


    //TODO: FIX THESE THREE METHODS. DON'T CALL THEM FROM OUTSIDE
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

}
