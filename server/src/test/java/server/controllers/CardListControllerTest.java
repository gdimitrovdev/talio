package server.controllers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Board;
import commons.CardList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.services.BoardService;
import server.services.CardListService;

@RunWith(MockitoJUnitRunner.class)
class CardListControllerTest {

    // @Mock annotation is used to create a mock BoardRepository object
    @Mock
    private CardListService cardListServiceMock;

    @Mock
    private BoardService boardServiceMock;

    @Mock
    private SimpMessagingTemplate templateMock;

    @InjectMocks
    private CardListController cardListControllerMock;

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

        when(cardListServiceMock.getMany()).thenReturn(cardLists);

        List<CardList> returnedCardLists = cardListControllerMock.getMany();
        assertEquals(cardList1, returnedCardLists.get(0));
        assertEquals(cardList2, returnedCardLists.get(1));
    }

    @Test
    void getOne() {
        Long listId = 1L;
        CardList cardList = new CardList();
        when(cardListServiceMock.getOne(listId)).thenReturn(Optional.of(cardList));

        CardList returnedCardList = cardListControllerMock.getOne(1L).getBody();
        assertEquals(cardList, returnedCardList);
    }

    @Test
    void createOne() {
        CardList cardList = new CardList();

        when(cardListServiceMock.createOne(cardList)).thenReturn(cardList);

        CardList returnedCardList = cardListControllerMock.createOne(cardList).getBody();
        assertEquals(cardList, returnedCardList);
    }

    @Test
    public void createOneException() {
        CardList cardList = new CardList();
        doThrow(new RuntimeException()).when(cardListServiceMock).createOne(cardList);

        ResponseEntity<CardList> response = cardListControllerMock.createOne(cardList);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Test whether deleteById() method of the repository was called for the correct cardList
    @Test
    void deleteOne() {
        Board board = new Board("board1", "code1", "readOnlyCode1", "", "", null, 0);
        board.setId(1L);
        CardList list = new CardList("My list", board);
        list.setId(1L);

        when(cardListServiceMock.getOne(1L)).thenReturn(Optional.of(list));
        when(boardServiceMock.getOne(1L)).thenReturn(Optional.of(board));

        cardListControllerMock.deleteOne(1L);

        verify(cardListServiceMock).deleteOne(1L);
    }

    @Test
    public void deleteOneException() {
        Long invalidId = 12345L;
        doThrow(EntityNotFoundException.class).when(cardListServiceMock).deleteOne(invalidId);
        var result = cardListControllerMock.deleteOne(invalidId);
        assertTrue(result.getStatusCode().isError());
    }

    @Test
    void updateOne() {
        CardList cardList = new CardList();
        cardList.setId(
                1L); //setting the id and title manually because otherwise all constructors require a Board
        cardList.setTitle("title1");

        CardList updatedCardList = new CardList();
        updatedCardList.setTitle("title2");

        when(cardListServiceMock.updateOne(1L, cardList)).thenReturn(updatedCardList);

        CardList returnedCardList = cardListControllerMock.updateOne(1L, cardList).getBody();

        assertEquals(updatedCardList, returnedCardList);

    }

    @Test
    public void updateOneException() {
        Long invalidId = 123L;
        CardList cardList = new CardList();
        when(cardListServiceMock.updateOne(invalidId, cardList)).thenThrow(
                new EntityNotFoundException());

        ResponseEntity<CardList> result = cardListControllerMock.updateOne(invalidId, cardList);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void testRefreshList() {

        CardList cardList = new CardList();
        cardList.setId(1L);

        when(cardListServiceMock.getOne(1L)).thenReturn(Optional.of(cardList));

        ResponseEntity<CardList> response = cardListControllerMock.refreshList(1L);

        verify(cardListServiceMock, times(2)).getOne(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(cardList, response.getBody());
    }
}
