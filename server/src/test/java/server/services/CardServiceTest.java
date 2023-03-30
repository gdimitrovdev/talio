package server.services;

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
import server.database.CardRepository;

@RunWith(MockitoJUnitRunner.class)
class CardServiceTest {
    @Mock
    private CardRepository cardRepositoryMock;

    @InjectMocks
    private CardService cardServiceMock;

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

        when(cardRepositoryMock.findAll()).thenReturn(cards);

        List<Card> returnedCards = cardServiceMock.getMany();
        assertEquals(card1, returnedCards.get(0));
        assertEquals(card2, returnedCards.get(1));

    }

    @Test
    void getOne() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.of(card));

        Optional<Card> returnedCard = cardServiceMock.getOne(cardId);
        assertEquals(card, returnedCard.get());
    }

    @Test
    void createOne() {
        Card card = new Card();

        when(cardRepositoryMock.save(card)).thenReturn(card);

        Card returnedCard = cardServiceMock.createOne(card);
        assertEquals(card, returnedCard);

    }

    // Test whether deleteById() method of the repository was called for the correct card
    @Test
    void deleteOne() {
        Card card = new Card();
        card.setId(1L);
        when(cardRepositoryMock.existsById(1L)).thenReturn(true);

        cardServiceMock.deleteOne(1L);

        verify(cardRepositoryMock).deleteById(1L);



    }

    @Test
    void updateOne() {
        Card card = new Card();
        card.setId(1L); //setting the id and title manually because otherwise all constructors require a Board
        card.setTitle("title1");

        when(cardRepositoryMock.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepositoryMock.save(card)).thenReturn(card);

        Card updatedCard = new Card();
        updatedCard.setTitle("title2");
        Card returnedCard = cardServiceMock.updateOne(1L, updatedCard);
        assertEquals(updatedCard.getTitle(), returnedCard.getTitle());
    }
}
