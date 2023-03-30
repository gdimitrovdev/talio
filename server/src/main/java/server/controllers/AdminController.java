package server.controllers;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Subtask;
import commons.Tag;
import java.util.stream.Collectors;
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
@RequestMapping("/admin")
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

    @GetMapping(path = {"boards", "boards/"})
    @ResponseBody
    public String getBoards() {
        return """
                Avaliable boards:
                <table style="border: 1px solid black; border-collapse: collapse">
                    <tr>
                        <th style="border: 1px solid black">Id</th>
                        <th style="border: 1px solid black">Name</th>
                        <th style="border: 1px solid black">Join Code</th>
                        <th style="border: 1px solid black">Read-Only Join Code</th>
                    </tr>
                """
                + boardRepository.findAll().stream().map(b -> "<tr><td style=\"border: 1px solid black\">"
                    + b.getId() + "</td><td style=\"border: 1px solid black\">" + b.getName() + "</td><td style=\"border: 1px solid black\">"
                    + b.getCode() + "</td><td style=\"border: 1px solid black\">" + b.getReadOnlyCode() + "</td></tr>"
                ).collect(Collectors.joining("\n")) + "</table>";
    }

    @GetMapping(path = {"/clear", "/clear/"})
    @ResponseBody
    public ResponseEntity clear() {
        try {
            subtaskRepository.deleteAll();
            cardRepository.deleteAll();
            tagRepository.deleteAll();
            cardListRepository.deleteAll();
            boardRepository.deleteAll();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = {"/refill", "/refill/"})
    @ResponseBody
    public ResponseEntity refill() {
        if (clear().equals(ResponseEntity.badRequest().build())) {
            return ResponseEntity.badRequest().build();
        }
        if (fill().equals(ResponseEntity.badRequest().build())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = {"/fill", "/fill/"})
    @ResponseBody
    public ResponseEntity fill() {
        try {
            // TODO fill this with proper example data
            // Colors in format #Background/#Foreground

            Board team69Board =
                    boardController.createOne(new Board("Team 69", "", "", "#FFFFFF/#000000"))
                            .getBody();
            Tag b1urgentTag =
                    tagController.createOne(new Tag("Urgent", "#FF0000/#FFFFFF", team69Board))
                            .getBody();
            Tag b1bugTag =
                    tagController.createOne(new Tag("Bug", "#AA0000/#FFFFFF", team69Board))
                            .getBody();
            Tag b1documentationTag = tagController.createOne(new Tag("Documentation",
                    "#0000AA/#FFFFFF", team69Board)).getBody();
            Tag b1featureTag = tagController.createOne(new Tag("Feature",
                    "#00AA00/#FFFFFF", team69Board)).getBody();
            CardList b1toDoCardList =
                    cardListController.createOne(new CardList("To Do", team69Board)).getBody();
            CardList b1doingCardList = cardListController.createOne(new CardList("Doing",
                    team69Board)).getBody();
            CardList b1doneCardList = cardListController.createOne(new CardList("Done",
                    team69Board)).getBody();
            CardList b1CardList4 = cardListController.createOne(new CardList("Card List 4",
                    team69Board)).getBody();
            CardList b1CardList5 = cardListController.createOne(new CardList("Card List 5",
                    team69Board)).getBody();
            Card b1DuplicateTagWarningCard = cardController.createOne(new Card("Warning message "
                    + "for duplicate tags", "", "", null)).getBody();
            cardController.moveToListLast(b1DuplicateTagWarningCard.getId(),
                    b1toDoCardList.getId());
            cardController.addTagToCard(b1DuplicateTagWarningCard.getId(), b1featureTag.getId());
            Card b1TestClientControllersCard = cardController.createOne(new Card("Test Client "
                    + "Controllers", "", "", null)).getBody();
            cardController.moveToListLast(b1TestClientControllersCard.getId(),
                    b1toDoCardList.getId());
            cardController.addTagToCard(b1TestClientControllersCard.getId(), b1featureTag.getId());
            cardController.addTagToCard(b1TestClientControllersCard.getId(), b1bugTag.getId());
            cardController.addTagToCard(b1TestClientControllersCard.getId(), b1urgentTag.getId());
            cardController.addTagToCard(b1TestClientControllersCard.getId(),
                    b1documentationTag.getId());
            Subtask b1Subtask1 = subtaskController.createOne(new Subtask("Subtask 1",
                    b1TestClientControllersCard, true)).getBody();
            Subtask b1Subtask2 = subtaskController.createOne(new Subtask("Subtask 2",
                    b1TestClientControllersCard, false)).getBody();
            Subtask b1Subtask3 = subtaskController.createOne(new Subtask("Subtask 3",
                    b1TestClientControllersCard, false)).getBody();
            Subtask b1Subtask4 = subtaskController.createOne(new Subtask("Subtask 4",
                    b1TestClientControllersCard, false)).getBody();
            Card b1Card3 = cardController.createOne(new Card("Card 3", "", "", null)).getBody();
            cardController.moveToListLast(b1Card3.getId(), b1doingCardList.getId());
            Card b1Card4 = cardController.createOne(new Card("Card 4", "", "", null)).getBody();
            cardController.moveToListLast(b1Card4.getId(), b1doingCardList.getId());
            Card b1Card6 = cardController.createOne(new Card("Card 6", "", "", null)).getBody();
            cardController.moveToListLast(b1Card6.getId(), b1doingCardList.getId());
            Card b1Card5 = cardController.createOne(new Card("Card 5", "", "", null)).getBody();
            cardController.moveToListAfterCard(b1Card5.getId(), b1doingCardList.getId(),
                    b1Card4.getId());
            Card b1Card7 = cardController.createOne(new Card("Card 7", "", "", null)).getBody();
            cardController.moveToListLast(b1Card7.getId(), b1CardList4.getId());
            Card b1Card8 = cardController.createOne(new Card("Card 8", "", "", null)).getBody();
            cardController.moveToListLast(b1Card8.getId(), b1CardList5.getId());

            Board studyingBoard =
                    boardController.createOne(new Board("Studying", "", "", "#FFFFFF")).getBody();
            Board team99Board =
                    boardController.createOne(new Board("Team 99", "", "", "#FFFFFF")).getBody();

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }
}
