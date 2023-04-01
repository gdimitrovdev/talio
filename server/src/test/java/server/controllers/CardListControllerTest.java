package server.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import server.services.CardListService;

@RunWith(MockitoJUnitRunner.class)
class CardListControllerTest {

    // @Mock annotation is used to create a mock BoardRepository object
    @Mock
    private CardListService cardListServiceMock;

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

    // Test whether deleteById() method of the repository was called for the correct cardList
    @Test
    void deleteOne() {

        cardListControllerMock.deleteOne(1L);

        verify(cardListServiceMock).deleteOne(1L);
    }

    @Test
    void updateOne() {
        CardList cardList = new CardList();
        cardList.setId(1L); //setting the id and title manually because otherwise all constructors require a Board
        cardList.setTitle("title1");

        when(cardListServiceMock.getOne(1L)).thenReturn(Optional.of(cardList));
        when(cardListServiceMock.createOne(cardList)).thenReturn(cardList);

        CardList updatedCardList = new CardList();
        updatedCardList.setTitle("title2");
        CardList returnedCardList = cardListControllerMock.updateOne(1L, updatedCardList).getBody();
        assertEquals(updatedCardList.getTitle(), returnedCardList.getTitle());

    }
}
