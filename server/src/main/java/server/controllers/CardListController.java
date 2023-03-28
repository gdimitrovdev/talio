package server.controllers;

import commons.Board;
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
import server.services.BoardService;
import server.services.CardListService;

@RestController
@RequestMapping("/api/lists")
public class CardListController {
    @Autowired
    private SimpMessagingTemplate template;

    private final CardListService cardListService;
    private final BoardService boardService;

    public CardListController(CardListService cardListService, BoardService boardService) {
        this.cardListService = cardListService;
        this.boardService = boardService;
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

    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<CardList> createOne(@RequestBody CardList cardList) {
        try {
            var newCardList = cardListService.createOne(cardList);
            template.convertAndSend("/topic/lists", newCardList);
            return ResponseEntity.ok(newCardList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Board> deleteOne(@PathVariable("id") Long id) {
        try {
            var boardId = cardListService.getOne(id).get().getBoard().getId();
            cardListService.deleteOne(id);
            var board = boardService.getOne(boardId).get();
            template.convertAndSend("/topic/boards", board);
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CardList> updateOne(@PathVariable Long id,
            @RequestBody CardList cardList) {
        try {
            CardList updatedCardList = cardListService.updateOne(id, cardList);
            template.convertAndSend("/topic/lists", updatedCardList);
            return ResponseEntity.ok(updatedCardList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
