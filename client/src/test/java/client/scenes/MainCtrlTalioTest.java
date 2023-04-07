package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import client.utils.ServerUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)

public class MainCtrlTalioTest {
    private MainCtrlTalio mainCtrlTalio;
    @Mock private ServerUtils serverUtils;
    private Map<String, Set<Long>> joinedBoards;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        joinedBoards = new HashMap<>();
        mainCtrlTalio = new MainCtrlTalio(serverUtils);
        mainCtrlTalio.setJoinedBoards(joinedBoards);

    }

    @Test
    public void testAddJoinedBoard() {
        String serverUrl = "http://localhost:8080";
        Long boardId = 1L;
        mainCtrlTalio.addJoinedBoard(serverUrl, boardId);

        Set<Long> expectedBoardIds = new HashSet<>();
        expectedBoardIds.add(boardId);
        assertEquals(expectedBoardIds, mainCtrlTalio.getJoinedBoardForServer(serverUrl));
    }

    @Test
    public void testRemoveJoinedBoard() {
        String serverUrl = "http://localhost:8080";
        Long boardId = 1L;
        mainCtrlTalio.addJoinedBoard(serverUrl, boardId);
        mainCtrlTalio.removeJoinedBoard(serverUrl, boardId);

        Set<Long> expectedBoardIds = new HashSet<>();
        assertEquals(expectedBoardIds, mainCtrlTalio.getJoinedBoardForServer(serverUrl));
    }

    @Test
    public void testGetJoinedBoardForServer() {
        String serverUrl = "http://localhost:8080";
        Long boardId1 = 1L;
        Long boardId2 = 2L;

        mainCtrlTalio.addJoinedBoard(serverUrl, boardId1);
        mainCtrlTalio.addJoinedBoard(serverUrl, boardId2);

        Set<Long> expectedBoardIds = new HashSet<>();
        expectedBoardIds.add(boardId1);
        expectedBoardIds.add(boardId2);

        assertEquals(expectedBoardIds, mainCtrlTalio.getJoinedBoardForServer(serverUrl));
    }
}