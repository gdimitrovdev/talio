package server.controllers;

import commons.Card;
import commons.CardList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.services.CardListService;
import server.services.CardService;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    private SimpMessagingTemplate template;

    private final CardListService cardListService;
    private final CardService cardService;

    public CardController(CardListService cardListService, CardService cardService) {
        this.cardListService = cardListService;
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
        try {
            Card newCard = cardService.createOne(card);
            template.convertAndSend("topics/cards", newCard);
            return ResponseEntity.ok(newCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CardList> deleteOne(@PathVariable("id") Long id) {
        try {
            var cardListId = cardService.getOne(id).get().getList().getId();
            cardService.deleteOne(id);
            var cardList = cardListService.getOne(cardListId).get();
            template.convertAndSend("/topic/lists", cardList);
            return ResponseEntity.ok(cardList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Card> updateOne(@PathVariable Long id,
            @RequestBody Card card) {
        try {
            Card updatedCard = cardService.updateOne(id, card);
            template.convertAndSend("/topic/cards", updatedCard);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
