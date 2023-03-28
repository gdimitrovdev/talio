package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.database.BoardRepository;
import server.database.CardListRepository;
import server.database.CardRepository;
import server.database.SubtaskRepository;
import server.database.TagRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private BoardController boardController;
    @Autowired
    private CardController cardController;
    @Autowired
    private CardListController cardListController;
    @Autowired
    private SubtaskController subtaskController;
    @Autowired
    private TagController tagController;

    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;
    private final SubtaskRepository subtaskRepository;
    private final CardListRepository cardListRepository;
    private final CardRepository cardRepository;

    public AdminController(BoardRepository boardRepository, TagRepository tagRepository,
            SubtaskRepository subtaskRepository, CardListRepository cardListRepository,
            CardRepository cardRepository) {
        this.boardRepository = boardRepository;
        this.tagRepository = tagRepository;
        this.subtaskRepository = subtaskRepository;
        this.cardListRepository = cardListRepository;
        this.cardRepository = cardRepository;
    }

    @GetMapping(path = {"", "/"})
    @ResponseBody
    public String get() {
        return """
                Avaliable endpoints:
                <ul>
                <li>/api/admin/clear - Clears the database</li>
                <li>/api/admin/fill - Fills the database with example data</li>
                <li>/api/admin/refill - Clears the database and fills it with example data</li>
                <li>/api/admin/boards - Lists all boards and their invite codes</li>
                </ul>
                """;
    }

    @GetMapping(path = {"/clear", "/clear/"})
    @ResponseBody
    public ResponseEntity clear() {
        try {
            subtaskRepository.deleteAll();
            tagRepository.deleteAll();
            cardRepository.deleteAll();
            cardListRepository.deleteAll();
            boardRepository.deleteAll();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity fill() {
        try {
            //Board board1 = boardController.createOne(new Board()).getBody();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
