package server.controllers;

import commons.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CardService;

import java.util.List;

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
        return cardService.getOne(id);
    }

    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Card> createOne(@RequestBody Card card) {
        return cardService.createOne(card);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        cardService.deleteOne(id);
    }

    @PutMapping("/{id}")
    public Card update(@PathVariable Long id, @RequestBody Card card) {
        card.setId(id);
        return cardService.update(card);
    }

}
