package server.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Card;
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
        Card card = new Card("Card name", "Desc", "Bla", null);
        when(cardServiceMock.createOne(card)).thenReturn(card);

        Card returnedCard = cardControllerMock.createOne(card).getBody();
        assertEquals(card, returnedCard);

    }

    //Test whether deleteById() method of the repository was called for the correct card
    @Test
    void deleteOne() {

        CardList list = new CardList("List", null);
        Card c = new Card("Title", "Desc", "Red", null);
        c.setId(1L);
        list.addCard(c);

        when(cardServiceMock.getOne(1L)).thenReturn(Optional.of(c));

        cardControllerMock.deleteOne(1L);

        verify(cardServiceMock).deleteOne(1L);

    }

    @Test
    void updateOne() {
        Card card = new Card();
        card.setId(1L); //setting the id and title manually because otherwise all constructors require a Board
        card.setTitle("title1");

        Card updatedCard = new Card();
        updatedCard.setTitle("title2");

        when(cardServiceMock.updateOne(1L, card)).thenReturn(updatedCard);

        Card returnedCard = cardControllerMock.updateOne(1L, card).getBody();
        assertEquals(updatedCard, returnedCard);
    }

    @Test
    void sendList() {

    }

    @Test
    void moveToListAfterCard() {

    }

    @Test
    void moveToListLast() {

    }

    @Test
    void addTagToCard() {

    }

    @Test
    void removeTagFromCard() {

    }
}
