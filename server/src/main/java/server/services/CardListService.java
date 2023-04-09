package server.services;

import commons.Card;
import commons.CardList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.CardListRepository;

@Service
public class CardListService {

    private final CardListRepository cardListRepository;
    private final BoardRepository boardRepository;

    public CardListService(CardListRepository cardListRepository, BoardRepository boardRepository) {
        this.cardListRepository = cardListRepository;
        this.boardRepository = boardRepository;
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

    /**
     * Ignores the list's list of cards
     *
     * @param cardList
     * @return
     */
    public CardList createOne(CardList cardList) {
        cardList.setCards(new ArrayList<>());
        cardList.setBoard(boardRepository.findById(cardList.getBoard().getId())
                .orElseThrow(() -> new EntityNotFoundException("Board not found")));
        return cardListRepository.save(cardList);
    }

    public void deleteOne(Long id) {
        if (cardListRepository.existsById(id)) {
            cardListRepository.deleteById(id);
        }
    }

    /**
     * Ignores board and list of cards
     *
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
