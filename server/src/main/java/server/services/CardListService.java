package server.services;

import commons.CardList;
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

    public List<CardList> getMany() {
        return cardListRepository.findAll();
    }

    public Optional<CardList> getOne(Long id) {
        return cardListRepository.findById(id);
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

    public CardList updateOne(Long id, CardList cardList) throws EntityNotFoundException {
        CardList existingList = cardListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("List not found"));

        existingList.setTitle(cardList.getTitle());
        existingList.setBoard(cardList.getBoard());

        return cardListRepository.save(existingList);
    }

}
