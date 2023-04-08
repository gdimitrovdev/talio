package server.services;

import commons.Card;
import commons.CardList;
import java.util.ArrayList;
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
     * Ignores tags and priority and places card at the end of its list
     *
     * @param card
     * @return
     */
    public Card createOne(Card card) {
        card.setTags(new ArrayList<>());
        card.setList(cardListRepository.findById(card.getList().getId()).get());
        card.setListPriority(0L);
        Card newCard = cardRepository.saveAndFlush(card);
        CardList list = cardListRepository.findById(card.getList().getId()).get();
        if (list.getCards().size() != 0) {
            newCard.setListPriority(
                    list.getCards().stream().max(Comparator.comparing(Card::getListPriority)).get()
                            .getListPriority() + 1L
            );
        }
        newCard = cardRepository.save(newCard);
        return newCard;
    }

    /**
     * Also removes card from its list
     *
     * @param id
     */
    public void deleteOne(Long id) {
        if (cardRepository.existsById(id)) {
            removeCardFromItsList(id);
            cardRepository.deleteById(id);
        }
    }

    private Card removeCardFromItsList(Long cardId) {
        try {
            var card = cardRepository.findById(cardId).get();
            if (card.getList() == null) {
                return card;
            }
            var list = card.getList();
            list.removeCard(card);
            cardRepository.save(card);
            list.getCards().stream().filter(c -> c.getListPriority() > card.getListPriority())
                    .forEach(c -> {
                        c.setListPriority(c.getListPriority() - 1);
                        cardRepository.saveAndFlush(c);
                    });
            return card;
        } catch (Exception e) {
            throw new EntityNotFoundException(
                    "Card or list not found while removing a card from a list");
        }
    }

    public Card moveToListLast(Long cardId, Long listId) {
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
                list.addCard(card);
                cardRepository.saveAndFlush(card);
                return card;
            }
        } catch (Exception e) {
            throw new EntityNotFoundException(
                    "Card or list not found while moving a card to the last place in a list");
        }
    }

    public Card moveToListAfterCard(Long cardId, Long listId, Long afterCardId) {
        try {
            var card = removeCardFromItsList(cardId);
            var list = cardListRepository.findById(listId).get();
            var afterCard = cardRepository.findById(afterCardId).get();
            card.setListPriority(afterCard.getListPriority() + 1);
            list.getCards().stream().filter(c -> c.getListPriority() > afterCard.getListPriority())
                    .forEach(c -> {
                        c.setListPriority(c.getListPriority() + 1);
                        cardRepository.saveAndFlush(c);
                    });
            list.addCard(card);
            card.setListPriority(afterCard.getListPriority() + 1L);
            /*for (Card cardOfList : list.getCards()) {
                if (cardOfList.getListPriority() > afterCard.getListPriority()
                        && !cardOfList.getId().equals(card.getId())) {
                    cardOfList.setListPriority(cardOfList.getListPriority() + 1L);
                    cardRepository.save(cardOfList);
                }
            }*/
            cardRepository.saveAndFlush(card);
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
    public Card updateOne(Long id, Card card) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        existingCard.setTitle(card.getTitle());
        existingCard.setColorPresetNumber(card.getColorPresetNumber());
        existingCard.setDescription(card.getDescription());

        return cardRepository.save(existingCard);
    }

    public Card addTagToCard(Long tagId, Long cardId) {
        try {
            var tag = tagRepository.findById(tagId).get();
            var card = cardRepository.findById(cardId).get();
            card.getTags().add(tag);
            cardRepository.save(card);
            return cardRepository.findById(cardId).get();
        } catch (Exception e) {
            throw new EntityNotFoundException("Error while adding tag to card");
        }
    }

    public Card removeTagFromCard(Long tagId, Long cardId) {
        try {
            var tag = tagRepository.findById(tagId).get();
            var card = cardRepository.findById(cardId).get();
            card.getTags().remove(tag);
            cardRepository.save(card);
            return cardRepository.findById(cardId).get();
        } catch (Exception e) {
            throw new EntityNotFoundException("Error while removing tag from card");
        }
    }

    public void deleteMany() {
        cardRepository.deleteAll();
    }
}
