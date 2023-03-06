package server.services;


import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.CardListRepository;

import java.util.List;

@Service
public class CardListService
{
  private CardListRepository cardListRepository;

    public CardListService(CardListRepository cardListRepository)
    {
        this.cardListRepository = cardListRepository;
    }
    public ResponseEntity<CardList> getOne(Long id)
    {
        if(!cardListRepository.existsById(id))
        {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cardListRepository.getById(id));
    }

    public List<CardList> getMany()
    {
        return cardListRepository.findAll();
    }

    public void deleteOne(Long id)
    {
        cardListRepository.deleteById(id);
    }

    public ResponseEntity<CardList> create(CardList cardList)
    {
        CardList ans =cardListRepository.save(cardList);
        return ResponseEntity.ok(ans);
    }
}
