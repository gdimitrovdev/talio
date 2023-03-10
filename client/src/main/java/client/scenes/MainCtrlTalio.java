package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

public class MainCtrlTalio {

    //initialize the stage
    private Stage primaryStageTalio;
    private Scene board;
    private BoardCtrl boardCtrl;

    public void initialize(Stage primaryStageTalio,
                           Pair<BoardCtrl, Parent> boardPair){
        this.primaryStageTalio=primaryStageTalio;
        this.boardCtrl=boardPair.getKey();
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
