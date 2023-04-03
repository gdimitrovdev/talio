package server.controllers;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Subtask;
import commons.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.services.BoardService;
import server.services.CardListService;
import server.services.CardService;
import server.services.SubtaskService;
import server.services.TagService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final BoardService boardService;
    private final TagService tagService;
    private final SubtaskService subtaskService;
    private final CardListService cardListService;
    private final CardService cardService;

    @Inject
    public AdminController(BoardService boardService, TagService tagService,
            SubtaskService subtaskService, CardListService cardListService,
            CardService cardService) {
        this.boardService = boardService;
        this.tagService = tagService;
        this.subtaskService = subtaskService;
        this.cardListService = cardListService;
        this.cardService = cardService;
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
                + boardService.getMany().stream().map(b -> "<tr><td style=\"border: 1px solid black\">"
                    + b.getId() + "</td><td style=\"border: 1px solid black\">" + b.getName() + "</td><td style=\"border: 1px solid black\">"
                    + b.getCode() + "</td><td style=\"border: 1px solid black\">" + b.getReadOnlyCode() + "</td></tr>"
                ).collect(Collectors.joining("\n")) + "</table>";
    }

    @GetMapping(path = {"/clear", "/clear/"})
    @ResponseBody
    public ResponseEntity clear() {
        try {
            subtaskService.deleteMany();
            cardService.deleteMany();
            tagService.deleteMany();
            cardListService.deleteMany();
            boardService.deleteMany();
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
            List<String> defaultPresets = new ArrayList<>();
            defaultPresets.add("#ffffff/#000000");
            defaultPresets.add("#ff0008/#000000");
            defaultPresets.add("#abffc3/#004714");
            Board team69Board =
                    boardService.createOne(new Board("Team 69", "", "",
                                    "#/#000000", "#dedede/#000000", defaultPresets, 1));
            Tag b1urgentTag =
                    tagService.createOne(new Tag("Urgent", "#FF0000/#FFFFFF", team69Board));
            Tag b1bugTag =
                    tagService.createOne(new Tag("Bug", "#AA0000/#FFFFFF", team69Board));
            Tag b1documentationTag = tagService.createOne(new Tag("Documentation",
                    "#0000AA/#FFFFFF", team69Board));
            Tag b1featureTag = tagService.createOne(new Tag("Feature",
                    "#00AA00/#FFFFFF", team69Board));
            CardList b1toDoCardList =
                    cardListService.createOne(new CardList("To Do", team69Board));
            CardList b1doingCardList = cardListService.createOne(new CardList("Doing",
                    team69Board));
            CardList b1doneCardList = cardListService.createOne(new CardList("Done",
                    team69Board));
            CardList b1CardList4 = cardListService.createOne(new CardList("Card List 4",
                    team69Board));
            CardList b1CardList5 = cardListService.createOne(new CardList("Card List 5",
                    team69Board));
            Card b1DuplicateTagWarningCard = cardService.createOne(new Card("Warning message "
                    + "for duplicate tags", "", "", null, 1));
            cardService.moveToListLast(b1DuplicateTagWarningCard.getId(),
                    b1toDoCardList.getId());
            cardService.addTagToCard(b1DuplicateTagWarningCard.getId(), b1featureTag.getId());
            Card b1TestClientControllersCard = cardService.createOne(new Card("Test Client "
                    + "Controllers", "", "", null, 1 ));
            cardService.moveToListLast(b1TestClientControllersCard.getId(),
                    b1toDoCardList.getId());
            cardService.addTagToCard(b1TestClientControllersCard.getId(), b1featureTag.getId());
            cardService.addTagToCard(b1TestClientControllersCard.getId(), b1bugTag.getId());
            cardService.addTagToCard(b1TestClientControllersCard.getId(), b1urgentTag.getId());
            cardService.addTagToCard(b1TestClientControllersCard.getId(),
                    b1documentationTag.getId());

            Subtask b1Subtask1 = subtaskService.createOne(new Subtask("Subtask 1",
                    b1TestClientControllersCard, true));
            Subtask b1Subtask2 = subtaskService.createOne(new Subtask("Subtask 2",
                    b1TestClientControllersCard, false));
            Subtask b1Subtask3 = subtaskService.createOne(new Subtask("Subtask 3",
                    b1TestClientControllersCard, false));
            Subtask b1Subtask4 = subtaskService.createOne(new Subtask("Subtask 4",
                    b1TestClientControllersCard, false));
            Card b1Card3 = cardService.createOne(new Card("Card 3", "", "", null, 1));
            cardService.moveToListLast(b1Card3.getId(), b1doingCardList.getId());
            Card b1Card4 = cardService.createOne(new Card("Card 4", "", "", null, 1));
            cardService.moveToListLast(b1Card4.getId(), b1doingCardList.getId());
            Card b1Card6 = cardService.createOne(new Card("Card 6", "", "", null, 1));
            cardService.moveToListLast(b1Card6.getId(), b1doingCardList.getId());
            Card b1Card5 = cardService.createOne(new Card("Card 5", "", "", null, 1));
            cardService.moveToListAfterCard(b1Card5.getId(), b1doingCardList.getId(),
                    b1Card4.getId());
            Card b1Card7 = cardService.createOne(new Card("Card 7", "", "", null, 1));
            cardService.moveToListLast(b1Card7.getId(), b1CardList4.getId());
            Card b1Card8 = cardService.createOne(new Card("Card 8", "", "", null, 1));
            cardService.moveToListLast(b1Card8.getId(), b1CardList5.getId());

            Board studyingBoard =


            Board studyingBoard =
                    boardService.createOne(new Board("Studying", "", "", "#bababa/#000000", "#dedede/#000000", defaultPresets, 1));
            Board team99Board =
                    boardService.createOne(new Board("Team 99", "", "", "#bababa/#000000", "#dedede/#000000", defaultPresets, 1));

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }
}
