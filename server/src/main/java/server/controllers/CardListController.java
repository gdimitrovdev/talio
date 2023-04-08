package server.controllers;

import commons.Board;
import commons.CardList;
import commons.Topics;
import java.util.List;
import java.util.Optional;
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
import server.services.CardListService;

@RestController
@RequestMapping("/api/lists")
public class CardListController {
    private final SimpMessagingTemplate template;

    private final CardListService cardListService;
    private final BoardService boardService;

    public CardListController(CardListService cardListService, BoardService boardService,
            SimpMessagingTemplate template) {
        this.cardListService = cardListService;
        this.boardService = boardService;
        this.template = template;
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
        return optionalCardList.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());

    }

    // TODO don't send update on Topic.BOARDS

    /**
     * Also sends update to Topics.BOARDS and Topics.LISTS
     *
     * @param cardList
     * @return
     */
    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<CardList> createOne(@RequestBody CardList cardList) {
        try {
            var newCardList = cardListService.createOne(cardList);
            template.convertAndSend(Topics.BOARDS.toString(), newCardList.getBoard());
            return ResponseEntity.ok(newCardList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO send delete update using long-polling

    /**
     * Also sends update to Topics.BOARDS
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Board> deleteOne(@PathVariable("id") Long id) {
        try {
            var boardId = cardListService.getOne(id).get().getBoard().getId();
            cardListService.deleteOne(id);
            var board = boardService.getOne(boardId).get();
            template.convertAndSend(Topics.BOARDS.toString(), board);
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Also sends update to Topics.LISTS
     *
     * @param id
     * @param cardList
     * @return
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CardList> updateOne(@PathVariable Long id,
            @RequestBody CardList cardList) {
        try {
            CardList updatedCardList = cardListService.updateOne(id, cardList);
            template.convertAndSend(Topics.LISTS.toString(), updatedCardList);
            return ResponseEntity.ok(updatedCardList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO Remove this
    @GetMapping("/refresh-list/{id}")
    @ResponseBody
    public ResponseEntity<CardList> refreshList(@PathVariable Long id) {
        template.convertAndSend("/topic/lists", cardListService.getOne(id).get());
        return ResponseEntity.ok(cardListService.getOne(id).get());
    }

}
