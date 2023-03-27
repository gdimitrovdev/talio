package server.controllers;

import commons.CardList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.services.CardListService;

@RestController
@RequestMapping("/api/lists")
public class CardListController {
    @Autowired
    private SimpMessagingTemplate template;


    private final CardListService cardListService;

    public CardListController(CardListService cardListService) {
        this.cardListService = cardListService;
    }

    @GetMapping(path = {"", "/"})
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
        System.out.println("/lists/lists received " + cardList);
        return cardList;
    }

    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<CardList> createOne(@RequestBody CardList cardList) {
        System.out.println(cardList);
        template.convertAndSend("/topic/lists", cardList);
        return ResponseEntity.ok(cardListService.createOne(cardList));
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        cardListService.deleteOne(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardList> updateOne(@PathVariable Long id,
            @RequestBody CardList cardList) {
        try {
            CardList updatedCardList = cardListService.updateOne(id, cardList);
            template.convertAndSend("/topic/lists", cardList);
            return ResponseEntity.ok(updatedCardList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
