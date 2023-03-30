package server.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Board;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import server.database.BoardRepository;

@RunWith(MockitoJUnitRunner.class)
class BoardServiceTest {


    // @Mock annotation is used to create a mock BoardRepository object
    @Mock
    private BoardRepository boardRepositoryMock;


    //@InjectMocks annotation is used to inject the mock BoardRepository object into the BoardService object being tested.
    @InjectMocks
    private BoardService boardServiceMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getMany() {
        List<Board> expectedBoards = new ArrayList<>();
        expectedBoards.add(new Board("board1", "code1", "readOnlyCode1", "red"));
        expectedBoards.add(new Board("board2", "code2", "readOnlyCode2", "blue"));
        when(boardRepositoryMock.findAll()).thenReturn(expectedBoards);

        List<Board> returnedBoards = boardServiceMock.getMany();

        assertEquals(expectedBoards, returnedBoards);
    }

    @Test
    public void getOne() {
        Long boardId = 1L;
        Board expectedBoard = new Board("board1", "code1", "readOnlyCode1", "red");
        when(boardRepositoryMock.findById(boardId)).thenReturn(Optional.of(expectedBoard));

        Optional<Board> returnedBoard = boardServiceMock.getOne(boardId);
        assertEquals(expectedBoard, returnedBoard.get());
    }

    @Test
    public void getOneWithInvalidId() {
        Optional<Board> optionalBoard = Optional.empty();
        when(boardRepositoryMock.findById(1L)).thenReturn(optionalBoard);

        Optional<Board> result = boardServiceMock.getOne(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void getOneByCode() {
        Board board = new Board();
        board.setCode("12345");
        when(boardRepositoryMock.findByCode("12345")).thenReturn(Optional.of(board));
        Optional<Board> result = boardServiceMock.getOneByCode("12345");
        assertEquals("12345", result.get().getCode());
    }

    @Test
    public void getOneByInvalidCode() {
        Optional<Board> optionalBoard = Optional.empty();
        when(boardRepositoryMock.findByCode("12345")).thenReturn(optionalBoard);
        Optional<Board> result = boardServiceMock.getOneByCode("12345");
        assertTrue(result.isEmpty());
    }

    @Test
    public void createOne() {
        Board board = new Board();

        when(boardRepositoryMock.save(ArgumentMatchers.any())).thenReturn(board);

        Board createdBoard = boardServiceMock.createOne(board);
        assertEquals(board, createdBoard);
    }

    // Test whether deleteById() method of the repository was called for the correct board
    @Test
    public void deleteOne() {

        when(boardRepositoryMock.existsById(1L)).thenReturn(true);

        boardServiceMock.deleteOne(1L);

        verify(boardRepositoryMock).deleteById(1L);

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
        updatedBoard.setId(2L);
        updatedBoard.setName("Updated Board");
        updatedBoard.setColor("#blue");
        updatedBoard.setCode("abc");
        updatedBoard.setReadOnlyCode("b");


        when(boardRepositoryMock.findById(1L)).thenReturn(Optional.of(existingBoard));
        when(boardRepositoryMock.save(existingBoard)).thenReturn(existingBoard);

        Board result = boardServiceMock.updateOne(1L, updatedBoard);

        assertEquals(result, existingBoard);
        assertEquals(existingBoard.getName(), updatedBoard.getName());
        assertEquals(existingBoard.getColor(), updatedBoard.getColor());
        assertEquals(existingBoard.getCode(), updatedBoard.getCode());
        assertEquals(existingBoard.getReadOnlyCode(), updatedBoard.getReadOnlyCode());

    }

    @Test
    public void isCodeAlreadyUsed() {
        String code = "123";
        Board board = new Board();
        board.setCode(code);

        when(boardRepositoryMock.findByCode(code)).thenReturn(Optional.of(board));

        Assertions.assertTrue(boardServiceMock.isCodeAlreadyUsed(code));
    }

    @Test
    public void updateBoardCodes() {
        Board board = new Board();
        String firstCode = board.getCode();
        String readOnlyCode = board.getReadOnlyCode();

        boardServiceMock.updateBoardCodes(board);

        Assertions.assertNotEquals(firstCode, board.getCode());
        Assertions.assertNotEquals(readOnlyCode, board.getReadOnlyCode());

    }

    @Test
    public void getRandomCode() {
        String code1 = boardServiceMock.getNewRandomCode(4);
        String code2 = boardServiceMock.getNewRandomCode(4);
        Assertions.assertNotEquals(code1, code2);

    }
}
