package server.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Card;
import commons.CardList;
import commons.Tag;
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
import server.services.CardListService;
import server.services.CardService;

@RunWith(MockitoJUnitRunner.class)
class CardControllerTest {
    @Mock
    private CardService cardServiceMock;

    @Mock
    private CardListService cardListServiceMock;

    @Mock
    private SimpMessagingTemplate templateMock;

    @InjectMocks
    private CardController cardControllerMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMany() {
        List<Card> cards = new ArrayList<>();
        Card card1 = new Card();
        Card card2 = new Card();
        cards.add(card1);
        cards.add(card2);

        when(cardServiceMock.getMany()).thenReturn(cards);

        List<Card> returnedCards = cardControllerMock.getMany();
        assertEquals(card1, returnedCards.get(0));
        assertEquals(card2, returnedCards.get(1));

    }

    @Test
    void getOne() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        when(cardServiceMock.getOne(cardId)).thenReturn(Optional.of(card));

        Card returnedCard = cardControllerMock.getOne(cardId).getBody();
        assertEquals(card, returnedCard);
    }

    @Test
    void createOne() {
        Card card = new Card("Card name", "Desc", null, 0);
        when(cardServiceMock.createOne(card)).thenReturn(card);

        Card returnedCard = cardControllerMock.createOne(card).getBody();
        assertEquals(card, returnedCard);

    }

    @Test
    public void createOneException() {
        Card card = new Card();
        doThrow(new RuntimeException()).when(cardServiceMock).createOne(card);

        ResponseEntity<Card> response = cardControllerMock.createOne(card);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    //Test whether deleteById() method of the repository was called for the correct card
    @Test
    void deleteOne() {

        CardList list = new CardList("List", null);
        Card c = new Card("Title", "Desc", null, 1);
        c.setId(1L);
        list.addCard(c);

        when(cardServiceMock.getOne(1L)).thenReturn(Optional.of(c));

        cardControllerMock.deleteOne(1L);

        verify(cardServiceMock).deleteOne(1L);

    }

    @Test
    void updateOne() {
        Card card = new Card();
        card.setId(
                1L); //setting the id and title manually because otherwise all constructors require a Board
        card.setTitle("title1");

        Card updatedCard = new Card();
        updatedCard.setTitle("title2");

        when(cardServiceMock.updateOne(1L, card)).thenReturn(updatedCard);

        Card returnedCard = cardControllerMock.updateOne(1L, card).getBody();
        assertEquals(updatedCard, returnedCard);
    }

    @Test
    public void updateOneException() {
        Long invalidId = 123L;
        Card card = new Card();
        when(cardServiceMock.updateOne(invalidId, card)).thenThrow(new EntityNotFoundException());

        ResponseEntity<Card> result = cardControllerMock.updateOne(invalidId, card);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void moveToListAfterCard() {
        Card card = new Card("My card", "Bla", null, 1),
                after = new Card("Another card", "Bli", null, 1);

        card.setId(1L);
        after.setId(2L);

        CardList list1 = new CardList("First list", null),
                list2 = new CardList("Second list", null);

        list1.setId(1L);
        list2.setId(2L);

        list1.addCard(card);
        list2.addCard(after);

        when(cardServiceMock.moveToListAfterCard(1L, 2L, 2L)).thenReturn(list2);

        assertEquals(list2, cardControllerMock.moveToListAfterCard(1L, 2L, 2L).getBody());
    }

    @Test
    public void moveToListAfterCardException() {
        Long invalidId = 123L;
        Long invalidListId = 124L;
        Long invalidAfterCardId = 125L;
        Card card = new Card();
        when(cardServiceMock.moveToListAfterCard(invalidId, invalidListId,
                invalidAfterCardId)).thenThrow(new RuntimeException());

        ResponseEntity<CardList> result =
                cardControllerMock.moveToListAfterCard(invalidId, invalidListId,
                        invalidAfterCardId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void moveToListLast() {
        Card card = new Card("My card", "Bla", null, 1);

        card.setId(1L);

        CardList list1 = new CardList("First list", null),
                list2 = new CardList("Second list", null);

        list1.setId(1L);
        list2.setId(2L);

        list1.addCard(card);

        when(cardServiceMock.moveToListLast(1L, 2L)).thenReturn(list2);

        assertEquals(list2, cardControllerMock.moveToListLast(1L, 2L).getBody());
    }

    @Test
    public void moveToListLastException() {
        Long invalidId = 123L;
        Long invalidListId = 124L;
        Card card = new Card();
        when(cardServiceMock.moveToListLast(invalidId, invalidListId)).thenThrow(
                new RuntimeException());

        ResponseEntity<CardList> result =
                cardControllerMock.moveToListLast(invalidId, invalidListId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void addTagToCard() {
        Card withoutTag = new Card("My card", "Bla", null, 1),
                withTag = new Card("My card 2", "Bla 2", null, 1);

        withTag.setId(1L);
        withoutTag.setId(1L);

        Tag tag = new Tag("Name", "Red", null);
        tag.setId(1L);

        withTag.addTag(tag);

        when(cardServiceMock.addTagToCard(1L, 1L)).thenReturn(withTag);
        assertEquals(withTag, cardControllerMock.addTagToCard(1L, 1L).getBody());
    }

    @Test
    public void addTagToCardException() {
        Long invalidId = 123L;
        Long invalidTagId = 124L;

        when(cardServiceMock.addTagToCard(invalidTagId, invalidId)).thenThrow(
                new EntityNotFoundException());

        ResponseEntity<Card> result = cardControllerMock.addTagToCard(invalidId, invalidTagId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void removeTagFromCard() {
        Card withoutTag = new Card("My card", "Bla", null, 1),
                withTag = new Card("My card 2", "Bla 2", null, 1);

        withTag.setId(1L);
        withoutTag.setId(1L);

        Tag tag = new Tag("Name", "Red", null);
        tag.setId(1L);

        withTag.addTag(tag);

        when(cardServiceMock.removeTagFromCard(1L, 1L)).thenReturn(withoutTag);
        assertEquals(withoutTag, cardControllerMock.removeTagFromCard(1L, 1L).getBody());
    }

    @Test
    public void removeTagFromCardException() {
        Long invalidId = 123L;
        Long invalidTagId = 124L;

        when(cardServiceMock.removeTagFromCard(invalidTagId, invalidId)).thenThrow(
                new EntityNotFoundException());

        ResponseEntity<Card> result = cardControllerMock.removeTagFromCard(invalidId, invalidTagId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}
