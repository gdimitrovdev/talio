package server.controllers;

import commons.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.BoardService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping(path = { "", "/" })
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
    public ResponseEntity<Board> getOneByHash(@PathVariable("code")String code){
        Optional<Board> optionalBoard = boardService.getOneByCode(code);
        if (!optionalBoard.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(optionalBoard.get());

    }


    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Board> createOne(@RequestBody Board board) {
        return ResponseEntity.ok(boardService.createOne(board));
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        boardService.deleteOne(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> updateOne(@PathVariable Long id, @RequestBody Board board) {
        try {
            Board updatedBoard = boardService.updateOne(id, board);
            return ResponseEntity.ok(updatedBoard);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
