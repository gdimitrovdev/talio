package server.services;

import commons.Card;
import commons.CardList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.CardListRepository;

@Service
public class CardListService {

    private final CardListRepository cardListRepository;

    public CardListService(CardListRepository cardListRepository) {
        this.cardListRepository = cardListRepository;
    }

    // TODO make sure this orders the cards like getOne does
    public List<CardList> getMany() {
        return cardListRepository.findAll();
    }

    public Optional<CardList> getOne(Long id) {
        if (!cardListRepository.existsById(id)) {
            return Optional.empty();
        }
        CardList list = cardListRepository.findById(id).get();
        list.setCards(list.getCards().stream().sorted(Comparator.comparing(Card::getListPriority))
                .toList());
        return Optional.of(list);
    }

    public CardList createOne(CardList cardList) {
        CardList newCardList = cardListRepository.save(cardList);
        return newCardList;
    }

    public void deleteOne(Long id) {
        if (cardListRepository.existsById(id)) {
            cardListRepository.deleteById(id);
        }
    }

    /**
     * Ignores board, because you should not be able to change that
     * @param id
     * @param cardList
     * @return
     * @throws EntityNotFoundException
     */
    public CardList updateOne(Long id, CardList cardList) throws EntityNotFoundException {
        CardList existingList = cardListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("List not found"));

        existingList.setTitle(cardList.getTitle());

        return cardListRepository.save(existingList);
    }

    public void deleteMany() {
        cardListRepository.deleteAll();
    }
}
