package server.controllers;

import commons.CardList;
import commons.Subtask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.SubtaskService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskController {

    private final SubtaskService subtaskService;

    public SubtaskController(SubtaskService subtaskService) {
        this.subtaskService = subtaskService;
    }

    @GetMapping(path = { "", "/" })
    @ResponseBody
    public List<Subtask> getMany() {
        return subtaskService.getMany();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Subtask> getOne(@PathVariable("id") Long id) {
        Optional<Subtask> optionalSubtask = subtaskService.getOne(id);
        if (!optionalSubtask.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(optionalSubtask.get());
    }

    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Subtask> createOne(@RequestBody Subtask subtask) {
        return ResponseEntity.ok(subtaskService.createOne(subtask));
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        subtaskService.deleteOne(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subtask> updateOne(@PathVariable Long id, @RequestBody Subtask subtask) {
        try {
            Subtask updatedSubtask = subtaskService.updateOne(id, subtask);
            return ResponseEntity.ok(updatedSubtask);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
