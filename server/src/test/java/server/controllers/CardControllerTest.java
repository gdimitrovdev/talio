package server.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Card;
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
import server.services.CardService;

@RunWith(MockitoJUnitRunner.class)
class CardControllerTest {
    @Mock
    private CardService cardServiceMock;

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
        Card card = new Card();

        when(cardServiceMock.createOne(card)).thenReturn(card);

        Card returnedCard = cardControllerMock.createOne(card).getBody();
        assertEquals(card, returnedCard);

    }

    //Test whether deleteById() method of the repository was called for the correct card
    @Test
    void deleteOne() {
        Card card = new Card();
        card.setId(1L);

        cardControllerMock.deleteOne(1L);

        verify(cardServiceMock).deleteOne(1L);

    }

    @Test
    void updateOne() {
        Card card = new Card();
        card.setId(1L); //setting the id and title manually because otherwise all constructors require a Board
        card.setTitle("title1");

        when(cardServiceMock.updateOne(1L, card)).thenReturn(card);

        Card updatedCard = new Card();
        updatedCard.setTitle("title2");
        Card returnedCard = cardControllerMock.updateOne(1L, updatedCard).getBody();
        assertEquals(updatedCard.getTitle(), returnedCard.getTitle());
    }
}
