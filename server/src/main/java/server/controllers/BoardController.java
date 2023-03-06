package server.controllers;


import commons.Board;
import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.BoardService;

import java.util.List;

@RestController
public class BoardController
{
    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Board> getOne(@PathVariable("id") Long id)
    {
        return BoardService.getOne(id);
    }
    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id)
    {
        BoardService.deleteOne(id);
    }
    @GetMapping(path = {""})
    @ResponseBody
    public List<CardList> getMany() {
        return BoardService.getMany();
    }
    @PostMapping(path= {""})
    @ResponseBody
    public ResponseEntity<CardList> create(@RequestBody CardList cardList)
    {
        return BoardService.create(cardList);
    }
}
