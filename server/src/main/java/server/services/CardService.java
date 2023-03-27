package server.services;

import commons.Card;
<<<<<<< HEAD
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.CardRepository;
=======
import commons.Tag;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import server.database.CardRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import server.database.TagRepository;
>>>>>>> 68daa1c (Established a way to do every possible update on the server and started establishing the client-side receiving of updates. Ref #54)

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TagRepository tagRepository;

    public CardService(CardRepository cardRepository, TagRepository tagRepository) {
        this.cardRepository = cardRepository;
        this.tagRepository = tagRepository;
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

        var tags = new ArrayList<Tag>();
        for(Tag tag : card.getTags()) {
            tags.add(tagRepository.getById(tag.getId()));
        }
        existingCard.setTags(tags);

        return cardRepository.save(existingCard);
    }
}
