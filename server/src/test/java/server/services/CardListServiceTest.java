package server.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Board;
import commons.CardList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import server.database.BoardRepository;
import server.database.CardListRepository;

@RunWith(MockitoJUnitRunner.class)
class CardListServiceTest {

    // @Mock annotation is used to create a mock BoardRepository object
    @Mock
    private CardListRepository cardListRepositoryMock;

    @Mock
    private BoardRepository boardRepositoryMock;

    @InjectMocks
    private CardListService cardListService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMany() {
        List<CardList> cardLists = new ArrayList<>();
        CardList cardList1 = new CardList();
        CardList cardList2 = new CardList();
        cardLists.add(cardList1);
        cardLists.add(cardList2);

        when(cardListRepositoryMock.findAll()).thenReturn(cardLists);

        List<CardList> returnedCardLists = cardListService.getMany();
        assertEquals(cardList1, returnedCardLists.get(0));
        assertEquals(cardList2, returnedCardLists.get(1));
    }

    @Test
    void getOne() {
        Long listId = 1L;
        CardList cardList = new CardList();
        when(cardListRepositoryMock.existsById(listId)).thenReturn(true);
        when(cardListRepositoryMock.findById(listId)).thenReturn(Optional.of(cardList));

        Optional<CardList> returnedCardList = cardListService.getOne(1L);
        assertTrue(returnedCardList.isPresent());
        assertEquals(cardList, returnedCardList.get());
    }

    @Test
    void getOneWhenListDoesNotExist() {
        Long listId = 99999L;
        when(cardListRepositoryMock.existsById(listId)).thenReturn(false);

        Optional<CardList> returnedCardList = cardListService.getOne(listId);

        assertTrue(returnedCardList.isEmpty());
    }

    @Test
    void createOne() {
        Board board = new Board();
        board.setId(1L);
        CardList list = new CardList("List title", board);
        when(boardRepositoryMock.findById(board.getId())).thenReturn(Optional.of(board));
        when(cardListRepositoryMock.save(list)).thenReturn(list);

        CardList returnedList = cardListService.createOne(list);
        assertEquals(list, returnedList);
    }

    @Test
    void createOneWithInvalidBoard() {
        Board board = new Board();
        board.setId(1L);
        CardList list = new CardList("List title", board);
        when(boardRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        when(cardListRepositoryMock.save(list)).thenReturn(list);
        assertThrows(Exception.class, () -> {
            cardListService.createOne(list);
        });
    }

    @Test
    void createOneWithNullBoard() {
        Board board = new Board();
        board.setId(1L);
        CardList list = new CardList("List title", null);
        when(boardRepositoryMock.findById(1L)).thenReturn(Optional.of(board));
        when(cardListRepositoryMock.save(list)).thenReturn(list);
        assertThrows(Exception.class, () -> {
            cardListService.createOne(list);
        });
    }

    @Test
    void createOneWithNullBoardId() {
        Board board = new Board();
        board.setId(null);
        CardList list = new CardList("List title", board);
        when(boardRepositoryMock.findById(1L)).thenReturn(Optional.of(board));
        when(cardListRepositoryMock.save(list)).thenReturn(list);
        assertThrows(Exception.class, () -> {
            cardListService.createOne(list);
        });
    }

    // Test whether deleteById() method of the repository was called for the correct cardList
    @Test
    void deleteOne() {
        when(cardListRepositoryMock.existsById(1L)).thenReturn(true);

        cardListService.deleteOne(1L);

        verify(cardListRepositoryMock).deleteById(1L);
    }

    @Test
    void updateOne() {
        CardList cardList = new CardList();
        cardList.setId(
                1L); //setting the id and title manually because otherwise all constructors require a Board
        cardList.setTitle("title1");

        when(cardListRepositoryMock.findById(1L)).thenReturn(Optional.of(cardList));
        when(cardListRepositoryMock.save(cardList)).thenReturn(cardList);

        CardList updatedCardList = new CardList();
        updatedCardList.setTitle("title2");
        CardList returnedCardList = cardListService.updateOne(1L, updatedCardList);
        assertEquals(updatedCardList.getTitle(), returnedCardList.getTitle());

    }

    @Test
    void deleteManyEmpty() {
        cardListService.deleteMany();

        verify(cardListRepositoryMock).deleteAll();
    }

    @Test
    void deleteManyMultiple() {
        List<CardList> cardLists = new ArrayList<>();
        CardList cardList1 = new CardList();
        CardList cardList2 = new CardList();
        cardLists.add(cardList1);
        cardLists.add(cardList2);

        when(cardListRepositoryMock.findAll()).thenReturn(cardLists);

        cardListService.deleteMany();

        verify(cardListRepositoryMock).deleteAll();
    }

    @Test
    void deleteManyThrowsException() {
        doThrow(new RuntimeException("Unable to delete card lists")).when(cardListRepositoryMock)
                .deleteAll();

        //when(cardListRepositoryMock.deleteAll()).thenThrow(new RuntimeException("Unable to delete card lists"));

        assertThrows(RuntimeException.class, () -> cardListService.deleteMany());

        verify(cardListRepositoryMock).deleteAll();
    }
}
