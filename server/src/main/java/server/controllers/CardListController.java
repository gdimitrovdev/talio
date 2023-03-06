package server.controllers;


import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CardListService;

import java.util.List;

@RestController
@RequestMapping("/api/cardList")
public class CardListController
{
    private CardListService cardListService;

    public CardListController(CardListService cardListService) {
        this.cardListService = cardListService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CardList> getOne(@PathVariable("id") Long id)
    {
        return cardListService.getOne(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id)
    {
        cardListService.deleteOne(id);
    }

    @GetMapping(path = {""})
    @ResponseBody
    public List<CardList> getMany() {
        return cardListService.getMany();
    }

    @PostMapping(path= {""})
    @ResponseBody
    public ResponseEntity<CardList> create(@RequestBody CardList cardList)
    {
        return cardListService.create(cardList);
    }


}
