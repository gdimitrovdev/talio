package client.scenes;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import client.utils.ServerUtils;
import commons.Board;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
class HomeCtrlTest {
    @Mock
    private MainCtrlTalio mainCtrlTalio;

    HomeCtrl homeCtrl;

    @Mock
    ServerUtils serverUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        serverUtils = new ServerUtils();
        homeCtrl = new HomeCtrl(serverUtils, mainCtrlTalio);

    }

    /**
     * testing the constructor of the HomeCtrl class
     */
    @Test
    public void testHomeCtrlConstructor() {
        // Create mock objects for ServerUtils and MainCtrlTalio

        // Instantiate HomeCtrl with the mock objects
        HomeCtrl homeCtrl = new HomeCtrl(serverUtils, mainCtrlTalio);

        // Verify that the server and mainCtrlTalio fields are set correctly
        assertEquals(serverUtils, homeCtrl.getServer());
        assertEquals(mainCtrlTalio, homeCtrl.getMainCtrlTalio());
    }

    /**
     * test checks if when openServerScene is called,
     * showServerConnection from the MainCtrlTalio is called
     */
    @Test
    public void testOpenServerScene() {
        homeCtrl.openServerScene();
        // Verify that showServerConnection() method is called on mainCtrlTalio
        verify(mainCtrlTalio).showServerConnection();
    }

    @Test
    public void testDisplayCreatePopUp() {
        homeCtrl.displayCreatePopUp();
        // Verify that showServerConnection() method is called on mainCtrlTalio
        verify(mainCtrlTalio).showCreateBoard();
    }

    @Test
    public void testDisplayJoinPopUp() {
        homeCtrl.displayJoinPopUp();
        // Verify that showServerConnection() method is called on mainCtrlTalio
        verify(mainCtrlTalio).showJoinBoardCode();
    }

    @Test
    public void testDisplayBoard() throws IOException {
        Board board = new Board();
        board.setId(1L);

        homeCtrl.displayBoard(board);

        //the void methods can be tested with the 'verify' method
        //that checks whether a method is called with a specific input
        verify(mainCtrlTalio, times(1)).showBoard(board);
    }

    @Test
    public void  testOpenBoardSetting() {
        Board board = new Board();
        board.setId(1L);

        homeCtrl.openBoardSetting(board);
        verify(mainCtrlTalio).showBoardSettings(board);
    }

}



