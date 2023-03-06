package server.controllers;

import commons.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.BoardService;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private BoardService boardService;

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
        return boardService.getOne(id);
    }

    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Board> createOne(@RequestBody Board board) {
        return boardService.createOne(board);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        boardService.deleteOne(id);
    }

}
