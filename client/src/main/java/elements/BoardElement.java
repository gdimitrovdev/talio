package elements;

import client.scenes.MainCtrlTalio;
import commons.Board;
import commons.CardList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;


public class BoardElement  extends ScrollPane {
    private final MainCtrlTalio mainCtrlTalio;

    private HBox innerHBox;


    public BoardElement(Board board, MainCtrlTalio mainCtrlTalio)  {
        super();
        this.mainCtrlTalio = mainCtrlTalio;

        this.setLayoutX(10.0);
        this.setLayoutY(40.0);


        //create the UI components
        HBox outerHBox = new HBox();
        HBox innerHBox = new HBox();
        Button newListButton = new Button("+");
        this.innerHBox=innerHBox;

        // set the style for the UI components
        outerHBox.setStyle(" -fx-min-width: 780.0; -fx-min-height: 510.0; -fx-background-color: #d3dbe8;");
        outerHBox.setPadding(new Insets(40, 0, 0, 40));
        outerHBox.setSpacing(80);
        newListButton.setStyle("-fx-pref-width: 30px; -fx-pref-height: 30px;");
        Tooltip newListToolTip = new Tooltip("add a new list");
        newListButton.setTooltip(newListToolTip);

        //add the CardListElement instances to the innerHBox
        for(CardList cardList : board.getLists()){
            // TODO: Change this to FXML
            CardListElement cardListEl = new CardListElement(cardList, mainCtrlTalio);
            innerHBox.getChildren().add(cardListEl);
            innerHBox.setMargin(cardListEl, new Insets(0, 10, 0, 0));
        }
        //add the innerHBox and newListButton to the outerHBox
        outerHBox.getChildren().add(innerHBox);
        outerHBox.getChildren().add(newListButton);

        // Wrap the outerHBox in a StackPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(outerHBox);

        // Set the content of the ScrollPane to the StackPane
        setContent(stackPane);

        // Set the vertical scrollbar to always be shown
        //and the horizontal scrollbar to never be shown
        setHbarPolicy(ScrollBarPolicy.ALWAYS);
        setVbarPolicy(ScrollBarPolicy.NEVER);

        VBox.setVgrow(this, Priority.ALWAYS);

    }
    //TODO implement this method
    public void addCardList(){

        CardList current = new CardList();
        current.setTitle("Enter list title");
        innerHBox.getChildren().add(new CardListElement(current, mainCtrlTalio));
        //TO DO: the id of the list and the id of the board
        //attributes of the cardList should be set here

    }

}
