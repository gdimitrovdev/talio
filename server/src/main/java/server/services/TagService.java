package server.services;


import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.TagRepository;

import java.util.List;

@Service
public class TagService
{
    private TagRepository tagRepository;

    public TagService(TagRepository tagRepository)
    {
        this.tagRepository = tagRepository;
    }

    public ResponseEntity<Tag> getOne(Long id)
    {
        if(!tagRepository.existsById(id))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(tagRepository.getById(id));
    }

    public List<Tag> getAll()
    {
        return tagRepository.findAll();
    }

    public void delete(Long id)
    {
        tagRepository.deleteById(id);
    }

    public ResponseEntity<Tag> create(Tag tag)
    {
        Tag ans=tagRepository.save(tag);
        return ResponseEntity.ok(ans);
    }
}
