package server.controllers;

import commons.Board;
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

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private SimpMessagingTemplate template;

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping(path = {"", "/"})
    @ResponseBody
    public List<Board> getMany() {
        return boardService.getMany();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Board> getOne(@PathVariable("id") Long id) {
        Optional<Board> optionalBoard = boardService.getOne(id);
        if (!optionalBoard.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(optionalBoard.get());
    }

    @GetMapping("/by-code/{code}")
    @ResponseBody
    public ResponseEntity<Board> getOneByCode(@PathVariable("code") String code) {
        try {
            return ResponseEntity.ok(boardService.getOneByCode(code).get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Board> createOne(@RequestBody Board board) {
        try {
            var newBoard = boardService.createOne(board);
            template.convertAndSend("/topic/boards", newBoard);
            return ResponseEntity.ok(newBoard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO Use long-polling to notify the users of this
    @DeleteMapping("/{id}")
    public ResponseEntity deleteOne(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Board> updateOne(@PathVariable Long id,
            @RequestBody Board board) {
        try {
            Board updatedBoard = boardService.updateOne(id, board);
            template.convertAndSend("/topic/boards", updatedBoard);
            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
