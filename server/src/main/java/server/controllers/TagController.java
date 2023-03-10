package server.controllers;

import commons.CardList;
import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.TagService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

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
        Optional<Tag> optionalTag = tagService.getOne(id);
        if (!optionalTag.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(optionalTag.get());
    }

    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Tag> createOne(@RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.createOne(tag));
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id) {
        tagService.deleteOne(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateOne(@PathVariable Long id, @RequestBody Tag tag) {
        try {
            Tag updatedTag = tagService.updateOne(id, tag);
            return ResponseEntity.ok(updatedTag);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
