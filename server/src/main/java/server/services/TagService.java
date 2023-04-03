package server.services;

import commons.Tag;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.TagRepository;

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

    /**
     * Ignores cards
     *
     * @param tag
     * @return
     */
    public Tag createOne(Tag tag) {
        tag.getCards().clear();
        return tagRepository.save(tag);
    }

    public void deleteOne(Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
        }
    }

    /**
     * Ignores cards
     *
     * @param id
     * @param tag
     * @return
     * @throws EntityNotFoundException
     */
    public Tag updateOne(Long id, Tag tag) throws EntityNotFoundException {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        existingTag.setTitle(tag.getTitle());
        existingTag.setColor(tag.getColor());
        existingTag.setBoard(tag.getBoard());
        existingTag.setCards(tag.getCards());

        return tagRepository.save(existingTag);
    }

    public void deleteMany() {
        tagRepository.deleteAll();
    }
}
