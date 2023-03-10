package elements;

import client.scenes.MainCtrlTalio;
import commons.Card;
import commons.CardList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class CardListElement extends VBox {
    private final MainCtrlTalio mainCtrlTalio;
    //constructor
    public CardListElement(CardList cardList, MainCtrlTalio mainCtrlTalio) {
        super();
        this.mainCtrlTalio = mainCtrlTalio;

        //create a HBox that will be the header of the list
        HBox titleHolder = new HBox();
        TextField titleField = new TextField(cardList.getTitle());
        Button deleteListBtn = new Button("x");
        Tooltip deleteListToolTip = new Tooltip("delete the this list");
        deleteListBtn.setTooltip(deleteListToolTip);
        titleHolder.getChildren().addAll(titleField,deleteListBtn);

        //set the style for the header of the list
        titleHolder.setStyle("-fx-pref-height: 30.0; -fx-pref-width: 180.0;");
        titleField.setStyle("-fx-pref-height: 25.0; -fx-pref-width: 130.0;");
        deleteListBtn.setStyle("-fx-pref-height: 25.0; -fx-pref-width: 25.0");


        //set VBox style properties
        super.setStyle("-fx-min-height: 450.0; -fx-pref-width: 180.0; " +
                "-fx-border-color: black; -fx-border-width: 1px; " +
                "-fx-background-color: #e6e6e6; -fx-padding: 5px;");
        this.setSpacing(20.0);

        //inserting the header HBox
        super.getChildren().add(titleHolder);

        //inserting the cards in the list
        for(Card card : cardList.getCards()){
                //instead of buttons, CardElement-s should be instantiated
                Button cardButton = new Button(card.getTitle());
                cardButton.setStyle("-fx-pref-width: 160px; -fx-pref-height: 40px;" +
                        " -fx-padding: 10 10 10 10; -fx-spacing: 10px");
                super.getChildren().add(cardButton);
        }


        //inserting the button for adding new cards
        Button addCardBtn = new Button("+");

        //setting the action for the button
        addCardBtn.setOnAction( event -> addCard());

        //setting the style for the button
        addCardBtn.setStyle("-fx-pref-width: 30px; -fx-pref-height: " +
                "30px; -fx-spacing: 10px;");

        //setting the ToolTip message for the 'add a card' button
        Tooltip addCardToolTip = new Tooltip("add a new card");
        addCardBtn.setTooltip(addCardToolTip);


        //adding the 'add Card' button
        super.getChildren().add(addCardBtn);

        //set VBox to grow with available space
        VBox.setVgrow(this, Priority.ALWAYS);
    }





    /**
     * this method should open the pop-up for adding cards,
     * but currently it just creates an empty card
     */
    public void addCard(){
        Button newCard = new Button("new card");
        newCard.setStyle("-fx-pref-width: 160px; -fx-pref-height: 40px;" +
                " -fx-padding: 10 10 10 10; -fx-spacing: 10px");

        //placing the new card to be last but still abve the button
        int size = super.getChildren().size();
        if(size <= 1){
            super.getChildren().add(newCard);
        }
        super.getChildren().add(super.getChildren().size() - 1, newCard);



    }

}
