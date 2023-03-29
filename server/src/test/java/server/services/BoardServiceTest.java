package server.services;
import commons.Board;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import server.database.BoardRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
class BoardServiceTest  {


    // @Mock annotation is used to create a mock BoardRepository object
    @Mock
    private BoardRepository boardRepositoryMock;


    //@InjectMocks annotation is used to inject the mock BoardRepository object into the BoardService object being tested.
    @InjectMocks
    private BoardService boardService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testGetMany() {
        List<Board> expectedBoards = new ArrayList<>();
        expectedBoards.add(new Board("board1", "code1", "readOnlyCode1", "red"));
        expectedBoards.add(new Board("board2", "code2", "readOnlyCode2", "blue"));
        when(boardRepositoryMock.findAll()).thenReturn(expectedBoards);

        List<Board> actualBoards = boardService.getMany();

        assertEquals(expectedBoards, actualBoards);
    }


    @Test
    public void testGetOne() {
        Long boardId = 1L;
        Board expectedBoard = new Board("board1", "code1", "readOnlyCode1", "red");
        when(boardRepositoryMock.findById(boardId)).thenReturn(Optional.of(expectedBoard));

        Optional<Board> actualBoard = boardService.getOne(boardId);
        assertEquals(expectedBoard, actualBoard.get());
    }


    @Test
    public void testGetOneWithInvalidId() {
        Optional<Board> optionalBoard = Optional.empty();
        when(boardRepositoryMock.findById(1L)).thenReturn(optionalBoard);

        Optional<Board> result = boardService.getOne(1L);

        assertThat(result).isEmpty();
    }
    @Test
    public void testGetOneByCode() {
        Board board = new Board();
        board.setCode("12345");
        Optional<Board> optionalBoard = Optional.of(board);
        when(boardRepositoryMock.findByCode("12345")).thenReturn(optionalBoard);
        Optional<Board> result = boardService.getOneByCode("12345");
        assertEquals("12345", result.get().getCode());
    }


    @Test
    public void testGetOneByInvalidCode() {
        Optional<Board> optionalBoard = Optional.empty();
        when(boardRepositoryMock.findByCode("12345")).thenReturn(optionalBoard);
        Optional<Board> result = boardService.getOneByCode("12345");
        assertThat(result).isEmpty();
    }
    @Test
    public void testCreateOne() {
        Board board = new Board();
        board.setName("Test Board");
        board.setColor("#FF0000");

        Board savedBoard = new Board();
        savedBoard.setId(1L);
        savedBoard.setName(board.getName());
        savedBoard.setColor(board.getColor());

        when(boardRepositoryMock.save(ArgumentMatchers.any())).thenReturn(savedBoard);

        Board createdBoard = boardService.createOne(board);
        assertEquals(savedBoard, createdBoard);
    }


    @Test
    public void testDeleteOne() {
        Long boardId = 1L;

        when(boardRepositoryMock.existsById(boardId)).thenReturn(true);

        boardService.deleteOne(boardId);

        verify(boardRepositoryMock).deleteById(boardId);

    }


    @Test
    public void testUpdateOne() {
        Long boardId = 1L;

        Board existingBoard = new Board();
        existingBoard.setId(boardId);
        existingBoard.setName("Test Board");
        existingBoard.setColor("#red");
        existingBoard.setCode("abc");
        existingBoard.setReadOnlyCode("a");

        Board updatedBoard = new Board();
        updatedBoard.setId(boardId);
        updatedBoard.setName("Updated Board");
        updatedBoard.setColor("#blue");
        updatedBoard.setCode("abc");
        updatedBoard.setReadOnlyCode("b");


        when(boardRepositoryMock.findById(boardId)).thenReturn(Optional.of(existingBoard));
        when(boardRepositoryMock.findByCode(updatedBoard.getCode())).thenReturn(Optional.empty());
        when(boardRepositoryMock.findByCode(updatedBoard.getReadOnlyCode())).thenReturn(Optional.empty());
        when(boardRepositoryMock.save(existingBoard)).thenReturn(existingBoard);

        Board result = boardService.updateOne(boardId, updatedBoard);

        assertEquals(result, existingBoard);
        assertEquals(existingBoard.getName(), updatedBoard.getName());
        assertEquals(existingBoard.getColor(), updatedBoard.getColor());
        assertEquals(existingBoard.getCode(), updatedBoard.getCode());
        assertEquals(existingBoard.getReadOnlyCode(), updatedBoard.getReadOnlyCode());

    }
    @Test
    public void testIsCodeAlreadyUsed() {
        String code = "123";
        Board board = new Board();
        board.setCode(code);

        when(boardRepositoryMock.findByCode(code)).thenReturn(Optional.of(board));

        Assertions.assertEquals(boardService.isCodeAlreadyUsed(code), true);
    }
    @Test
    public void testUpdateBoardCodes() {
        Board board = new Board();
        String firstCode = board.getCode();
        String readOnlyCode = board.getReadOnlyCode();

        boardService.updateBoardCodes(board);

        Assertions.assertNotEquals(firstCode, board.getCode());
        Assertions.assertNotEquals(readOnlyCode, board.getReadOnlyCode());



    }




}
