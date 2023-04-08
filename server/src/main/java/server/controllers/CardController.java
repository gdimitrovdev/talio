package server.controllers;

import commons.Card;
import commons.CardList;
import commons.Topics;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
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
    private final SimpMessagingTemplate template;

    private final CardListService cardListService;
    private final CardService cardService;

    @Inject
    public CardController(CardListService cardListService, CardService cardService,
            SimpMessagingTemplate template) {
        this.cardListService = cardListService;
        this.cardService = cardService;
        this.template = template;
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

    // TODO send update to Topic.CARDS and not Topic.LISTS

    /**
     * Also sends update to Topic.LISTS
     *
     * @param card
     * @return
     */
    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Card> createOne(@RequestBody Card card) {
        try {
            Card newCard = cardService.createOne(card);
            template.convertAndSend(Topics.LISTS.toString(), newCard.getList());
            //template.convertAndSend(Topics.CARDS.toString(), newCard);
            return ResponseEntity.ok(newCard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Also sends update to Topic.LISTS
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CardList> deleteOne(@PathVariable("id") Long id) {
        try {
            var cardListId = cardService.getOne(id).get().getList().getId();
            cardService.deleteOne(id);
            var cardList = cardListService.getOne(cardListId).get();
            template.convertAndSend(Topics.LISTS.toString(), cardList);
            return ResponseEntity.ok(cardList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Also sends update to Topics.CARDS
     *
     * @param id
     * @param card
     * @return
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Card> updateOne(@PathVariable Long id,
            @RequestBody Card card) {
        try {
            Card updatedCard = cardService.updateOne(id, card);
            template.convertAndSend(Topics.CARDS.toString(), updatedCard);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/move-to-list-after-card/{id}/{listId}/{afterCardId}")
    @ResponseBody
    public ResponseEntity<CardList> moveToListAfterCard(@PathVariable Long id,
            @PathVariable Long listId,
            @PathVariable Long afterCardId) {
        try {
            CardList newList = cardService.moveToListAfterCard(id, listId, afterCardId).getList();

            template.convertAndSend(Topics.LISTS.toString(), newList);

            return ResponseEntity.ok(newList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/move-to-list-last/{id}/{listId}")
    @ResponseBody
    public ResponseEntity<CardList> moveToListLast(@PathVariable Long id,
            @PathVariable Long listId) {
        try {
            CardList newList = cardService.moveToListLast(id, listId).getList();

            template.convertAndSend(Topics.LISTS.toString(), newList);

            return ResponseEntity.ok(newList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/add-tag-to-card/{id}/{tagId}")
    @ResponseBody
    public ResponseEntity<Card> addTagToCard(@PathVariable Long id, @PathVariable Long tagId) {
        try {
            Card updatedCard = cardService.addTagToCard(tagId, id);
            template.convertAndSend(Topics.CARDS.toString(), updatedCard);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/remove-tag-from-card/{id}/{tagId}")
    @ResponseBody
    public ResponseEntity<Card> removeTagFromCard(@PathVariable Long id, @PathVariable Long tagId) {
        try {
            Card updatedCard = cardService.removeTagFromCard(tagId, id);
            template.convertAndSend(Topics.CARDS.toString(), updatedCard);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
