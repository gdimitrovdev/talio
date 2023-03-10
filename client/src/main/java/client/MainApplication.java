package client;
import client.scenes.BoardCtrl;
import client.scenes.MainCtrlTalio;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import static com.google.inject.Guice.createInjector;

public class MainApplication extends Application {
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args){
        launch();
    }

    public void start(Stage primaryStageTalio){

        var mainControl = INJECTOR.getInstance(MainCtrlTalio.class);
        var boardPair = FXML.load(BoardCtrl.class, "client", "scenes", "board.fxml");
        mainControl.initialize(primaryStageTalio, boardPair);
    }
}
