package server.controllers;

import commons.Card;
import commons.Tag;
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
import server.services.BoardService;
import server.services.CardService;
import server.services.TagService;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private SimpMessagingTemplate template;

    private final TagService tagService;
    private final BoardService boardService;
    private final CardService cardService;

    public TagController(TagService tagService, BoardService boardService,
            CardService cardService) {
        this.tagService = tagService;
        this.boardService = boardService;
        this.cardService = cardService;
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

    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Tag> createOne(@RequestBody Tag tag) {
        try {
            Tag newTag = tagService.createOne(tag);
            template.convertAndSend("topics/tags", newTag);
            return ResponseEntity.ok(newTag);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity deleteOne(@PathVariable("id") Long id) {
        try {
            List<Card> affectedCards =
                    cardService.getMany().stream().filter(c -> c.getTags().stream()
                            .anyMatch(t -> t.getId().equals(id))).toList();
            tagService.deleteOne(id);
            for (Card card : affectedCards) {
                template.convertAndSend("/topic/cards", cardService.getOne(card.getId()));
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity updateOne(@PathVariable Long id,
            @RequestBody Tag tag) {
        try {
            List<Card> affectedCards =
                    cardService.getMany().stream().filter(c -> c.getTags().stream()
                            .anyMatch(t -> t.getId().equals(id))).toList();
            tagService.updateOne(id, tag);
            for (Card card : affectedCards) {
                template.convertAndSend("/topic/cards", cardService.getOne(card.getId()));
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }
}
