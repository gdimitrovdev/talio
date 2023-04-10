package client.scenes;

import client.components.CardComponentCtrl;
import client.components.ListComponentCtrl;
import client.components.TitleField;
import client.utils.ServerUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BoardCtrlTest {
    @Mock
    private ServerUtils serverUtils;

    @Mock
    private MainCtrlTalio mainCtrlTalio;

    private BoardCtrl boardCtrl;

    @BeforeEach
    public void init() throws IOException {
        MockitoAnnotations.openMocks(this);
        boardCtrl = new BoardCtrl(serverUtils, mainCtrlTalio);
    }

    @Test
    void getCardListsFromBoard() {
        boardCtrl.setInnerHBox(new HBox());
        boardCtrl.getInnerHBox().getChildren().add(Mockito.mock(ListComponentCtrl.class));
        assertEquals(1, boardCtrl.getCardListsFromBoard().size());
    }
}
