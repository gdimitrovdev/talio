package server.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Board;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import server.services.BoardService;

@RunWith(MockitoJUnitRunner.class)
public class BoardControllerTest {
    @Mock
    private BoardService boardServiceMock;

    @InjectMocks
    private BoardController boardControllerMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getMany() {

        List<Board> expectedBoards = new ArrayList<>();
        expectedBoards.add(new Board("board1", "code1", "readOnlyCode1", "red"));
        expectedBoards.add(new Board("board2", "code2", "readOnlyCode2", "blue"));
        when(boardServiceMock.getMany()).thenReturn(expectedBoards);

        List<Board> returnedBoards = boardControllerMock.getMany();

        Assert.assertEquals(expectedBoards, returnedBoards);
    }

    @Test
    public void getOne() {
        Long boardId = 1L;
        Board expectedBoard = new Board("board1", "code1", "readOnlyCode1", "red");
        when(boardServiceMock.getOne(boardId)).thenReturn(Optional.of(expectedBoard));

        Board returnedBoard = boardControllerMock.getOne(boardId).getBody();
        assertEquals(expectedBoard, returnedBoard);
    }

    @Test
    public void getOneWithInvalidId() {
        Optional<Board> optionalBoard = Optional.empty();
        when(boardServiceMock.getOne(1L)).thenReturn(optionalBoard);

        var result = boardControllerMock.getOne(1L);

        assertTrue(result.getStatusCode().isError());
    }

    @Test
    public void getOneByCode() {
        Board board = new Board();
        board.setCode("12345");
        when(boardServiceMock.getOneByCode("12345")).thenReturn(Optional.of(board));
        Board result = boardControllerMock.getOneByCode("12345").getBody();
        assertEquals("12345", result.getCode());
    }

    @Test
    public void getOneByInvalidCode() {
        Optional<Board> optionalBoard = Optional.empty();
        when(boardServiceMock.getOneByCode("12345")).thenReturn(optionalBoard);
        var result = boardControllerMock.getOneByCode("12345");
        assertTrue(result.getStatusCode().isError());
    }

    @Test
    public void createOne() {
        Board board = new Board();

        when(boardServiceMock.createOne(ArgumentMatchers.any())).thenReturn(board);

        Board createdBoard = boardControllerMock.createOne(board).getBody();
        assertEquals(board, createdBoard);
    }

    // Test whether deleteById() method of the repository was called for the correct board
    @Test
    public void deleteOne() {

        boardControllerMock.deleteOne(1L);

        verify(boardServiceMock).deleteOne(1L);

    }

    @Test
    public void updateOne() {

        Board existingBoard = new Board();
        existingBoard.setId(1L);
        existingBoard.setName("Test Board");
        existingBoard.setColor("#red");
        existingBoard.setCode("abc");
        existingBoard.setReadOnlyCode("a");

        Board updatedBoard = new Board();
        updatedBoard.setId(1L);
        updatedBoard.setName("Updated Board");
        updatedBoard.setColor("#blue");
        updatedBoard.setCode("abc");
        updatedBoard.setReadOnlyCode("b");

        when(boardServiceMock.updateOne(1L, existingBoard)).thenReturn(updatedBoard);

        Board result = boardControllerMock.updateOne(1L, existingBoard).getBody();

        assertEquals(updatedBoard, result);

    }
}
