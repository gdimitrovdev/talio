package server.services;

import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.TagRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getMany() {
        return tagRepository.findAll();
    }

    public ResponseEntity<Tag> getOne(Long id) {
        if(id < 0 || !tagRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(tagRepository.findById(id).get());
    }

    public ResponseEntity<Tag> createOne(Tag tag) {
        Tag newTag = tagRepository.save(tag);
        return ResponseEntity.ok(newTag);
    }

    public void deleteOne(Long id) {
        tagRepository.deleteById(id);
    }

    public Tag update(Tag tag)
    {
        Tag existingTag = tagRepository.findById(tag.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        existingTag.setTitle(tag.getTitle());
        existingTag.setColor(tag.getColor());
        existingTag.setBoard(tag.getBoard());
        existingTag.setCards(tag.getCards());

        return tagRepository.save(existingTag);
    }

}