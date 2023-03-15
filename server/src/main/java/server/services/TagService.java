package server.services;

import commons.Tag;
import org.springframework.stereotype.Service;
import server.database.TagRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getMany() {
        return tagRepository.findAll();
    }

    public Optional<Tag> getOne(Long id) {
        return tagRepository.findById(id);
    }

    public Tag createOne(Tag tag) {
        Tag newTag = tagRepository.save(tag);
        return newTag;
    }

    public void deleteOne(Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
        }
    }

    public Tag updateOne(Long id, Tag tag) throws EntityNotFoundException {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        existingTag.setTitle(tag.getTitle());
        existingTag.setColor(tag.getColor());
        existingTag.setBoard(tag.getBoard());
        existingTag.setCards(tag.getCards());

        return tagRepository.save(existingTag);
    }

}
