package server.services;

import commons.Card;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.CardListRepository;
import server.database.CardRepository;
import server.database.TagRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TagRepository tagRepository;
    private final CardListRepository cardListRepository;

    public CardService(CardRepository cardRepository, TagRepository tagRepository,
            CardListRepository cardListRepository) {
        this.cardRepository = cardRepository;
        this.tagRepository = tagRepository;
        this.cardListRepository = cardListRepository;
    }

    public List<Card> getMany() {
        return cardRepository.findAll();
    }

    public Optional<Card> getOne(Long id) {
        return cardRepository.findById(id);
    }

    /**
     * Ignores tags and lists
     *
     * @param card
     * @return
     */
    public Card createOne(Card card) {
        card.setList(null);
        card.getTags().clear();
        Card newCard = cardRepository.save(card);
        return newCard;
    }

    public void deleteOne(Long id) {
        if (cardRepository.existsById(id)) {
            removeCardFromItsList(id);
            cardRepository.deleteById(id);
        }
    }

    private Card removeCardFromItsList(Long cardId) throws EntityNotFoundException {
        try {
            var card = cardRepository.getReferenceById(cardId);
            if (card.getList() == null) {
                return card;
            }
            var list = card.getList();
            list.getCards().stream().filter(c -> c.getListPriority() > card.getListPriority())
                    .forEach(c -> {
                        c.setListPriority(c.getListPriority() - 1);
                        cardRepository.save(c);
                    });
            card.setList(null);
            card.setListPriority(-1L);
            cardRepository.save(card);
            return card;
        } catch (Exception e) {
            throw new EntityNotFoundException(
                    "Card or list not found while removing a card from a list");
        }
    }

    public Card moveToListLast(Long cardId, Long listId) throws EntityNotFoundException {
        try {
            var card = cardRepository.getReferenceById(cardId);
            var list = cardListRepository.getReferenceById(listId);
            if (list.getCards().size() >= 1) {
                Card lastCard =
                        list.getCards().stream().max(Comparator.comparing(Card::getListPriority))
                                .get();
                return moveToListAfterCard(cardId, listId, lastCard.getId());
            } else {
                card = removeCardFromItsList(card.getId());
                card.setListPriority(0L);
                card.setList(list);
                cardRepository.save(card);
                return card;
            }
        } catch (Exception e) {
            throw new EntityNotFoundException(
                    "Card or list not found while moving a card to the last place in a list");
        }
    }

    public Card moveToListAfterCard(Long cardId, Long listId, Long afterCardId)
            throws EntityNotFoundException {
        try {
            var card = removeCardFromItsList(cardId);
            var list = cardListRepository.getReferenceById(listId);
            var afterCard = cardRepository.getReferenceById(afterCardId);
            card.setListPriority(afterCard.getListPriority() + 1);
            list.getCards().stream().filter(c -> c.getListPriority() >= card.getListPriority())
                    .forEach(c -> {
                        c.setListPriority(c.getListPriority() + 1);
                        cardRepository.save(c);
                    });
            card.setList(list);
            cardRepository.save(card);
            return card;
        } catch (Exception e) {
            throw new EntityNotFoundException(
                    "Card or list not found while moving a card to a list after another card");
        }
    }

    /**
     * Ignores a card's list. If you want to change that use moveToListAfterCard and moveToListLast.
     * Also ignores a card's tags. If you want to change that use addTag and removeTag.
     *
     * @param id
     * @param card
     * @return
     * @throws EntityNotFoundException
     */
    public Card updateOne(Long id, Card card) throws EntityNotFoundException {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        existingCard.setTitle(card.getTitle());
        existingCard.setColor(card.getColor());
        existingCard.setDescription(card.getDescription());

        return cardRepository.save(existingCard);
    }

    public Card addTagToCard(Long tagId, Long cardId) {
        try {
            var tag = tagRepository.getReferenceById(tagId);
            var card = cardRepository.getReferenceById(cardId);
            card.getTags().add(tag);
            cardRepository.save(card);
            return cardRepository.getReferenceById(cardId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Error while adding tag to card");
        }
    }

    public Card removeTagFromCard(Long tagId, Long cardId) {
        try {
            var tag = tagRepository.getReferenceById(tagId);
            var card = cardRepository.getReferenceById(cardId);
            card.getTags().remove(tag);
            cardRepository.save(card);
            return cardRepository.getReferenceById(cardId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Error while removing tag from card");
        }
    }
}
