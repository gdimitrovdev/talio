package server.services;

import commons.Card;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.CardRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> getMany() {
        return cardRepository.findAll();
    }

    public Optional<Card> getOne(Long id) {
        return cardRepository.findById(id);
    }

    public Card createOne(Card card) {
        Card newCard = cardRepository.save(card);
        return newCard;
    }

    public void deleteOne(Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
        }
    }

    public Card updateOne(Long id, Card card) throws EntityNotFoundException {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        existingCard.setTitle(card.getTitle());
        existingCard.setColor(card.getColor());
        existingCard.setDescription(card.getDescription());
        existingCard.setList(card.getList());
        existingCard.setTags(card.getTags());
        existingCard.setSubtasks(card.getSubtasks());

        return cardRepository.save(existingCard);
    }
}
