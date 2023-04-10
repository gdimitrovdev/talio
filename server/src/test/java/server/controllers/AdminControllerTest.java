package server.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Board b = new Board("My board", "Bla", "Ble", "Bli", "", null, 0);
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

    @Test
    public void testClearException() {
        doThrow(new RuntimeException("")).when(tagServiceMock).deleteMany();

        ResponseEntity response = adminControllerMock.clear();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    /*
    @Test
    void refill() {
        doNothing().when(subtaskServiceMock).deleteMany();
        doNothing().when(cardServiceMock).deleteMany();
        doNothing().when(tagServiceMock).deleteMany();
        doNothing().when(cardListServiceMock).deleteMany();
        doNothing().when(boardServiceMock).deleteMany();

        ResponseEntity<?> response = adminControllerMock.refill();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(subtaskServiceMock, times(1)).deleteMany();
        verify(cardServiceMock, times(1)).deleteMany();
        verify(tagServiceMock, times(1)).deleteMany();
        verify(cardListServiceMock, times(1)).deleteMany();
        verify(boardServiceMock, times(1)).deleteMany();

    }
    @Test
    void fill() {
        assertTrue(adminControllerMock.fill().getStatusCode().is2xxSuccessful());

        verify(boardServiceMock, times(3)).createOne(any(Board.class));
    }

    @Test
    void refill() {
        assertTrue(adminControllerMock.refill().getStatusCode().is2xxSuccessful());
    }

    @Test
    void fill() {
        assertTrue(adminControllerMock.fill().getStatusCode().is2xxSuccessful());
    }
    */
    @Test
    void fill() {
        assertFalse(adminControllerMock.fill().getStatusCode().is2xxSuccessful());

        verify(boardServiceMock, times(1)).createOne(any(Board.class));
    }

    @Test
    void refill() {
        assertFalse(adminControllerMock.refill().getStatusCode().is2xxSuccessful());
    }

}
