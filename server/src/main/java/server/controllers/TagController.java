package server.controllers;

import commons.Tag;
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
import server.services.TagService;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(path = {"", "/"})
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

    @PostMapping(path = {"", "/"})
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
