package server.controllers;

import commons.Subtask;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.services.SubtaskService;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskController {

    private final SubtaskService subtaskService;

    public SubtaskController(SubtaskService subtaskService) {
        this.subtaskService = subtaskService;
    }

    @GetMapping(path = {"", "/"})
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

    @PostMapping(path = {"", "/"})
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
