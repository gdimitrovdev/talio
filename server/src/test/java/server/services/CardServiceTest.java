package server.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
import server.database.CardListRepository;
import server.database.CardRepository;
import server.database.TagRepository;

@RunWith(MockitoJUnitRunner.class)
class CardServiceTest {
    @Mock
    private CardRepository cardRepositoryMock;

    @Mock
    private CardListRepository cardListRepositoryMock;

    @Mock
    private TagRepository tagRepositoryMock;

    @InjectMocks
    private CardService cardService;

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

        List<Card> returnedCards = cardService.getMany();
        assertEquals(card1, returnedCards.get(0));
        assertEquals(card2, returnedCards.get(1));

    }

    @Test
    void getOne() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.of(card));

        Optional<Card> returnedCard = cardService.getOne(cardId);
        assertEquals(card, returnedCard.get());
    }

    @Test
    void createOne() {
        CardList list = new CardList();
        list.setId(1L);

        Card card = new Card();
        card.setId(1L);
        card.setList(list);

        when(cardListRepositoryMock.findById(1L)).thenReturn(Optional.of(list));
        when(cardRepositoryMock.saveAndFlush(card)).thenReturn(card);
        when(cardRepositoryMock.save(card)).thenReturn(card);

        Card returnedCard = cardService.createOne(card);

        assertEquals(card, returnedCard);
    }

    @Test
    void testCreateOneElse() {

        CardList list = new CardList();
        list.setId(1L);

        Card existingCard = new Card();
        existingCard.setId(1L);
        existingCard.setListPriority(0L);
        existingCard.setList(list);

        list.addCard(existingCard);

        Card newCard = new Card();
        newCard.setList(list);
        list.addCard(newCard);

        when(cardListRepositoryMock.findById(1L)).thenReturn(Optional.of(list));
        when(cardRepositoryMock.saveAndFlush(any(Card.class))).thenReturn(newCard);
        when(cardRepositoryMock.save(any(Card.class))).thenReturn(newCard);
        Card createdCard = cardService.createOne(newCard);
        assertEquals(createdCard.getListPriority(), 1L);
    }

    @Test
    void testDeleteOne() {
        Long id = 1L;
        Card card = new Card();
        card.setId(id);

        when(cardRepositoryMock.existsById(id)).thenReturn(true);
        when(cardRepositoryMock.findById(id)).thenReturn(Optional.of(card));

        cardService.deleteOne(id);

        verify(cardRepositoryMock).deleteById(id);
        verify(cardRepositoryMock).findById(id);
    }

    @Test
    void updateOne() {
        Card card = new Card();
        card.setId(
                1L); //setting the id and title manually because otherwise all constructors require a Board
        card.setTitle("title1");

        when(cardRepositoryMock.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepositoryMock.save(card)).thenReturn(card);

        Card updatedCard = new Card();
        updatedCard.setTitle("title2");
        Card returnedCard = cardService.updateOne(1L, updatedCard);
        assertEquals(updatedCard.getTitle(), returnedCard.getTitle());
    }

    /*
    @Test
    void testMoveToListLast() {
        Card card = new Card();
        card.setId(1L);
        card.setListPriority(0L);

        CardList list = new CardList();
        list.setId(1L);
        list.setCards(new ArrayList<>());

        when(cardRepositoryMock.getReferenceById(1L)).thenReturn(card);
        when(cardListRepositoryMock.findById(1L)).thenReturn(Optional.of(list));
        when(cardRepositoryMock.saveAndFlush(any(Card.class))).thenReturn(card);

        CardList result = cardService.moveToListLast(1L, 1L);

        assertEquals(result.getId(), 1L);
        assertEquals(result.getCards().size(), 1);
        assertEquals(result.getCards().get(0).getId(), 1L);

        assertEquals(result.getCards().get(0).getListPriority(), 1L);
    }
    */
    /*
    @Test
    void testMoveToListLastElse() throws EntityNotFoundException {
        Long cardId = 1L;
        Long listId = 1L;

        Card card = new Card();
        card.setId(cardId);
        card.setListPriority(0L);

        CardList list = new CardList();
        list.setId(listId);

        when(cardRepositoryMock.getReferenceById(cardId)).thenReturn(card);
        when(cardListRepositoryMock.getReferenceById(listId)).thenReturn(list);
        when(cardListRepositoryMock.findById(listId)).thenReturn(Optional.of(list));
        when(cardRepositoryMock.save(card)).thenReturn(card);

        Card movedCard = cardServiceMock.moveToListLast(cardId, listId);
        assertEquals(0L, movedCard.getListPriority());
        assertEquals(list, movedCard.getList());
        assertEquals(1, list.getCards().size());
        assertEquals(card, list.getCards().get(0));
    }
     */

    @Test
    void testMoveToListLastException() throws EntityNotFoundException {
        Long cardId = 1L;
        Long listId = 1L;

        when(cardRepositoryMock.getReferenceById(cardId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> {
            cardService.moveToListLast(cardId, listId);
        });
    }

    @Test
    void testAddTagToCard() {
        Long tagId = 1L;
        Long cardId = 2L;

        Tag tag = new Tag();
        tag.setId(tagId);
        Card card = new Card();
        card.setId(cardId);

        when(tagRepositoryMock.findById(tagId)).thenReturn(Optional.of(tag));
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepositoryMock.save(any(Card.class))).thenReturn(card);

        Card result = cardService.addTagToCard(tagId, cardId);

        assertEquals(1, result.getTags().size());
        assertEquals(tag, result.getTags().get(0));

        verify(tagRepositoryMock).findById(tagId);
        verify(cardRepositoryMock, times(2)).findById(cardId);
        verify(cardRepositoryMock).save(card);
    }

    @Test
    void testAddTagToCardException() {
        Long tagId = 1L;
        Long cardId = 2L;

        Tag tag = new Tag();
        tag.setId(tagId);
        Card card = new Card();
        card.setId(cardId);

        when(tagRepositoryMock.findById(tagId)).thenReturn(Optional.of(tag));
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepositoryMock.save(any(Card.class))).thenThrow(new RuntimeException());

        assertThrows(EntityNotFoundException.class, () -> {
            cardService.addTagToCard(tagId, cardId);
        });

        verify(tagRepositoryMock, times(1)).findById(tagId);
        verify(cardRepositoryMock, times(1)).findById(cardId);
        verify(cardRepositoryMock, times(1)).save(card);

    }

    @Test
    void testRemoveTagFromCard() {
        Long tagId = 1L;
        Long cardId = 2L;

        Tag tag = new Tag();
        tag.setId(tagId);
        Card card = new Card();
        card.setId(cardId);
        card.getTags().add(tag);
        ;

        when(tagRepositoryMock.findById(tagId)).thenReturn(Optional.of(tag));
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepositoryMock.save(card)).thenReturn(card);


        Card result = cardService.removeTagFromCard(tagId, cardId);

        assertEquals(0, result.getTags().size());
        verify(cardRepositoryMock, times(2)).findById(cardId);
        verify(tagRepositoryMock).findById(tagId);
        verify(cardRepositoryMock).save(card);
    }

    @Test
    void testRemoveTagFromCardException() {
        Long tagId = 1L;
        Long cardId = 2L;

        Tag tag = new Tag();
        tag.setId(tagId);
        Card card = new Card();
        card.setId(cardId);
        card.getTags().add(tag);

        when(tagRepositoryMock.findById(tagId)).thenReturn(Optional.of(tag));
        when(cardRepositoryMock.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepositoryMock.save(any(Card.class))).thenThrow(new RuntimeException());

        assertThrows(EntityNotFoundException.class, () -> {
            cardService.removeTagFromCard(tagId, cardId);
        });

        verify(tagRepositoryMock, times(1)).findById(tagId);
        verify(cardRepositoryMock, times(1)).findById(cardId);
        verify(cardRepositoryMock, times(1)).save(card);

    }

    @Test
    void testDeleteMany() {
        Card card1 = new Card();
        Card card2 = new Card();

        List<Card> cards = new ArrayList<>();

        cardRepositoryMock.saveAll(List.of(card1, card2));

        when(cardRepositoryMock.findAll()).thenReturn(cards);

        cardService.deleteMany();

        verify(cardRepositoryMock).deleteAll();

        assertEquals(0, cardRepositoryMock.count());
    }
}
