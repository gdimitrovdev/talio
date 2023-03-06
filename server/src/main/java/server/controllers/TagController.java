package server.controllers;

import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.TagService;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController
{
    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Tag> getOne(@PathVariable("id") Long id) {
        return tagService.getOne(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id)
    {
            tagService.delete(id);
    }

    @GetMapping("")
    @ResponseBody
    public List<Tag> getAll()
    {
        return tagService.getAll();
    }

    @PostMapping("")
    @ResponseBody
    public ResponseEntity<Tag> create(@RequestBody Tag tag)
    {
        return tagService.create(tag);
    }

}
