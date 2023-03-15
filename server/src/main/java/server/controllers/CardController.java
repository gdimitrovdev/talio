package server.controllers;

import commons.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CardService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping(path = { "", "/" })
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

    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Card> createOne(@RequestBody Card card) {
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
