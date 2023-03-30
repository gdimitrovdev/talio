/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.BoardCtrl;
import client.scenes.BoardSettingsCtrl;
import client.scenes.CreateBoardCtrl;
import client.scenes.HomeCtrl;
import client.scenes.JoinBoardCtrl;
import client.scenes.MainCtrlTalio;
import client.scenes.ServerConnectionCtrl;
import client.scenes.ShareBoardCtrl;
import com.google.inject.Injector;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    // TODO not sure if we should use static for the injector here
    public static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStageTalio) {

        var homePair = FXML.load(HomeCtrl.class, "client", "scenes", "HomeScene.fxml");
        var joinBoardPair = FXML.load(JoinBoardCtrl.class, "client", "scenes", "JoinBoard.fxml");
        var createBoardPair =
                FXML.load(CreateBoardCtrl.class, "client", "scenes", "CreateBoard.fxml");
        var serverConnectionPair =
                FXML.load(ServerConnectionCtrl.class, "client", "scenes", "ServerConnection.fxml");
        var boardComponentPair = FXML.load(BoardCtrl.class, "client", "scenes", "BoardScene.fxml");
        var shareBoardPair = FXML.load(ShareBoardCtrl.class, "client", "scenes", "ShareBoard.fxml");
        var boardSettingsPair =
                FXML.load(BoardSettingsCtrl.class, "client", "scenes", "BoardSettings.fxml");

        var mainControl = INJECTOR.getInstance(MainCtrlTalio.class);

        mainControl.initialize(primaryStageTalio, homePair, joinBoardPair, createBoardPair,
                serverConnectionPair, boardComponentPair, shareBoardPair, boardSettingsPair);


        /*var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "template", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "template", "AddQuote.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        mainCtrl.initialize(primaryStageTalio, overview, add);*/

    }
}
