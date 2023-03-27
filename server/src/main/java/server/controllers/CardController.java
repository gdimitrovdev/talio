package server.controllers;

import commons.Card;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.services.CardService;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping(path = {"", "/"})
    @ResponseBody
    public List<Card> getMany() {
        return cardService.getMany();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Card> getOne(@PathVariable("id") Long id) {
        Optional<Card> optionalCard = cardService.getOne(id);
        if (!optionalCard.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(optionalCard.get());
    }

    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Card> createOne(@RequestBody Card card) {
        System.out.println(card);
        return ResponseEntity.ok(cardService.createOne(card));
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        cardService.deleteOne(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Card> updateOne(@PathVariable Long id, @RequestBody Card card) {
        try {
            Card updatedCard = cardService.updateOne(id, card);
            return ResponseEntity.ok(updatedCard);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
