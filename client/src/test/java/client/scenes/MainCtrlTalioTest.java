package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import client.utils.ServerUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)

public class MainCtrlTalioTest {
    @InjectMocks
    private MainCtrlTalio mainCtrlTalio;
    @Mock
    private ServerUtils serverUtils;
    private Map<String, Set<Pair<Long, String>>> joinedBoards;

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
        assertEquals(expectedBoardIds, mainCtrlTalio.getJoinedBoardsForServer(serverUrl));
    }

    @Test
    public void testRemoveJoinedBoard() {
        String serverUrl = "http://localhost:8080";
        Long boardId = 1L;
        mainCtrlTalio.addJoinedBoard(serverUrl, boardId);
        mainCtrlTalio.removeJoinedBoard(serverUrl, boardId);

        Set<Long> expectedBoardIds = new HashSet<>();
        assertEquals(expectedBoardIds, mainCtrlTalio.getJoinedBoardsForServer(serverUrl));
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

        assertEquals(expectedBoardIds, mainCtrlTalio.getJoinedBoardsForServer(serverUrl));
    }

    @Test
    void testWriteReadLocalData() {
        // Prepare test data
        Map<String, Set<Pair<Long, String>>> testData = new HashMap<>();
        testData.put("http://localhost:8080", new HashSet<>(Arrays.asList(new Pair<>(1L, ""),
                new Pair<>(2L, ""),
                new Pair<>(3L, ""))));
        testData.put("http://example.com", new HashSet<>(Arrays.asList(new Pair<>(4L, ""),
                new Pair<>(5L, ""), new Pair<>(6L, ""))));

        // Write test data to file
        mainCtrlTalio.setJoinedBoards(testData);
        mainCtrlTalio.writeToLocalData();

        // Read test data from file
        mainCtrlTalio.readFromLocalData();
        Map<String, Set<Pair<Long, String>>> result = mainCtrlTalio.getJoinedBoards();

        // Assert that the test data was written and read successfully
        assertEquals(testData, result);
    }
}
