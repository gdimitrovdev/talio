package server.services;

import commons.Board;
import commons.Tag;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final BoardRepository boardRepository;

    @Inject
    public TagService(TagRepository tagRepository, BoardRepository boardRepository) {
        this.tagRepository = tagRepository;
        this.boardRepository = boardRepository;
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
        Optional<Tag> optTag = tagRepository.findById(id);
        if (optTag.isPresent()) {
            Tag t = optTag.get();
            Board b = t.getBoard();
            b.removeTag(t);
            boardRepository.save(b);
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
