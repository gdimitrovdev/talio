package server.controllers;

import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import server.services.CardListService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lists")
public class CardListController {

    private final CardListService cardListService;

    public CardListController(CardListService cardListService) {
        this.cardListService = cardListService;
    }

    @GetMapping(path = { "", "/" })
    @ResponseBody
    public List<CardList> getMany() {
        return cardListService.getMany();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CardList> getOne(@PathVariable("id") Long id) {
        Optional<CardList> optionalCardList = cardListService.getOne(id);
        if (!optionalCardList.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(optionalCardList.get());
    }

    @MessageMapping("/lists")
    @SendTo("/topic/lists")
    public CardList addMessage(CardList cardList) {
        createOne(cardList);
        return cardList;
    }

    @PostMapping(path= { "", "/" })
    @ResponseBody
    public ResponseEntity<CardList> createOne(@RequestBody CardList cardList) {
        return ResponseEntity.ok(cardListService.createOne(cardList));
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        cardListService.deleteOne(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardList> updateOne(@PathVariable Long id, @RequestBody CardList cardList) {
        try {
            CardList updatedCardList = cardListService.updateOne(id, cardList);
            return ResponseEntity.ok(updatedCardList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
