package server.controllers;

import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CardListService;

import java.util.List;

@RestController
@RequestMapping("/api/card-lists")
public class CardListController {

    private final CardListService cardListService;

    public CardListController(CardListService cardListService) {
        this.cardListService = cardListService;
    }

    @GetMapping(path = { "", "/" })
    @ResponseBody
    public List<CardList> getMany() {
        return cardListService.getMany();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CardList> getOne(@PathVariable("id") Long id) {
        return cardListService.getOne(id);
    }

    @PostMapping(path= { "", "/" })
    @ResponseBody
    public ResponseEntity<CardList> createOne(@RequestBody CardList cardList) {
        return cardListService.createOne(cardList);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        cardListService.deleteOne(id);
    }

    @PutMapping("/{id}")
    public CardList update(@PathVariable Long id, @RequestBody CardList cardList) {
        cardList.setId(id);
        return cardListService.update(cardList);
    }

}
