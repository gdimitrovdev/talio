package server.controllers;

import commons.Card;
import commons.Subtask;
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
import commons.Topics;
import server.services.CardService;
import server.services.SubtaskService;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskController {
    private final SimpMessagingTemplate template;

    private final SubtaskService subtaskService;
    private final CardService cardService;

    @Inject
    public SubtaskController(SubtaskService subtaskService, CardService cardService,
            SimpMessagingTemplate template) {
        this.subtaskService = subtaskService;
        this.cardService = cardService;
        this.template = template;
    }

    @GetMapping(path = {"", "/"})
    @ResponseBody
    public List<Subtask> getMany() {
        return subtaskService.getMany();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Subtask> getOne(@PathVariable("id") Long id) {
        Optional<Subtask> optionalSubtask = subtaskService.getOne(id);
        if (!optionalSubtask.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(optionalSubtask.get());
    }

    /**
     * Also sends an update to Topics.SUBTASKS
     *
     * @param subtask
     * @return
     */
    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Subtask> createOne(@RequestBody Subtask subtask) {
        try {
            Subtask newSubtask = subtaskService.createOne(subtask);
            template.convertAndSend(Topics.CARDS.toString(), newSubtask.getCard());
            return ResponseEntity.ok(newSubtask);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO Make this send a Topics.SUBTASK update

    /**
     * Also sends an update to the card that the subtasks was in
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Card> deleteOne(@PathVariable("id") Long id) {
        try {
            var cardId = subtaskService.getOne(id).get().getCard().getId();
            subtaskService.deleteOne(id);
            var card = cardService.getOne(cardId).get();
            template.convertAndSend(Topics.CARDS.toString(), card);
            return ResponseEntity.ok(card);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Also sends an update to Topics.CARDS
     *
     * @param id
     * @param subtask
     * @return
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Subtask> updateOne(@PathVariable Long id,
            @RequestBody Subtask subtask) {
        try {
            Subtask updatedSubtask = subtaskService.updateOne(id, subtask);
            template.convertAndSend(Topics.CARDS.toString(), updatedSubtask.getCard());
            return ResponseEntity.ok(updatedSubtask);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
