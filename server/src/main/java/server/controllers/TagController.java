package server.controllers;

import commons.Card;
import commons.Tag;
import commons.Topics;
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
import server.services.BoardService;
import server.services.CardService;
import server.services.TagService;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final SimpMessagingTemplate template;

    private final TagService tagService;
    private final BoardService boardService;
    private final CardService cardService;

    @Inject
    public TagController(TagService tagService, BoardService boardService,
            CardService cardService, SimpMessagingTemplate template) {
        this.tagService = tagService;
        this.boardService = boardService;
        this.cardService = cardService;
        this.template = template;
    }

    @GetMapping(path = {"", "/"})
    @ResponseBody
    public List<Tag> getMany() {
        return tagService.getMany();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Tag> getOne(@PathVariable("id") Long id) {
        Optional<Tag> optionalTag = tagService.getOne(id);
        if (!optionalTag.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(optionalTag.get());
    }

    /**
     * Sends websocket update to Topics.TAGS
     *
     * @param tag
     * @return
     */
    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Tag> createOne(@RequestBody Tag tag) {
        try {
            Tag newTag = tagService.createOne(tag);
            template.convertAndSend(Topics.TAGS.toString(), newTag);
            return ResponseEntity.ok(newTag);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO send update about this using long-polling and don't send updates to the cards

    /**
     * Sends update to Topic.CARDS for each card that has this tag
     * Also removes tag from any cards that have it
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity deleteOne(@PathVariable("id") Long id) {
        try {
            List<Card> affectedCards =
                    cardService.getMany().stream().filter(c -> c.getTags().stream()
                            .anyMatch(t -> t.getId().equals(id))).toList();
            for (Card card : affectedCards) {
                cardService.removeTagFromCard(id, card.getId());
                template.convertAndSend(Topics.CARDS.toString(), cardService.getOne(card.getId()));
            }
            tagService.deleteOne(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO don't send updates to the cards

    /**
     * Sends websocket updates to Topics.TAGS and Topics.CARD for each card that has this tag
     *
     * @param id
     * @param tag
     * @return
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Tag> updateOne(@PathVariable Long id,
            @RequestBody Tag tag) {
        try {
            Tag updated = tagService.updateOne(id, tag);
            List<Card> affectedCards =
                    cardService.getMany().stream().filter(c -> c.getTags().stream()
                            .anyMatch(t -> t.getId().equals(id))).toList();
            for (Card card : affectedCards) {
                template.convertAndSend(Topics.CARDS.toString(), cardService.getOne(card.getId()));
            }
            template.convertAndSend(Topics.TAGS.toString(), updated);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
