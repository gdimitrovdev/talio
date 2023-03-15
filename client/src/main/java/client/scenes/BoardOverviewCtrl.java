package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

public class BoardOverviewCtrl {
    private MainCtrlTalio mainCtrlTalio;
    @FXML
    private ScrollPane root;

    @FXML
    private HBox outerHBox;

    @FXML
    private HBox innerHBox;

    @FXML
    private Button addListButton;

    @Inject
    public BoardOverviewCtrl(MainCtrlTalio mainCtrlTalio) {
        this.mainCtrlTalio = mainCtrlTalio;
    }


}
