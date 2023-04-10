package server.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Board;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
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
        expectedBoards.add(new Board("board1", "code1", "readOnlyCode1", "", "", null, 0));
        expectedBoards.add(new Board("board2", "code2", "readOnlyCode2", "", "", null, 0));
        when(boardRepositoryMock.findAll()).thenReturn(expectedBoards);

        List<Board> returnedBoards = boardServiceMock.getMany();

        assertEquals(expectedBoards, returnedBoards);
    }

    @Test
    public void testGetManyWithEmptyList() {
        when(boardRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        List<Board> returnedBoards = boardServiceMock.getMany();

        assertTrue(returnedBoards.isEmpty());
    }

    @Test
    public void testGetManyWithNullList() {
        when(boardRepositoryMock.findAll()).thenReturn(null);

        List<Board> returnedBoards = boardServiceMock.getMany();

        assertNull(returnedBoards);
    }

    @Test
    public void getOne() {
        Long boardId = 1L;
        Board expectedBoard = new Board("board1", "code1", "readOnlyCode1", "", "", null, 0);
        when(boardRepositoryMock.findById(boardId)).thenReturn(Optional.of(expectedBoard));

        Optional<Board> returnedBoard = boardServiceMock.getOne(boardId);
        assertEquals(expectedBoard, returnedBoard.get());
    }

    @Test
    public void testGetOneWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            boardServiceMock.getOne(null);
        });
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

        when(boardRepositoryMock.save(any())).thenReturn(board);

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
        existingBoard.setBoardColor("#000000/#555555");
        existingBoard.setListsColor("#000000/#555555");
        existingBoard.setCode("abc");
        existingBoard.setReadOnlyCode("a");
        ArrayList<String> list = new ArrayList<>();
        list.add("#000000/#222222");
        existingBoard.setCardColorPresets(list);
        existingBoard.setDefaultPresetNum(0);

        Board updatedBoard = new Board();
        updatedBoard.setId(2L);
        updatedBoard.setName("Updated Board");
        updatedBoard.setBoardColor("#000000/#222222");
        updatedBoard.setListsColor("#000000/#222222");
        updatedBoard.setCode("abc");
        updatedBoard.setReadOnlyCode("b");
        ArrayList<String> updatedList = new ArrayList<>();
        updatedList.add("#000000/#222223");
        updatedBoard.setCardColorPresets(updatedList);
        existingBoard.setDefaultPresetNum(1);



        when(boardRepositoryMock.findById(1L)).thenReturn(Optional.of(existingBoard));
        when(boardRepositoryMock.save(existingBoard)).thenReturn(existingBoard);

        Board result = boardServiceMock.updateOne(1L, updatedBoard);

        assertEquals(result, existingBoard);
        assertEquals(existingBoard.getName(), updatedBoard.getName());
        assertEquals(existingBoard.getBoardColor(), updatedBoard.getBoardColor());
        assertEquals(existingBoard.getListsColor(), updatedBoard.getListsColor());
        assertEquals(existingBoard.getCode(), updatedBoard.getCode());
        assertEquals(existingBoard.getReadOnlyCode(), updatedBoard.getReadOnlyCode());
        assertEquals(existingBoard.getCardColorPresets(), updatedBoard.getCardColorPresets());
        assertEquals(existingBoard.getDefaultPresetNum(), updatedBoard.getDefaultPresetNum());

    }

    /*
    @Test
    public void updateOneCards() throws EntityNotFoundException {

        Board existingBoard = new Board();
        existingBoard.setId(1L);
        existingBoard.setName("Test Board");
        existingBoard.setBoardColor("#000000/#555555");
        existingBoard.setListsColor("#000000/#555555");
        existingBoard.setCode("abc");
        existingBoard.setReadOnlyCode("a");
        ArrayList<String> list = new ArrayList<>();
        list.add("#000000/#222222");
        existingBoard.setCardColorPresets(list);
        existingBoard.setDefaultPresetNum(0);

        Board updatedBoard = new Board();
        updatedBoard.setId(2L);
        updatedBoard.setName("Updated Board");
        updatedBoard.setBoardColor("#000000/#222222");
        updatedBoard.setListsColor("#000000/#222222");
        updatedBoard.setCode("abc");
        updatedBoard.setReadOnlyCode("b");
        ArrayList<String> updatedList = new ArrayList<>();
        updatedList.add("#000000/#222223");
        updatedBoard.setCardColorPresets(updatedList);
        updatedBoard.setDefaultPresetNum(1);

        Card card1 = new Card();
        card1.setId(1L);
        card1.setColorPresetNumber(0);


        Card card2 = new Card();
        card2.setId(2L);
        card2.setColorPresetNumber(0);

        CardList list1 = new CardList();
        list1.setId(1L);
        list1.setTitle("Test List 1");
        list1.setCards(Arrays.asList(card1, card2));
        list1.setBoard(existingBoard);

        card1.setList(list1);
        card2.setList(list1);

        existingBoard.setLists(Arrays.asList(list1));


        when(boardRepositoryMock.findById(1L)).thenReturn(Optional.of(existingBoard));
        when(boardRepositoryMock.save(existingBoard)).thenReturn(existingBoard);

        CardRepository cardRepositoryMock = mock(CardRepository.class);
        when(cardRepositoryMock.findById(1L)).thenReturn(Optional.of(card1));
        when(cardRepositoryMock.findById(2L)).thenReturn(Optional.of(card2));
        when(cardRepositoryMock.save(any(Card.class))).thenReturn(new Card());


        BoardService boardService = new BoardService(boardRepositoryMock, cardRepositoryMock);

        Board result = boardService.updateOne(1L, updatedBoard);
        System.out.println(result);

        assertEquals(result, existingBoard);
        assertEquals(existingBoard.getName(), updatedBoard.getName());
        assertEquals(existingBoard.getBoardColor(), updatedBoard.getBoardColor());
        assertEquals(existingBoard.getListsColor(), updatedBoard.getListsColor());
        assertEquals(existingBoard.getCode(), updatedBoard.getCode());
        assertEquals(existingBoard.getReadOnlyCode(), updatedBoard.getReadOnlyCode());
        assertEquals(existingBoard.getCardColorPresets(), updatedBoard.getCardColorPresets());
        assertEquals(existingBoard.getDefaultPresetNum(), updatedBoard.getDefaultPresetNum());

        long colorPresetNumber1 = card1.getColorPresetNumber().intValue();
        long colorPresetNumber2 = card2.getColorPresetNumber().intValue();
        assertEquals(colorPresetNumber1, 1L);
        assertEquals(colorPresetNumber2, 1L);

    }
     */

    @Test
    public void isCodeAlreadyUsed() {
        String code = "123";
        Board board = new Board();
        board.setCode(code);

        when(boardRepositoryMock.findByCode(code)).thenReturn(Optional.of(board));

        Assertions.assertTrue(boardServiceMock.isCodeAlreadyUsed(code));
    }

    @Test
    public void testDeleteOneWithNullId() {
        Long id = null;

        assertThrows(IllegalArgumentException.class, () -> boardServiceMock.deleteOne(id));
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
    
    @Test
    public void deleteMany() {
        Board board1 = new Board("board1", "code1", "readOnlyCode1", "", "", null, 0);
        Board board2 = new Board("board2", "code2", "readOnlyCode2", "", "", null, 0);
        boardRepositoryMock.saveAll(Arrays.asList(board1, board2));

        boardServiceMock.deleteMany();

        List<Board> returnedBoards = boardRepositoryMock.findAll();
        assertTrue(returnedBoards.isEmpty());
    }
}
