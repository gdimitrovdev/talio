package server.controllers;

import commons.Card;
import commons.CardList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
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
        return optionalCard.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());

    }

    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Card> createOne(@RequestBody Card card) {
        try {
            Card newCard = cardService.createOne(card);
            template.convertAndSend("topics/cards", newCard);
            return ResponseEntity.ok(newCard);
        } catch (Exception e) {
            System.out.println(e);
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
            System.out.println(e);
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
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @SendTo("/topic/lists")
    public CardList sendList(CardList list) {
        return list;
    }

    @GetMapping("/move-to-list-after-card/{id}/{listId}/{afterCardId}")
    @ResponseBody
    public ResponseEntity<CardList> moveToListAfterCard(@PathVariable Long id,
            @PathVariable Long listId,
            @PathVariable Long afterCardId) {
        try {
            Long originalListId = cardService.getOne(id).get().getList().getId();
            cardService.moveToListAfterCard(id, listId, afterCardId);
            CardList list = cardListService.getOne(listId).get();
            System.out.println("Cowabunga!");
            template.convertAndSend("/topic/lists", list);
            //sendList(list);
            System.out.println("Cowabunga 2!");
            if (!originalListId.equals(list.getId())) {
                System.out.println("Cowabunga 3!");
                template.convertAndSend("/topic/lists",
                        cardListService.getOne(originalListId).get());
                //sendList(cardListService.getOne(originalListId).get());
                System.out.println("Cowabunga 4!");
            }
            //System.out.println(list);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
        //return ResponseEntity.ok(cardListService.getOne(listId).get());
    }

    @GetMapping("/move-to-list-last/{id}/{listId}")
    @ResponseBody
    public ResponseEntity<CardList> moveToListLast(@PathVariable Long id,
            @PathVariable Long listId) {
        try {
            Long originalListId = cardService.getOne(id).get().getList().getId();
            cardService.moveToListLast(id, listId);
            CardList list = cardListService.getOne(listId).get();
            template.convertAndSend("/topic/lists", list);
            if (!originalListId.equals(list.getId())) {
                template.convertAndSend("/topic/lists",
                        cardListService.getOne(originalListId).get());
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/add-tag-to-card/{id}/{tagId}")
    @ResponseBody
    public ResponseEntity<Card> addTagToCard(@PathVariable Long id, @PathVariable Long tagId) {
        try {
            Card updatedCard = cardService.addTagToCard(tagId, id);
            template.convertAndSend("/topic/cards", updatedCard);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/remove-tag-from-card/{id}/{tagId}")
    @ResponseBody
    public ResponseEntity<Card> removeTagFromCard(@PathVariable Long id, @PathVariable Long tagId) {
        try {
            Card updatedCard = cardService.removeTagFromCard(tagId, id);
            template.convertAndSend("/topic/cards", updatedCard);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }
}
