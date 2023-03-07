package server.services;

import commons.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.CardRepository;

import java.util.List;

@Service
public class CardService {

    private CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> getMany() {
        return cardRepository.findAll();
    }

    public ResponseEntity<Card> getOne(Long id) {
        if (id < 0 || !cardRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(cardRepository.findById(id).get());
    }

    public ResponseEntity<Card> createOne(Card card) {
        Card newCard = cardRepository.save(card);
        return ResponseEntity.ok(newCard);
    }

    public void deleteOne(Long id) {
        cardRepository.deleteById(id);
    }

}
