package server.controllers;

import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.TagService;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(path = { "", "/" })
    @ResponseBody
    public List<Tag> getMany() {
        return tagService.getMany();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Tag> getOne(@PathVariable("id") Long id) {
        return tagService.getOne(id);
    }

    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Tag> createOne(@RequestBody Tag tag) {
        return tagService.createOne(tag);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
            tagService.deleteOne(id);
    }

}
