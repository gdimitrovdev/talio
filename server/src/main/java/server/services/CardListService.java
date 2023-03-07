package server.services;

import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.CardListRepository;

import java.util.List;

@Service
public class CardListService {

  private CardListRepository cardListRepository;

    public CardListService(CardListRepository cardListRepository) {
        this.cardListRepository = cardListRepository;
    }

    public List<CardList> getMany() {
        return cardListRepository.findAll();
    }

    public ResponseEntity<CardList> getOne(Long id) {
        if(id < 0 || !cardListRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(cardListRepository.findById(id).get());
    }

    public ResponseEntity<CardList> createOne(CardList cardList) {
        CardList newCardList = cardListRepository.save(cardList);
        return ResponseEntity.ok(newCardList);
    }

    public void deleteOne(Long id) {
        cardListRepository.deleteById(id);
    }

}
