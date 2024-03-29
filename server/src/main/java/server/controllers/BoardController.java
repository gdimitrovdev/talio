package server.controllers;

import commons.Board;
import commons.Topics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.inject.Inject;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.context.request.async.DeferredResult;
import server.services.BoardService;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final SimpMessagingTemplate template;

    private final BoardService boardService;

    @Inject
    public BoardController(BoardService boardService, SimpMessagingTemplate template) {
        this.boardService = boardService;
        this.template = template;
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
        return optionalBoard.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());

    }

    @GetMapping("/by-code/{code}")
    @ResponseBody
    public ResponseEntity<Board> getOneByCode(@PathVariable("code") String code) {
        try {
            Board board = boardService.getOneByCode(code).get();
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private Map<Object, Consumer<Board>> listeners = new HashMap<>();

    @GetMapping("/deleted")
    public DeferredResult<ResponseEntity<Board>> getDeletions() {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Board>>(1000L, noContent);

        var key = new Object();
        listeners.put(key, board -> {
            res.setResult(ResponseEntity.ok(board));
        });

        res.onCompletion(() -> listeners.remove(key));

        return res;
    }

    /**
     * Also sends update to Topics.BOARDS
     *
     * @param board
     * @return
     */
    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Board> createOne(@RequestBody Board board) {
        try {
            var newBoard = boardService.createOne(board);
            template.convertAndSend(Topics.BOARDS.toString(), newBoard);
            return ResponseEntity.ok(newBoard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO Use long-polling to notify the users of this
    @DeleteMapping("/{id}")
    public ResponseEntity deleteOne(@PathVariable("id") Long id) {
        try {
            listeners.forEach((k, l) -> l.accept(boardService.getOne(id).get()));
            template.convertAndSend("/topic/boards/deleted", boardService.getOne(id).get());
            boardService.deleteOne(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Also sends update to Topic.BOARDS
     *
     * @param id
     * @param board
     * @return
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Board> updateOne(@PathVariable Long id,
            @RequestBody Board board) {
        try {
            Board updatedBoard = boardService.updateOne(id, board);
            template.convertAndSend(Topics.BOARDS.toString(), updatedBoard);
            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
