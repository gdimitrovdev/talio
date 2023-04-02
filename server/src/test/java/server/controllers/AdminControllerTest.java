package server.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import commons.Board;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import server.services.BoardService;
import server.services.CardListService;
import server.services.CardService;
import server.services.SubtaskService;
import server.services.TagService;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {
    @Mock
    private BoardService boardServiceMock;
    @Mock
    private CardListService cardListServiceMock;
    @Mock
    private CardService cardServiceMock;
    @Mock
    private TagService tagServiceMock;
    @Mock
    private SubtaskService subtaskServiceMock;
    @InjectMocks
    private AdminController adminControllerMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get() {
        String html = """
                Avaliable endpoints:
                <ul>
                <li>/api/admin/clear - Clears the database</li>
                <li>/api/admin/fill - Fills the database with example data</li>
                <li>/api/admin/refill - Clears the database and fills it with example data</li>
                <li>/api/admin/boards - Lists all boards and their invite codes</li>
                </ul>
                """;

        assertEquals(html, adminControllerMock.get());
    }

    @Test
    void getBoards() {
        Board b = new Board("My board", "Bla", "Ble", "Bli");
        b.setId(3L);

        when(boardServiceMock.getMany()).thenReturn(List.of(b));

        String header = """
                Avaliable boards:
                <table style="border: 1px solid black; border-collapse: collapse">
                    <tr>
                        <th style="border: 1px solid black">Id</th>
                        <th style="border: 1px solid black">Name</th>
                        <th style="border: 1px solid black">Join Code</th>
                        <th style="border: 1px solid black">Read-Only Join Code</th>
                    </tr>
                """;

        String data = "<tr><td style=\"border: 1px solid black\">"
                + b.getId() + "</td><td style=\"border: 1px solid black\">" + b.getName() + "</td><td style=\"border: 1px solid black\">"
                + b.getCode() + "</td><td style=\"border: 1px solid black\">" + b.getReadOnlyCode() + "</td></tr>"
                + "</table>";

        assertEquals(header + data, adminControllerMock.getBoards());
    }

    @Test
    void clear() {
        assertTrue(adminControllerMock.clear().getStatusCode().is2xxSuccessful());
    }

    /*
    @Test
    void refill() {
        assertTrue(adminControllerMock.refill().getStatusCode().is2xxSuccessful());
    }

    @Test
    void fill() {
        assertTrue(adminControllerMock.fill().getStatusCode().is2xxSuccessful());
    }
    */
}
