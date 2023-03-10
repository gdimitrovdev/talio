package server.controllers;

import commons.Subtask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.SubtaskService;

import java.util.List;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskController {

    private SubtaskService subtaskService;

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
        return subtaskService.getOne(id);
    }

    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Subtask> createOne(@RequestBody Subtask subtask) {
        return subtaskService.createOne(subtask);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        subtaskService.deleteOne(id);
    }

}
